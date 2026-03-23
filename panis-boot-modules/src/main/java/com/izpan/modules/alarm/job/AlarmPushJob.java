package com.izpan.modules.alarm.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.modules.alarm.domain.entity.AlarmNotice;
import com.izpan.modules.alarm.domain.entity.AlarmRule;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.domain.vo.AlarmPushVO;
import com.izpan.modules.alarm.service.IAlarmNoticeService;
import com.izpan.modules.alarm.service.IAlarmRuleService;
import com.izpan.modules.alarm.service.IDeviceAlarmService;
import com.izpan.modules.alarm.websocket.AlarmWebSocketHandler;
import com.izpan.modules.equipment.domain.entity.DevicePart;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.service.IDevicePartService;
import com.izpan.modules.equipment.service.IFactoryDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AlarmPushJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long ruleId = Long.parseLong(dataMap.getString("ruleId"));

        log.info("执行报警推送任务: ruleId={}", ruleId);

        try {
            IAlarmRuleService alarmRuleService = SpringContext.getBean(IAlarmRuleService.class);
            IDeviceAlarmService deviceAlarmService = SpringContext.getBean(IDeviceAlarmService.class);
            IAlarmNoticeService alarmNoticeService = SpringContext.getBean(IAlarmNoticeService.class);
            IFactoryDeviceService factoryDeviceService = SpringContext.getBean(IFactoryDeviceService.class);
            IDevicePartService devicePartService = SpringContext.getBean(IDevicePartService.class);
            AlarmWebSocketHandler alarmWebSocketHandler = SpringContext.getBean(AlarmWebSocketHandler.class);
            ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);

            AlarmRule rule = alarmRuleService.getById(ruleId);
            if (rule == null || rule.getRuleStatus() == null || rule.getRuleStatus() != 1) {
                log.info("规则不存在或已禁用: ruleId={}", ruleId);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            LocalTime currentTime = now.toLocalTime();

            if (!isInPushTimeRange(currentTime, rule.getPushStartTime(), rule.getPushEndTime())) {
                log.debug("当前时间不在推送时间范围内: ruleId={}", ruleId);
                return;
            }

            Set<Long> deviceIds = parseDeviceIds(rule.getDeviceIds(), objectMapper);
            Set<Integer> alarmLevels = parseAlarmLevels(rule.getAlarmLevels(), objectMapper);

            if (deviceIds.isEmpty()) {
                log.warn("规则没有配置设备: ruleId={}", ruleId);
                return;
            }

            LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<DeviceAlarm>()
                    .in(DeviceAlarm::getDeviceId, deviceIds)
                    .in(DeviceAlarm::getAlarmLevel, alarmLevels)
                    .eq(DeviceAlarm::getConfirmStatus, 0)
                    .orderByAsc(DeviceAlarm::getAlarmTime);

            List<DeviceAlarm> unconfirmedAlarms = deviceAlarmService.list(queryWrapper);
            log.info("规则 {} 查询到 {} 条未确认报警", ruleId, unconfirmedAlarms.size());

            for (DeviceAlarm alarm : unconfirmedAlarms) {
                if (!shouldPush(alarm, rule, alarmNoticeService)) {
                    continue;
                }
                pushAlarm(alarm, rule, now, factoryDeviceService, devicePartService,
                        alarmWebSocketHandler, alarmNoticeService, objectMapper);
            }

        } catch (Exception e) {
            log.error("报警推送任务执行失败: ruleId={}", ruleId, e);
        }
    }

    private boolean isInPushTimeRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return true;
        }
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    private boolean shouldPush(DeviceAlarm alarm, AlarmRule rule, IAlarmNoticeService alarmNoticeService) {
        Integer pushInterval = rule.getPushInterval();
        if (pushInterval == null || pushInterval <= 0) {
            pushInterval = 5;
        }

        LambdaQueryWrapper<AlarmNotice> queryWrapper = new LambdaQueryWrapper<AlarmNotice>()
                .eq(AlarmNotice::getAlarmId, alarm.getAlarmId())
                .eq(AlarmNotice::getRuleId, rule.getRuleId())
                .orderByDesc(AlarmNotice::getCreateTime)
                .last("LIMIT 1");

        AlarmNotice lastNotice = alarmNoticeService.getOne(queryWrapper);

        if (lastNotice == null) {
            return true;
        }

        LocalDateTime nextPushTime = lastNotice.getCreateTime().plusMinutes(pushInterval);
        return LocalDateTime.now().isAfter(nextPushTime);
    }

    private void pushAlarm(DeviceAlarm alarm, AlarmRule rule, LocalDateTime pushTime,
                           IFactoryDeviceService factoryDeviceService, IDevicePartService devicePartService,
                           AlarmWebSocketHandler alarmWebSocketHandler, IAlarmNoticeService alarmNoticeService,
                           ObjectMapper objectMapper) {
        Set<Long> targetUserIds = parseNotifyTargetIds(rule.getNotifyTargetIds(), objectMapper);
        if (targetUserIds.isEmpty()) {
            log.warn("报警规则没有配置通知目标: ruleId={}", rule.getRuleId());
            return;
        }

        FactoryDevice device = factoryDeviceService.getById(alarm.getDeviceId());
        DevicePart part = alarm.getPartId() != null ? devicePartService.getById(alarm.getPartId()) : null;

        AlarmPushVO pushVO = buildAlarmPushVO(alarm, device, part, pushTime);

        for (Long userId : targetUserIds) {
            if (alarmWebSocketHandler.isUserOnline(userId)) {
                alarmWebSocketHandler.pushAlarmToUser(userId, pushVO);
                saveAlarmNotice(alarm, rule, device, userId, pushTime, alarmNoticeService);
            }
        }
    }

    private AlarmPushVO buildAlarmPushVO(DeviceAlarm alarm, FactoryDevice device, DevicePart part, LocalDateTime pushTime) {
        return AlarmPushVO.builder()
                .alarmId(alarm.getAlarmId())
                .alarmCode(alarm.getAlarmCode())
                .alarmLevel(alarm.getAlarmLevel())
                .alarmLevelName(getAlarmLevelName(alarm.getAlarmLevel()))
                .deviceId(alarm.getDeviceId())
                .deviceName(device != null ? device.getDeviceName() : null)
                .deviceCode(device != null ? device.getDeviceCode() : null)
                .partName(part != null ? part.getPartName() : null)
                .alarmTime(alarm.getAlarmTime())
                .currentValue(alarm.getCurrentValue())
                .thresholdValue(alarm.getThresholdValue())
                .alarmMessage(buildAlarmMessage(alarm, device, part))
                .ruleId(alarm.getRuleId())
                .pushTime(pushTime)
                .build();
    }

    private String buildAlarmMessage(DeviceAlarm alarm, FactoryDevice device, DevicePart part) {
        StringBuilder sb = new StringBuilder();
        sb.append("设备").append(device != null ? device.getDeviceName() : "未知");
        if (part != null) {
            sb.append("的").append(part.getPartName());
        }
        sb.append("温度异常，当前值：").append(alarm.getCurrentValue()).append("℃");
        if (alarm.getThresholdValue() != null) {
            sb.append("，阈值：").append(alarm.getThresholdValue()).append("℃");
        }
        return sb.toString();
    }

    private String getAlarmLevelName(Integer level) {
        if (level == null) return "未知";
        return switch (level) {
            case 1 -> "一级(严重)";
            case 2 -> "二级(警告)";
            case 3 -> "三级(提示)";
            default -> "未知";
        };
    }

    private Set<Long> parseDeviceIds(String deviceIds, ObjectMapper objectMapper) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            List<Long> ids = objectMapper.readValue(deviceIds, new TypeReference<List<Long>>() {});
            return new HashSet<>(ids);
        } catch (JsonProcessingException e) {
            log.error("解析设备ID失败: {}", deviceIds, e);
            return Collections.emptySet();
        }
    }

    private Set<Integer> parseAlarmLevels(String alarmLevels, ObjectMapper objectMapper) {
        if (alarmLevels == null || alarmLevels.isEmpty()) {
            return Set.of(1, 2, 3);
        }
        try {
            List<Integer> levels = objectMapper.readValue(alarmLevels, new TypeReference<List<Integer>>() {});
            return new HashSet<>(levels);
        } catch (JsonProcessingException e) {
            log.error("解析报警等级失败: {}", alarmLevels, e);
            return Set.of(1, 2, 3);
        }
    }

    private Set<Long> parseNotifyTargetIds(String notifyTargetIds, ObjectMapper objectMapper) {
        if (notifyTargetIds == null || notifyTargetIds.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            List<String> ids = objectMapper.readValue(notifyTargetIds, new TypeReference<List<String>>() {});
            Set<Long> result = new HashSet<>();
            for (String id : ids) {
                if (id.startsWith("user_")) {
                    result.add(Long.parseLong(id.substring(5)));
                } else if (id.startsWith("org_")) {
                    // TODO: 根据组织ID查询用户
                } else {
                    try {
                        result.add(Long.parseLong(id));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return result;
        } catch (JsonProcessingException e) {
            log.error("解析通知目标ID失败: {}", notifyTargetIds, e);
            return Collections.emptySet();
        }
    }

    private void saveAlarmNotice(DeviceAlarm alarm, AlarmRule rule, FactoryDevice device, Long userId,
                                 LocalDateTime pushTime, IAlarmNoticeService alarmNoticeService) {
        AlarmNotice notice = AlarmNotice.builder()
                .ruleId(rule.getRuleId())
                .ruleName(rule.getRuleName())
                .alarmId(alarm.getAlarmId())
                .deviceId(alarm.getDeviceId())
                .deviceName(device != null ? device.getDeviceName() : "")
                .deviceCode(device != null ? device.getDeviceCode() : "")
                .alarmType(alarm.getAlarmType())
                .alarmLevel(alarm.getAlarmLevel())
                .alarmMessage(buildAlarmMessage(alarm, device, null))
                .currentValue(alarm.getCurrentValue().toString())
                .thresholdValue(alarm.getThresholdValue() != null ? alarm.getThresholdValue().toString() : "")
                .notifyUserId(userId)
                .notifyStatus(1)
                .notifyTime(pushTime)
                .readStatus(0)
                .build();

        alarmNoticeService.save(notice);
    }
}
