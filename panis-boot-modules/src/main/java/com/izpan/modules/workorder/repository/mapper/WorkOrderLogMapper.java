package com.izpan.modules.workorder.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.workorder.domain.entity.WorkOrderLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工单流转日志 Mapper 接口
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.repository.mapper.WorkOrderLogMapper
 * @CreateTime 2026-02-21
 */

@Mapper
public interface WorkOrderLogMapper extends BaseMapper<WorkOrderLog> {
    
    List<WorkOrderLog> selectWorkOrderLogsByOrderId(@Param("orderId") Long orderId);
}
