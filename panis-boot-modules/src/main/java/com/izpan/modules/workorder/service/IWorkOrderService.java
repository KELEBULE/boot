package com.izpan.modules.workorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.workorder.domain.bo.WorkOrderBO;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;

/**
 * 设备工单管理 Service 服务接口层
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.service.IWorkOrderService
 * @CreateTime 2026-01-27
 */

public interface IWorkOrderService extends IService<WorkOrder> {

    /**
     * 设备工单管理 - 分页查询
     *
     * @param pageQuery 分页对象
     * @param workOrderSearchDTO 查询对象
     * @return 分页结果
     */
    IPage<WorkOrder> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO);

    /**
     * 根据ID获取设备工单信息
     *
     * @param id 工单ID
     * @return 工单信息
     */
    WorkOrder getWorkOrderById(Long id);

    /**
     * 新增设备工单
     *
     * @param workOrderAddDTO 新增对象
     * @return 结果
     */
    boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO);
}