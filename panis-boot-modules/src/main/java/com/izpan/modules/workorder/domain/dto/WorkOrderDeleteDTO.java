package com.izpan.modules.workorder.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 设备工单删除 DTO 对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.dto.WorkOrderDeleteDTO
 * @CreateTime 2026-02-21
 */
@Data
@Schema(name = "WorkOrderDeleteDTO", description = "设备工单删除 DTO 对象")
public class WorkOrderDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "工单ID列表不能为空")
    @Schema(description = "工单ID列表")
    private List<Long> ids;
}
