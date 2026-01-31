package com.izpan.modules.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.workorder.domain.bo.WorkOrderBO;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.repository.mapper.WorkOrderMapper;
import com.izpan.modules.workorder.service.IWorkOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 设备工单管理 Service 服务接口实现层
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.service.impl.WorkOrderServiceImpl
 * @CreateTime 2026-01-27
 */

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements IWorkOrderService {

    @Override
    public IPage<WorkOrder> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO) {
        LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.like(StringUtils.isNotBlank(workOrderSearchDTO.getOrderCode()), WorkOrder::getOrderCode, workOrderSearchDTO.getOrderCode())
                .eq(workOrderSearchDTO.getDeviceId() != null, WorkOrder::getDeviceId, workOrderSearchDTO.getDeviceId())
                .eq(workOrderSearchDTO.getAlarmId() != null, WorkOrder::getAlarmId, workOrderSearchDTO.getAlarmId())
                .eq(workOrderSearchDTO.getOrderType() != null, WorkOrder::getOrderType, workOrderSearchDTO.getOrderType())
                .eq(workOrderSearchDTO.getOrderSource() != null, WorkOrder::getOrderSource, workOrderSearchDTO.getOrderSource())
                .eq(workOrderSearchDTO.getPriority() != null, WorkOrder::getPriority, workOrderSearchDTO.getPriority())
                .eq(workOrderSearchDTO.getCreatorId() != null, WorkOrder::getCreatorId, workOrderSearchDTO.getCreatorId())
                .eq(workOrderSearchDTO.getAssigneeId() != null, WorkOrder::getAssigneeId, workOrderSearchDTO.getAssigneeId())
                .eq(workOrderSearchDTO.getOrderStatus() != null, WorkOrder::getOrderStatus, workOrderSearchDTO.getOrderStatus())
                .orderByDesc(WorkOrder::getCreateTime);

        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public WorkOrder getWorkOrderById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO) {
        WorkOrderBO workOrderBO = CglibUtil.convertObj(workOrderAddDTO, WorkOrderBO::new);
        return save(workOrderBO);
    }
}