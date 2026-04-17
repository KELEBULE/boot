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
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.equipment.domain.entity.FactoryDevice;
import com.izpan.modules.equipment.repository.mapper.FactoryDeviceMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryDevicesExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "equipment:device:page";

    private final FactoryDeviceMapper deviceMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getToolName() {
        return "query_devices";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            Integer deviceStatus = arguments.containsKey("deviceStatus") 
                    ? ((Number) arguments.get("deviceStatus")).intValue() : null;
            String deviceName = (String) arguments.get("deviceName");
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 20;

            LambdaQueryWrapper<FactoryDevice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(FactoryDevice::getCreateTime);

            if (deviceStatus != null) {
                queryWrapper.eq(FactoryDevice::getDeviceStatus, deviceStatus);
            }
            if (deviceName != null && !deviceName.isEmpty()) {
                queryWrapper.like(FactoryDevice::getDeviceName, deviceName);
            }

            Page<FactoryDevice> page = deviceMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatDeviceList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询设备列表失败", e);
            return AiToolResult.failure(getToolName(), "查询设备列表失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatDeviceList(List<FactoryDevice> devices) {
        return devices.stream().map(device -> {
            Map<String, Object> map = new HashMap<>();
            map.put("deviceId", device.getDeviceId());
            map.put("deviceCode", device.getDeviceCode());
            map.put("deviceName", device.getDeviceName());
            map.put("deviceModel", device.getDeviceModel());
            map.put("deviceStatus", device.getDeviceStatus());
            map.put("deviceStatusName", getDeviceStatusName(device.getDeviceStatus()));
            map.put("manufacturer", device.getManufacturer());
            map.put("installTime", device.getInstallTime() != null ? device.getInstallTime().format(FORMATTER) : null);
            map.put("lastOnlineTime", device.getLastOnlineTime() != null ? device.getLastOnlineTime().format(FORMATTER) : null);
            return map;
        }).toList();
    }

    private String getDeviceStatusName(Integer status) {
        return switch (status) {
            case 1 -> "运行中";
            case 2 -> "待机";
            case 3 -> "故障";
            case 4 -> "维护中";
            default -> "未知";
        };
    }
}
