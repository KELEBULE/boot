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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AlarmPushJob {

    private final IDeviceAlarmService deviceAlarmService;
    private final IAlarmRuleService alarmRuleService;
    private final IAlarmNoticeService alarmNoticeService;
    private final IFactoryDeviceService factoryDeviceService;
    private final IDevicePartService devicePartService;
    private final AlarmWebSocketHandler alarmWebSocketHandler;
    private final ObjectMapper objectMapper;

    public AlarmPushJob(
            IDeviceAlarmService deviceAlarmService,
            IAlarmRuleService alarmRuleService,
            IAlarmNoticeService alarmNoticeService,
            IFactoryDeviceService factoryDeviceService,
            IDevicePartService devicePartService,
            AlarmWebSocketHandler alarmWebSocketHandler,
            ObjectMapper objectMapper) {
        this.deviceAlarmService = deviceAlarmService;
        this.alarmRuleService = alarmRuleService;
        this.alarmNoticeService = alarmNoticeService;
        this.factoryDeviceService = factoryDeviceService;
        this.devicePartService = devicePartService;
        this.alarmWebSocketHandler = alarmWebSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 60000)
    public void execute() {
        log.info("开始执行报警推送定时任务...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<DeviceAlarm>()
                .eq(DeviceAlarm::getConfirmStatus, 0)
                .orderByAsc(DeviceAlarm::getAlarmTime);
        
        List<DeviceAlarm> unconfirmedAlarms = deviceAlarmService.list(queryWrapper);
        log.info("查询到 {} 条未确认报警", unconfirmedAlarms.size());

        List<AlarmRule> allRules = alarmRuleService.list(
            new LambdaQueryWrapper<AlarmRule>().eq(AlarmRule::getRuleStatus, 1)
        );

        for (DeviceAlarm alarm : unconfirmedAlarms) {
            AlarmRule rule = findMatchingRule(alarm, allRules);
            if (rule == null) {
                log.debug("未找到匹配的报警规则, alarmId: {}, deviceId: {}", alarm.getAlarmId(), alarm.getDeviceId());
                continue;
            }

            if (!isInPushTimeRange(currentTime, rule.getPushStartTime(), rule.getPushEndTime())) {
                log.debug("当前时间不在推送时间范围内, ruleId: {}", rule.getRuleId());
                continue;
            }

            if (!shouldPush(alarm, rule)) {
                continue;
            }

            pushAlarm(alarm, rule, now);
        }

        log.info("报警推送定时任务执行完成");
    }

    private AlarmRule findMatchingRule(DeviceAlarm alarm, List<AlarmRule> rules) {
        if (alarm.getRuleId() != null) {
            return rules.stream()
                .filter(r -> r.getRuleId().equals(alarm.getRuleId()))
                .findFirst()
                .orElse(null);
        }

        for (AlarmRule rule : rules) {
            Set<Long> deviceIds = parseDeviceIds(rule.getDeviceIds());
            if (deviceIds.contains(alarm.getDeviceId())) {
                Set<Integer> alarmLevels = parseAlarmLevels(rule.getAlarmLevels());
                if (alarmLevels.contains(alarm.getAlarmLevel())) {
                    return rule;
                }
            }
        }
        return null;
    }

    private Set<Long> parseDeviceIds(String deviceIds) {
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

    private Set<Integer> parseAlarmLevels(String alarmLevels) {
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

    private boolean isInPushTimeRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return true;
        }
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    private boolean shouldPush(DeviceAlarm alarm, AlarmRule rule) {
        Integer pushInterval = rule.getPushInterval();
        if (pushInterval == null || pushInterval <= 0) {
            pushInterval = 5;
        }

        LambdaQueryWrapper<AlarmNotice> queryWrapper = new LambdaQueryWrapper<AlarmNotice>()
                .eq(AlarmNotice::getAlarmId, alarm.getAlarmId())
                .orderByDesc(AlarmNotice::getCreateTime)
                .last("LIMIT 1");
        
        AlarmNotice lastNotice = alarmNoticeService.getOne(queryWrapper);
        
        if (lastNotice == null) {
            return true;
        }

        LocalDateTime nextPushTime = lastNotice.getCreateTime().plusMinutes(pushInterval);
        return LocalDateTime.now().isAfter(nextPushTime);
    }

    private void pushAlarm(DeviceAlarm alarm, AlarmRule rule, LocalDateTime pushTime) {
        Set<Long> targetUserIds = parseNotifyTargetIds(rule.getNotifyTargetIds());
        if (targetUserIds.isEmpty()) {
            log.warn("报警规则没有配置通知目标, ruleId: {}", rule.getRuleId());
            return;
        }

        FactoryDevice device = factoryDeviceService.getById(alarm.getDeviceId());
        DevicePart part = alarm.getPartId() != null ? devicePartService.getById(alarm.getPartId()) : null;

        AlarmPushVO pushVO = buildAlarmPushVO(alarm, device, part, pushTime);

        for (Long userId : targetUserIds) {
            if (alarmWebSocketHandler.isUserOnline(userId)) {
                alarmWebSocketHandler.pushAlarmToUser(userId, pushVO);
                saveAlarmNotice(alarm, rule, device, userId, pushTime);
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

    private Set<Long> parseNotifyTargetIds(String notifyTargetIds) {
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

    private void saveAlarmNotice(DeviceAlarm alarm, AlarmRule rule, FactoryDevice device, Long userId, LocalDateTime pushTime) {
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
