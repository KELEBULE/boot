package com.izpan.modules.ai.tools.executor.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.alarm.service.IDeviceAlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetDailyAlarmTrendExecutor implements IAiToolExecutor {

    private final IDeviceAlarmService deviceAlarmService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getToolName() {
        return "get_daily_alarm_trend";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        try {
            Long deviceId = arguments.containsKey("deviceId") 
                    ? ((Number) arguments.get("deviceId")).longValue() : null;
            int days = arguments.containsKey("days") 
                    ? ((Number) arguments.get("days")).intValue() : 7;

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            String startTimeStr = startDate.format(DATE_FORMATTER);
            String endTimeStr = endDate.format(DATE_FORMATTER);

            List<Map<String, Object>> trend;
            if (deviceId != null) {
                trend = deviceAlarmService.getDailyAlarmTrend(deviceId, startTimeStr, endTimeStr);
            } else {
                trend = deviceAlarmService.getDailyAlarmTrend(null, startTimeStr, endTimeStr);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("trend", trend);
            result.put("days", days);
            result.put("startDate", startTimeStr);
            result.put("endDate", endTimeStr);

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取每日报警趋势失败", e);
            return AiToolResult.failure(getToolName(), "获取每日报警趋势失败: " + e.getMessage());
        }
    }
}
