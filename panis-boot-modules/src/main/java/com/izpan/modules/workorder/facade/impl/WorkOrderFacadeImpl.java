package com.izpan.modules.workorder.facade.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.common.util.CglibUtil;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.facade.IWorkOrderFacade;
import com.izpan.modules.workorder.service.IWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备工单管理门面接口实现
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.facade.impl.WorkOrderFacadeImpl
 * @CreateTime 2026-01-27
 */

@Service
@RequiredArgsConstructor
public class WorkOrderFacadeImpl implements IWorkOrderFacade {

    private final IWorkOrderService workOrderService;

    @Override
    public RPage<WorkOrderVO> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO) {
        IPage<WorkOrder> workOrderPage = workOrderService.listWorkOrderPage(pageQuery, workOrderSearchDTO);
        return RPage.build(workOrderPage, WorkOrderVO::new);
    }

    @Override
    public WorkOrderVO getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderService.getWorkOrderById(id);
        return BeanUtil.copyProperties(workOrder, WorkOrderVO.class);
    }

    @Override
    @Transactional
    public boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO) {
        return workOrderService.addWorkOrder(workOrderAddDTO);
    }
}