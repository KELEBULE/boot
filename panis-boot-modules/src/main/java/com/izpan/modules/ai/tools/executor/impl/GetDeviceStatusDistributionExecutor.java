package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.equipment.service.IFactoryDeviceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetDeviceStatusDistributionExecutor implements IAiToolExecutor {

    private final IFactoryDeviceService deviceService;

    @Override
    public String getToolName() {
        return "get_device_status_distribution";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        try {
            Map<Integer, Long> distribution = deviceService.getDeviceStatusDistribution();

            Map<String, Object> result = new HashMap<>();
            result.put("distribution", Map.of(
                    "running", Map.of("name", "运行中", "count", distribution.getOrDefault(1, 0L)),
                    "standby", Map.of("name", "待机", "count", distribution.getOrDefault(2, 0L)),
                    "fault", Map.of("name", "故障", "count", distribution.getOrDefault(3, 0L)),
                    "maintenance", Map.of("name", "维护中", "count", distribution.getOrDefault(4, 0L))
            ));
            result.put("total", distribution.values().stream().mapToLong(Long::longValue).sum());

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取设备状态分布失败", e);
            return AiToolResult.failure(getToolName(), "获取设备状态分布失败: " + e.getMessage());
        }
    }
}
