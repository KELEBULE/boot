package com.izpan.modules.workorder.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.workorder.domain.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

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
}