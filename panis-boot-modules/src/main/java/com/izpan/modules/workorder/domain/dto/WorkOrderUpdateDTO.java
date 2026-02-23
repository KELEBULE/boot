package com.izpan.modules.workorder.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备工单更新 DTO 对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.dto.WorkOrderUpdateDTO
 * @CreateTime 2026-02-21
 */
@Data
@Schema(name = "WorkOrderUpdateDTO", description = "设备工单更新 DTO 对象")
public class WorkOrderUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private Long orderId;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "关联告警ID")
    private Long alarmId;

    @Schema(description = "工单类型 1-维修 2-保养 3-巡检 4-校准")
    private Integer orderType;

    @Schema(description = "故障时间")
    private String faultTime;

    @Schema(description = "故障描述")
    private String faultDescription;

    @Schema(description = "修复要求")
    private String repairRequirement;

    @Schema(description = "优先级 1-紧急 2-高 3-中 4-低")
    private Integer priority;

    @Schema(description = "指派处理人ID")
    private Long assigneeId;

    @Schema(description = "计划开始时间")
    private String planStartTime;

    @Schema(description = "计划完成时间")
    private String planEndTime;

    @Schema(description = "处理耗时(分钟)")
    private Integer handleDuration;

    @Schema(description = "维修结果")
    private String repairResult;

    @Schema(description = "维修费用")
    private BigDecimal repairCost;

    @Schema(description = "更换备件")
    private String spareParts;
}
