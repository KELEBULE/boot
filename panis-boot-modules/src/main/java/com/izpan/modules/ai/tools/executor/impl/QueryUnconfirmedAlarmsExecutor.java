package com.izpan.modules.ai.tools.executor.impl;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.alarm.domain.entity.DeviceAlarm;
import com.izpan.modules.alarm.repository.mapper.DeviceAlarmMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryUnconfirmedAlarmsExecutor implements IAiToolExecutor {

    private final DeviceAlarmMapper deviceAlarmMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getToolName() {
        return "query_unconfirmed_alarms";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        try {
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 20;

            LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceAlarm::getConfirmStatus, 0)
                    .orderByDesc(DeviceAlarm::getAlarmTime);

            Page<DeviceAlarm> page = deviceAlarmMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatAlarmList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询未确认报警失败", e);
            return AiToolResult.failure(getToolName(), "查询未确认报警失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatAlarmList(List<DeviceAlarm> alarms) {
        return alarms.stream().map(alarm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("alarmId", alarm.getAlarmId());
            map.put("alarmCode", alarm.getAlarmCode());
            map.put("deviceId", alarm.getDeviceId());
            map.put("alarmLevel", alarm.getAlarmLevel());
            map.put("alarmLevelName", getAlarmLevelName(alarm.getAlarmLevel()));
            map.put("alarmTime", alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(FORMATTER) : null);
            map.put("currentValue", alarm.getCurrentValue());
            map.put("thresholdValue", alarm.getThresholdValue());
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
