package com.izpan.modules.workorder.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
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
    IPage<WorkOrderVO> listWorkOrderPage(PageQuery pageQuery, WorkOrderSearchDTO workOrderSearchDTO);

    /**
     * 根据ID获取设备工单信息
     *
     * @param id 工单ID
     * @return 工单信息
     */
    WorkOrder getWorkOrderById(Long id);

    /**
     * 根据ID获取设备工单详情（包含关联信息）
     *
     * @param id 工单ID
     * @return 工单详情
     */
    WorkOrderVO getWorkOrderDetailById(Long id);

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
     * 工单流转（状态变更、指派处理人等）
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
    List<WorkOrderLog> getWorkOrderLogs(Long orderId);

    /**
     * 保存工单流转日志
     *
     * @param workOrderLog 流转日志
     * @return 结果
     */
    boolean saveWorkOrderLog(WorkOrderLog workOrderLog);

    /**
     * 获取用户工单统计数据
     *
     * @param userId 用户ID
     * @param timeRange 时间范围: week-近一周, month-近一月, quarter-近一季度
     * @return 统计数据
     */
    WorkOrderStatisticsVO getStatisticsByUserId(Long userId, String timeRange);

    /**
     * 检查设备是否有未完成的工单
     *
     * @param deviceId 设备ID
     * @return true-有未完成工单，false-没有未完成工单
     */
    boolean hasUnfinishedWorkOrder(Long deviceId);

    /**
     * 获取设备未完成的工单数量
     *
     * @param deviceId 设备ID
     * @return 未完成工单数量
     */
    int countUnfinishedWorkOrders(Long deviceId);
}
