package com.izpan.modules.workorder.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备工单 BO 业务处理对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.bo.WorkOrderBO
 * @CreateTime 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkOrderBO extends com.izpan.modules.workorder.domain.entity.WorkOrder {

    @Serial
    private static final long serialVersionUID = 1L;
}
