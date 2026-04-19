package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.service.IFactoryDeviceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetDeviceDetailExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "factory:device:get";

    private final IFactoryDeviceService deviceService;

    @Override
    public String getToolName() {
        return "get_device_detail";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            Object deviceIdObj = arguments.get("deviceId");
            Long deviceId;
            
            if (deviceIdObj instanceof Number) {
                deviceId = ((Number) deviceIdObj).longValue();
            } else if (deviceIdObj instanceof String) {
                deviceId = Long.parseLong((String) deviceIdObj);
            } else {
                return AiToolResult.failure(getToolName(), "无效的设备ID参数");
            }
            
            FactoryDevice device = deviceService.getFactoryDeviceById(deviceId);

            if (device == null) {
                return AiToolResult.failure(getToolName(), "未找到设备ID为 " + deviceId + " 的设备");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("deviceId", device.getDeviceId());
            result.put("deviceCode", device.getDeviceCode());
            result.put("deviceName", device.getDeviceName());
            result.put("deviceModel", device.getDeviceModel());
            result.put("deviceStatus", device.getDeviceStatus());
            result.put("deviceStatusName", getDeviceStatusName(device.getDeviceStatus()));
            result.put("manufacturer", device.getManufacturer());
            result.put("deviceSn", device.getDeviceSn());
            result.put("installTime", device.getInstallTime());
            result.put("startUseTime", device.getStartUseTime());
            result.put("lastOnlineTime", device.getLastOnlineTime());
            result.put("totalWorkHours", device.getTotalWorkHours());
            result.put("lastMaintainTime", device.getLastMaintainTime());
            result.put("maintainCycle", device.getMaintainCycle());
            result.put("deviceNote", device.getDeviceNote());

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (NumberFormatException e) {
            log.error("设备ID格式错误", e);
            return AiToolResult.failure(getToolName(), "设备ID格式错误");
        } catch (Exception e) {
            log.error("获取设备详情失败", e);
            return AiToolResult.failure(getToolName(), "获取设备详情失败: " + e.getMessage());
        }
    }

    private String getDeviceStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 1 -> "运行中";
            case 2 -> "待机";
            case 3 -> "故障";
            case 4 -> "维护中";
            default -> "未知";
        };
    }
}
