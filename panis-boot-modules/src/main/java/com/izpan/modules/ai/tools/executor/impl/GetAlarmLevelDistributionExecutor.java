package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
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
public class GetAlarmLevelDistributionExecutor implements IAiToolExecutor {

    private final IDeviceAlarmService deviceAlarmService;
    private static final String REQUIRED_PERMISSION = "alarm:device:page";

    @Override
    public String getToolName() {
        return "get_alarm_level_distribution";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        long startTime = System.currentTimeMillis();
        try {
            Map<Integer, Long> distribution = deviceAlarmService.getAlarmLevelDistribution();

            Map<String, Object> result = new HashMap<>();
            result.put("distribution", Map.of(
                    "level1", Map.of("name", "一级(严重)", "count", distribution.getOrDefault(1, 0L)),
                    "level2", Map.of("name", "二级(警告)", "count", distribution.getOrDefault(2, 0L)),
                    "level3", Map.of("name", "三级(提示)", "count", distribution.getOrDefault(3, 0L))
            ));
            result.put("total", distribution.values().stream().mapToLong(Long::longValue).sum());

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取报警等级分布失败", e);
            return AiToolResult.failure(getToolName(), "获取报警等级分布失败: " + e.getMessage());
        }
    }
}
