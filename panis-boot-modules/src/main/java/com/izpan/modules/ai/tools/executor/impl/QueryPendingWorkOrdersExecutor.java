package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.repository.mapper.WorkOrderMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryPendingWorkOrdersExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "device:work:order:page";

    private final WorkOrderMapper workOrderMapper;

    @Override
    public String getToolName() {
        return "query_pending_work_orders";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 20;

            LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(WorkOrder::getOrderStatus, 0, 1)
                    .orderByAsc(WorkOrder::getPriority)
                    .orderByAsc(WorkOrder::getCreateTime);

            Page<WorkOrder> page = workOrderMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatWorkOrderList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询待处理工单失败", e);
            return AiToolResult.failure(getToolName(), "查询待处理工单失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatWorkOrderList(List<WorkOrder> orders) {
        return orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", order.getOrderId());
            map.put("orderCode", order.getOrderCode());
            map.put("orderTypeName", getOrderTypeName(order.getOrderType()));
            map.put("priorityName", getPriorityName(order.getPriority()));
            map.put("orderStatusName", getOrderStatusName(order.getOrderStatus()));
            map.put("faultDescription", order.getFaultDescription());
            map.put("createTime", order.getCreateTime());
            map.put("planEndTime", order.getPlanEndTime());
            return map;
        }).toList();
    }

    private String getOrderTypeName(Integer type) {
        return switch (type) {
            case 1 -> "维修工单";
            case 2 -> "保养工单";
            case 3 -> "巡检工单";
            default -> "其他";
        };
    }

    private String getPriorityName(Integer priority) {
        return switch (priority) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "紧急";
            default -> "未知";
        };
    }

    private String getOrderStatusName(Integer status) {
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "处理中";
            default -> "未知";
        };
    }
}
