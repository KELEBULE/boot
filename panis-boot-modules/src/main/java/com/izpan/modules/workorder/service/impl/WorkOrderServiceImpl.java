package com.izpan.modules.workorder.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.exception.BusinessException;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderDeleteDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderFlowDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderUpdateDTO;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.entity.WorkOrderLog;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.repository.mapper.WorkOrderLogMapper;
import com.izpan.modules.workorder.repository.mapper.WorkOrderMapper;
import com.izpan.modules.workorder.service.IWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 设备工单管理 Service 服务接口实现层
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.service.impl.WorkOrderServiceImpl
 * @CreateTime 2026-01-27
 */

@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements IWorkOrderService {

    private final WorkOrderLogMapper workOrderLogMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public IPage<WorkOrderVO> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO) {
        WorkOrder queryWorkOrder = BeanUtil.copyProperties(workOrderSearchDTO, WorkOrder.class);
        return baseMapper.listWorkOrderPage(pageQuery.buildPage(), queryWorkOrder);
    }

    @Override
    public WorkOrder getWorkOrderById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public WorkOrderVO getWorkOrderDetailById(Long id) {
        return baseMapper.getWorkOrderDetailById(id);
    }

    @Override
    @Transactional
    public boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO) {
        WorkOrder workOrder = BeanUtil.copyProperties(workOrderAddDTO, WorkOrder.class);
        
        if (StringUtils.isBlank(workOrder.getOrderCode())) {
            workOrder.setOrderCode(generateOrderCode());
        }
        
        if (workOrder.getOrderStatus() == null) {
            workOrder.setOrderStatus(0);
        }
        
        if (workOrder.getPriority() == null) {
            workOrder.setPriority(2);
        }
        
        if (workOrder.getOrderSource() == null) {
            workOrder.setOrderSource(3);
        }
        
        Long currentUserId = getCurrentUserId();
        workOrder.setCreatorId(currentUserId);
        workOrder.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        boolean result = save(workOrder);
        
        if (result) {
            WorkOrderLog workOrderLog = WorkOrderLog.builder()
                    .orderId(workOrder.getOrderId())
                    .orderCode(workOrder.getOrderCode())
                    .actionType(1)
                    .toStatus(workOrder.getOrderStatus())
                    .toAssignee(workOrder.getAssigneeId())
                    .actionRemark("创建工单")
                    .operatorId(currentUserId)
                    .createTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            saveWorkOrderLog(workOrderLog);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean updateWorkOrder(WorkOrderUpdateDTO workOrderUpdateDTO) {
        WorkOrder existWorkOrder = baseMapper.selectById(workOrderUpdateDTO.getOrderId());
        if (existWorkOrder == null) {
            throw new BusinessException("工单不存在");
        }
        
        WorkOrder workOrder = BeanUtil.copyProperties(workOrderUpdateDTO, WorkOrder.class);
        workOrder.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return updateById(workOrder);
    }

    @Override
    @Transactional
    public boolean deleteWorkOrder(WorkOrderDeleteDTO workOrderDeleteDTO) {
        return removeByIds(workOrderDeleteDTO.getIds());
    }

    @Override
    @Transactional
    public boolean flowWorkOrder(WorkOrderFlowDTO workOrderFlowDTO) {
        WorkOrder existWorkOrder = baseMapper.selectById(workOrderFlowDTO.getOrderId());
        if (existWorkOrder == null) {
            throw new BusinessException("工单不存在");
        }
        
        Integer fromStatus = existWorkOrder.getOrderStatus();
        Integer toStatus = workOrderFlowDTO.getTargetStatus();
        Long fromAssignee = existWorkOrder.getAssigneeId();
        Long toAssignee = workOrderFlowDTO.getAssigneeId();
        
        WorkOrder updateWorkOrder = new WorkOrder();
        updateWorkOrder.setOrderId(workOrderFlowDTO.getOrderId());
        
        int actionType = 2;
        
        if (toStatus != null) {
            updateWorkOrder.setOrderStatus(toStatus);
            actionType = 2;
            
            if (toStatus == 1) {
                actionType = 4;
                if (StringUtils.isNotBlank(workOrderFlowDTO.getActualStartTime())) {
                    updateWorkOrder.setActualStartTime(workOrderFlowDTO.getActualStartTime());
                } else {
                    updateWorkOrder.setActualStartTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                updateWorkOrder.setProcessorId(getCurrentUserId());
            } else if (toStatus == 2) {
                actionType = 5;
                if (StringUtils.isNotBlank(workOrderFlowDTO.getActualEndTime())) {
                    updateWorkOrder.setActualEndTime(workOrderFlowDTO.getActualEndTime());
                } else {
                    updateWorkOrder.setActualEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                if (workOrderFlowDTO.getHandleDuration() != null) {
                    updateWorkOrder.setHandleDuration(workOrderFlowDTO.getHandleDuration());
                }
                if (StringUtils.isNotBlank(workOrderFlowDTO.getRepairResult())) {
                    updateWorkOrder.setRepairResult(workOrderFlowDTO.getRepairResult());
                }
                if (workOrderFlowDTO.getRepairCost() != null) {
                    updateWorkOrder.setRepairCost(workOrderFlowDTO.getRepairCost());
                }
                if (StringUtils.isNotBlank(workOrderFlowDTO.getSpareParts())) {
                    updateWorkOrder.setSpareParts(workOrderFlowDTO.getSpareParts());
                }
            } else if (toStatus == 3) {
                actionType = 6;
                updateWorkOrder.setReviewerId(getCurrentUserId());
                if (StringUtils.isNotBlank(workOrderFlowDTO.getReviewResult())) {
                    updateWorkOrder.setReviewResult(workOrderFlowDTO.getReviewResult());
                }
                updateWorkOrder.setReviewTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else if (toStatus == 4) {
                actionType = 7;
            }
        }
        
        if (toAssignee != null) {
            updateWorkOrder.setAssigneeId(toAssignee);
            if (actionType == 2) {
                actionType = 3;
            }
        }
        
        if (workOrderFlowDTO.getEvaluationScore() != null) {
            updateWorkOrder.setEvaluationScore(workOrderFlowDTO.getEvaluationScore());
            updateWorkOrder.setEvaluationRemark(workOrderFlowDTO.getEvaluationRemark());
            actionType = 8;
        }
        
        updateWorkOrder.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        boolean result = updateById(updateWorkOrder);
        
        if (result) {
            WorkOrderLog workOrderLog = WorkOrderLog.builder()
                    .orderId(workOrderFlowDTO.getOrderId())
                    .orderCode(existWorkOrder.getOrderCode())
                    .actionType(actionType)
                    .fromStatus(fromStatus)
                    .toStatus(toStatus != null ? toStatus : fromStatus)
                    .fromAssignee(fromAssignee)
                    .toAssignee(toAssignee)
                    .actionRemark(workOrderFlowDTO.getRemark())
                    .operatorId(getCurrentUserId())
                    .createTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            saveWorkOrderLog(workOrderLog);
        }
        
        return result;
    }

    @Override
    public List<WorkOrderLog> getWorkOrderLogs(Long orderId) {
        return workOrderLogMapper.selectWorkOrderLogsByOrderId(orderId);
    }

    @Override
    public boolean saveWorkOrderLog(WorkOrderLog workOrderLog) {
        return workOrderLogMapper.insert(workOrderLog) > 0;
    }

    @Override
    public WorkOrderStatisticsVO getStatisticsByUserId(Long userId, String timeRange) {
        WorkOrderStatisticsVO statistics = baseMapper.selectStatisticsByUserId(userId, timeRange);
        if (statistics != null) {
            statistics.setStatusDistribution(baseMapper.selectStatusDistributionByUserId(userId, timeRange));
            statistics.setPriorityDistribution(baseMapper.selectPriorityDistributionByUserId(userId, timeRange));
        }
        return statistics;
    }
    
    private String generateOrderCode() {
        String prefix = "WO";
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String randomSuffix = String.valueOf(IdUtil.getSnowflakeNextIdStr()).substring(0, 4);
        return prefix + timestamp + randomSuffix;
    }
    
    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return 1L;
        }
    }
}
