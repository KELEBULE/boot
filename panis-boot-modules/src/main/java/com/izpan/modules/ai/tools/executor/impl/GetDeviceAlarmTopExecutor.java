package com.izpan.modules.ai.tools.executor.impl;

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
public class GetDeviceAlarmTopExecutor implements IAiToolExecutor {

    private final IDeviceAlarmService deviceAlarmService;
    private static final String REQUIRED_PERMISSION = "alarm:device:page";

    @Override
    public String getToolName() {
        return "get_device_alarm_top";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        long startTime = System.currentTimeMillis();
        try {
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 10;

            List<Map<String, Object>> topDevices = deviceAlarmService.getDeviceAlarmTop(limit);

            Map<String, Object> result = new HashMap<>();
            result.put("topDevices", topDevices);
            result.put("limit", limit);

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取设备报警TOP排行失败", e);
            return AiToolResult.failure(getToolName(), "获取设备报警TOP排行失败: " + e.getMessage());
        }
    }
}
