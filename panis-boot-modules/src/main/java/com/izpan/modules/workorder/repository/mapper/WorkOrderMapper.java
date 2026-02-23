package com.izpan.modules.workorder.repository.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;

/**
 * 工单管理 Mapper 接口
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.repository.mapper.WorkOrderMapper
 * @CreateTime 2026-01-27
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    IPage<WorkOrderVO> listWorkOrderPage(IPage<WorkOrder> page, @Param("bo") WorkOrder bo);

    WorkOrderVO getWorkOrderDetailById(@Param("orderId") Long orderId);

    WorkOrderStatisticsVO selectStatisticsByUserId(@Param("userId") Long userId);

    List<WorkOrderStatisticsVO.StatusDistribution> selectStatusDistributionByUserId(@Param("userId") Long userId);

    List<WorkOrderStatisticsVO.PriorityDistribution> selectPriorityDistributionByUserId(@Param("userId") Long userId);
}
