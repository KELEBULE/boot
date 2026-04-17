package com.izpan.modules.ai.tools.executor.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class QueryTodayAlarmsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "data:alarm:page";

    private final DeviceAlarmMapper deviceAlarmMapper;

    @Override
    public String getToolName() {
        return "query_today_alarms";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            Integer alarmLevel = arguments.containsKey("alarmLevel") 
                    ? ((Number) arguments.get("alarmLevel")).intValue() : null;
            Integer confirmStatus = arguments.containsKey("confirmStatus") 
                    ? ((Number) arguments.get("confirmStatus")).intValue() : null;
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 20;

            LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(DeviceAlarm::getAlarmTime, todayStart, todayEnd)
                    .orderByDesc(DeviceAlarm::getAlarmTime);

            if (alarmLevel != null) {
                queryWrapper.eq(DeviceAlarm::getAlarmLevel, alarmLevel);
            }
            if (confirmStatus != null) {
                queryWrapper.eq(DeviceAlarm::getConfirmStatus, confirmStatus);
            }

            Page<DeviceAlarm> page = deviceAlarmMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatAlarmList(page.getRecords()));
            result.put("date", LocalDate.now().toString());

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询今日报警失败", e);
            return AiToolResult.failure(getToolName(), "查询今日报警失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatAlarmList(List<DeviceAlarm> alarms) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return alarms.stream().map(alarm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("alarmId", alarm.getAlarmId());
            map.put("alarmCode", alarm.getAlarmCode());
            map.put("deviceId", alarm.getDeviceId());
            map.put("alarmLevel", alarm.getAlarmLevel());
            map.put("alarmLevelName", getAlarmLevelName(alarm.getAlarmLevel()));
            map.put("alarmTime", alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(formatter) : null);
            map.put("currentValue", alarm.getCurrentValue());
            map.put("thresholdValue", alarm.getThresholdValue());
            map.put("confirmStatus", alarm.getConfirmStatus());
            map.put("confirmStatusName", alarm.getConfirmStatus() == 1 ? "已确认" : "未确认");
            map.put("clearStatus", alarm.getClearStatus());
            map.put("clearStatusName", alarm.getClearStatus() == 1 ? "已清除" : "未清除");
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
