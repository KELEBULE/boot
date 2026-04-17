package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.service.IWorkOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetWorkOrderStatisticsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "device:work:order:page";

    private final IWorkOrderService workOrderService;

    @Override
    public String getToolName() {
        return "get_work_order_statistics";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            String timeRange = arguments.containsKey("timeRange") 
                    ? (String) arguments.get("timeRange") : "month";

            Long userId = GlobalUserHolder.getUserId();
            WorkOrderStatisticsVO statistics = workOrderService.getStatisticsByUserId(userId, timeRange);

            Map<String, Object> result = new HashMap<>();
            if (statistics != null) {
                result.put("totalOrders", statistics.getTotal());
                result.put("pendingOrders", statistics.getPending());
                result.put("processingOrders", statistics.getProcessing());
                result.put("completedOrders", statistics.getCompleted());
                result.put("cancelledOrders", statistics.getCancelled());
                result.put("completionRate", statistics.getCompletionRate());
                result.put("avgHandleDuration", statistics.getAvgHandleDuration());
                result.put("timeRange", timeRange);
            } else {
                result.put("totalOrders", 0);
                result.put("pendingOrders", 0);
                result.put("processingOrders", 0);
                result.put("completedOrders", 0);
                result.put("cancelledOrders", 0);
                result.put("timeRange", timeRange);
            }

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("获取工单统计失败", e);
            return AiToolResult.failure(getToolName(), "获取工单统计失败: " + e.getMessage());
        }
    }
}
