package com.izpan.modules.ai.tools.executor.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryDeviceAlarmsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "alarm:device:page";

    private final DeviceAlarmMapper deviceAlarmMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getToolName() {
        return "query_device_alarms";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            Long deviceId = ((Number) arguments.get("deviceId")).longValue();
            String startTimeStr = (String) arguments.get("startTime");
            String endTimeStr = (String) arguments.get("endTime");

            LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceAlarm::getDeviceId, deviceId)
                    .orderByDesc(DeviceAlarm::getAlarmTime);

            if (startTimeStr != null && endTimeStr != null) {
                LocalDate startDate = LocalDate.parse(startTimeStr, DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(endTimeStr, DATE_FORMATTER);
                queryWrapper.between(DeviceAlarm::getAlarmTime, 
                        startDate.atStartOfDay(), 
                        endDate.plusDays(1).atStartOfDay());
            }

            Page<DeviceAlarm> page = deviceAlarmMapper.selectPage(
                    new Page<>(1, 50), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("deviceId", deviceId);
            result.put("total", page.getTotal());
            result.put("list", formatAlarmList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询设备报警失败", e);
            return AiToolResult.failure(getToolName(), "查询设备报警失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatAlarmList(List<DeviceAlarm> alarms) {
        return alarms.stream().map(alarm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("alarmId", alarm.getAlarmId());
            map.put("alarmCode", alarm.getAlarmCode());
            map.put("alarmLevel", alarm.getAlarmLevel());
            map.put("alarmLevelName", getAlarmLevelName(alarm.getAlarmLevel()));
            map.put("alarmTime", alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(FORMATTER) : null);
            map.put("currentValue", alarm.getCurrentValue());
            map.put("thresholdValue", alarm.getThresholdValue());
            map.put("confirmStatus", alarm.getConfirmStatus());
            map.put("confirmStatusName", alarm.getConfirmStatus() == 1 ? "已确认" : "未确认");
            return map;
        }).toList();
    }

    private String getAlarmLevelName(Integer level) {
        return switch (level) {
            case 1 -> "一级(严重)";
            case 2 -> "二级(警告)";
            case 3 -> "三级(提示)";
            default -> "未知";
        };
    }
}
