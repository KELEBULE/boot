package com.izpan.modules.workorder.facade;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderDeleteDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderFlowDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderUpdateDTO;
import com.izpan.modules.workorder.domain.vo.WorkOrderLogVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;

import java.util.List;

/**
 * 设备工单管理门面接口
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.facade.IWorkOrderFacade
 * @CreateTime 2026-01-27
 */

public interface IWorkOrderFacade {

    /**
     * 设备工单管理 - 分页查询
     *
     * @param pageQuery 分页对象
     * @param workOrderSearchDTO 查询对象
     * @return 分页结果
     */
    RPage<WorkOrderVO> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO);

    /**
     * 根据ID获取设备工单信息
     *
     * @param id 工单ID
     * @return 工单信息
     */
    WorkOrderVO getWorkOrderById(Long id);

    /**
     * 新增设备工单
     *
     * @param workOrderAddDTO 新增对象
     * @return 结果
     */
    boolean addWorkOrder(WorkOrderAddDTO workOrderAddDTO);

    /**
     * 更新设备工单
     *
     * @param workOrderUpdateDTO 更新对象
     * @return 结果
     */
    boolean updateWorkOrder(WorkOrderUpdateDTO workOrderUpdateDTO);

    /**
     * 批量删除设备工单
     *
     * @param workOrderDeleteDTO 删除对象
     * @return 结果
     */
    boolean deleteWorkOrder(WorkOrderDeleteDTO workOrderDeleteDTO);

    /**
     * 工单流转
     *
     * @param workOrderFlowDTO 流转对象
     * @return 结果
     */
    boolean flowWorkOrder(WorkOrderFlowDTO workOrderFlowDTO);

    /**
     * 获取工单流转日志
     *
     * @param orderId 工单ID
     * @return 流转日志列表
     */
    List<WorkOrderLogVO> getWorkOrderLogs(Long orderId);

    /**
     * 获取当前用户本月工单统计数据
     *
     * @return 统计数据
     */
    WorkOrderStatisticsVO getStatistics();
}
