package com.izpan.modules.workorder.facade.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderDeleteDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderFlowDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderUpdateDTO;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.entity.WorkOrderLog;
import com.izpan.modules.workorder.domain.vo.WorkOrderLogVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.facade.IWorkOrderFacade;
import com.izpan.modules.workorder.service.IWorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        IPage<WorkOrderVO> workOrderPage = workOrderService.listWorkOrderPage(pageQuery, workOrderSearchDTO);
        return new RPage<>(workOrderPage.getCurrent(), workOrderPage.getSize(), workOrderPage.getRecords(), workOrderPage.getPages(), workOrderPage.getTotal());
    }

    @Override
    public WorkOrderVO getWorkOrderById(Long id) {
        WorkOrderVO workOrderVO = workOrderService.getWorkOrderDetailById(id);
        
        if (workOrderVO != null) {
            List<WorkOrderLog> logs = workOrderService.getWorkOrderLogs(id);
            List<WorkOrderLogVO> logVOList = logs.stream()
                    .map(log -> BeanUtil.copyProperties(log, WorkOrderLogVO.class))
                    .collect(Collectors.toList());
            workOrderVO.setFlowLogs(logVOList);
        }
        
        return workOrderVO;
    }

    @Override
    @Transactional
    public boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO) {
        return workOrderService.addWorkOrder(workOrderAddDTO);
    }

    @Override
    @Transactional
    public boolean updateWorkOrder(WorkOrderUpdateDTO workOrderUpdateDTO) {
        return workOrderService.updateWorkOrder(workOrderUpdateDTO);
    }

    @Override
    @Transactional
    public boolean deleteWorkOrder(WorkOrderDeleteDTO workOrderDeleteDTO) {
        return workOrderService.deleteWorkOrder(workOrderDeleteDTO);
    }

    @Override
    @Transactional
    public boolean flowWorkOrder(WorkOrderFlowDTO workOrderFlowDTO) {
        return workOrderService.flowWorkOrder(workOrderFlowDTO);
    }

    @Override
    public List<WorkOrderLogVO> getWorkOrderLogs(Long orderId) {
        List<WorkOrderLog> logs = workOrderService.getWorkOrderLogs(orderId);
        return logs.stream()
                .map(log -> BeanUtil.copyProperties(log, WorkOrderLogVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public WorkOrderStatisticsVO getStatistics(String timeRange) {
        Long userId = StpUtil.getLoginIdAsLong();
        return workOrderService.getStatisticsByUserId(userId, timeRange);
    }
}
