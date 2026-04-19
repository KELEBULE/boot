package com.izpan.modules.ai.tools.executor.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.alarm.service.IDeviceAlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAlarmStatisticsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "alarm:device:page";

    private final IDeviceAlarmService deviceAlarmService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getToolName() {
        return "get_alarm_statistics";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            Map<Integer, Long> levelDistribution = deviceAlarmService.getAlarmLevelDistribution();
            List<Map<String, Object>> topDevices = deviceAlarmService.getDeviceAlarmTop(5);

            LocalDate today = LocalDate.now();
            String todayStr = today.format(DATE_FORMATTER);

            Map<String, Object> result = new HashMap<>();
            result.put("levelDistribution", Map.of(
                    "level1", Map.of("name", "一级(严重)", "count", levelDistribution.getOrDefault(1, 0L)),
                    "level2", Map.of("name", "二级(警告)", "count", levelDistribution.getOrDefault(2, 0L)),
                    "level3", Map.of("name", "三级(提示)", "count", levelDistribution.getOrDefault(3, 0L))
            ));
            result.put("totalAlarms", levelDistribution.values().stream().mapToLong(Long::longValue).sum());
            result.put("topDevices", topDevices);
            result.put("queryDate", todayStr);

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取报警统计失败", e);
            return AiToolResult.failure(getToolName(), "获取报警统计失败: " + e.getMessage());
        }
    }
}
