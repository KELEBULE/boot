package com.izpan.modules.workorder.domain.bo;

import com.izpan.modules.workorder.domain.entity.WorkOrder;
import lombok.Data;

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
public class WorkOrderBO extends WorkOrder {

    @Serial
    private static final long serialVersionUID = 1L;
}