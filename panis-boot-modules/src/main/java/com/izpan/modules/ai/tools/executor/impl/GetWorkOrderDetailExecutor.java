package com.izpan.modules.ai.tools.executor.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.repository.mapper.WorkOrderMapper;
import com.izpan.modules.workorder.service.IWorkOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetWorkOrderDetailExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "device:work:order:get";

    private final IWorkOrderService workOrderService;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public String getToolName() {
        return "get_work_order_detail";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            WorkOrderVO order = null;
            
            if (arguments.containsKey("orderCode") && arguments.get("orderCode") != null) {
                String orderCode = (String) arguments.get("orderCode");
                log.info("通过工单编号查询: {}", orderCode);
                
                LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(WorkOrder::getOrderCode, orderCode);
                WorkOrder workOrder = workOrderMapper.selectOne(queryWrapper);
                
                if (workOrder != null) {
                    order = workOrderService.getWorkOrderDetailById(workOrder.getOrderId());
                }
            } else if (arguments.containsKey("orderId") && arguments.get("orderId") != null) {
                Object orderIdObj = arguments.get("orderId");
                Long orderId;
                
                if (orderIdObj instanceof Number) {
                    orderId = ((Number) orderIdObj).longValue();
                } else if (orderIdObj instanceof String) {
                    orderId = Long.parseLong((String) orderIdObj);
                } else {
                    return AiToolResult.failure(getToolName(), "无效的工单ID参数");
                }
                
                log.info("通过工单ID查询: {}", orderId);
                order = workOrderService.getWorkOrderDetailById(orderId);
            } else {
                return AiToolResult.failure(getToolName(), "请提供工单编号(orderCode)或工单ID(orderId)");
            }

            if (order == null) {
                return AiToolResult.failure(getToolName(), "未找到指定的工单，请检查工单编号或ID是否正确");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getOrderId());
            result.put("orderCode", order.getOrderCode());
            result.put("orderTypeName", getOrderTypeName(order.getOrderType()));
            result.put("orderSourceName", getOrderSourceName(order.getOrderSource()));
            result.put("priorityName", getPriorityName(order.getPriority()));
            result.put("orderStatusName", getOrderStatusName(order.getOrderStatus()));
            result.put("faultTime", order.getFaultTime());
            result.put("faultDescription", order.getFaultDescription());
            result.put("repairRequirement", order.getRepairRequirement());
            result.put("createTime", order.getCreateTime());
            result.put("planStartTime", order.getPlanStartTime());
            result.put("planEndTime", order.getPlanEndTime());
            result.put("actualStartTime", order.getActualStartTime());
            result.put("actualEndTime", order.getActualEndTime());
            result.put("handleDuration", order.getHandleDuration());
            result.put("repairResult", order.getRepairResult());
            result.put("repairCost", order.getRepairCost());
            result.put("deviceName", order.getDeviceName());
            result.put("deviceId", order.getDeviceId());
            result.put("alarmId", order.getAlarmId());
            result.put("creatorName", order.getCreatorName());
            result.put("assigneeId", order.getAssigneeId());
            result.put("assigneeName", order.getAssigneeName());
            result.put("processorId", order.getProcessorId());
            result.put("processorName", order.getProcessorName());
            result.put("reviewerId", order.getReviewerId());
            result.put("reviewerName", order.getReviewerName());

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (NumberFormatException e) {
            log.error("工单ID格式错误", e);
            return AiToolResult.failure(getToolName(), "工单ID格式错误");
        } catch (Exception e) {
            log.error("获取工单详情失败", e);
            return AiToolResult.failure(getToolName(), "获取工单详情失败: " + e.getMessage());
        }
    }

    private String getOrderTypeName(Integer type) {
        if (type == null) return "未知";
        return switch (type) {
            case 1 -> "维修工单";
            case 2 -> "保养工单";
            case 3 -> "巡检工单";
            default -> "其他";
        };
    }

    private String getOrderSourceName(Integer source) {
        if (source == null) return "未知";
        return switch (source) {
            case 1 -> "人工创建";
            case 2 -> "报警转工单";
            case 3 -> "巡检转工单";
            default -> "其他";
        };
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "未知";
        return switch (priority) {
            case 1 -> "低";
            case 2 -> "中";
            case 3 -> "高";
            case 4 -> "紧急";
            default -> "未知";
        };
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "处理中";
            case 2 -> "已完成";
            case 3 -> "已关闭";
            default -> "未知";
        };
    }
}
