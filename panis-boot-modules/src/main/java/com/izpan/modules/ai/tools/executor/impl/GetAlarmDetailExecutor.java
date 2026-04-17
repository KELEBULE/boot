package com.izpan.modules.ai.tools.executor.impl;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class GetAlarmDetailExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "data:alarm:get";

    private final DeviceAlarmMapper deviceAlarmMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getToolName() {
        return "get_alarm_detail";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            String alarmCode = (String) arguments.get("alarmCode");
            
            if (alarmCode == null || alarmCode.isEmpty()) {
                return AiToolResult.failure(getToolName(), "请提供报警编号(alarmCode)");
            }

            LambdaQueryWrapper<DeviceAlarm> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DeviceAlarm::getAlarmCode, alarmCode);
            DeviceAlarm alarm = deviceAlarmMapper.selectOne(queryWrapper);

            if (alarm == null) {
                return AiToolResult.failure(getToolName(), "未找到报警编号为 " + alarmCode + " 的报警记录，请检查编号是否正确");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("alarmId", alarm.getAlarmId());
            result.put("alarmCode", alarm.getAlarmCode());
            result.put("deviceId", alarm.getDeviceId());
            result.put("partId", alarm.getPartId());
            result.put("alarmType", alarm.getAlarmType());
            result.put("alarmLevel", alarm.getAlarmLevel());
            result.put("alarmLevelName", getAlarmLevelName(alarm.getAlarmLevel()));
            result.put("alarmTime", alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(FORMATTER) : null);
            result.put("currentValue", alarm.getCurrentValue());
            result.put("thresholdValue", alarm.getThresholdValue());
            result.put("confirmStatus", alarm.getConfirmStatus());
            result.put("confirmStatusName", alarm.getConfirmStatus() != null && alarm.getConfirmStatus() == 1 ? "已确认" : "未确认");
            result.put("confirmTime", alarm.getConfirmTime() != null ? alarm.getConfirmTime().format(FORMATTER) : null);
            result.put("clearStatus", alarm.getClearStatus());
            result.put("clearStatusName", alarm.getClearStatus() != null && alarm.getClearStatus() == 1 ? "已清除" : "未清除");
            result.put("clearTime", alarm.getClearTime() != null ? alarm.getClearTime().format(FORMATTER) : null);
            result.put("alarmDuration", alarm.getAlarmDuration());
            result.put("isFalseAlarm", alarm.getIsFalseAlarm());
            result.put("isFalseAlarmName", alarm.getIsFalseAlarm() != null && alarm.getIsFalseAlarm() == 1 ? "是" : "否");
            result.put("workOrderId", alarm.getWorkOrderId());
            result.put("createTime", alarm.getCreateTime() != null ? alarm.getCreateTime().format(FORMATTER) : null);

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询报警详情失败", e);
            return AiToolResult.failure(getToolName(), "查询报警详情失败: " + e.getMessage());
        }
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
}
