package com.izpan.modules.workorder.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备工单新增 DTO 对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO
 * @CreateTime 2026-01-27
 */
@Data
@Schema(name = "WorkOrderAddDTO", description = "设备工单新增 DTO 对象")
public class WorkOrderAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工单编号")
    private String orderCode;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "关联告警ID")
    private Long alarmId;

    @Schema(description = "工单类型 1-维修 2-保养 3-巡检 4-校准")
    private Integer orderType;

    @Schema(description = "工单来源 1-告警生成 2-计划任务 3-手动创建")
    private Integer orderSource;

    @Schema(description = "故障时间")
    private String faultTime;

    @Schema(description = "故障描述")
    private String faultDescription;

    @Schema(description = "修复要求")
    private String repairRequirement;

    @Schema(description = "优先级 1-紧急 2-高 3-中 4-低")
    private Integer priority;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "指派处理人ID")
    private Long assigneeId;

    @Schema(description = "计划开始时间")
    private String planStartTime;

    @Schema(description = "计划完成时间")
    private String planEndTime;

    @Schema(description = "工单状态 0-待处理 1-处理中 2-待审核 3-已完成 4-已取消")
    private Integer orderStatus;

    @Schema(description = "处理耗时(分钟)")
    private Integer handleDuration;

    @Schema(description = "维修结果")
    private String repairResult;

    @Schema(description = "维修费用")
    private BigDecimal repairCost;

    @Schema(description = "更换备件")
    private String spareParts;

    @Schema(description = "审核结果")
    private String reviewResult;

    @Schema(description = "审核时间")
    private String reviewTime;

    @Schema(description = "评价分数(1-5)")
    private Integer evaluationScore;

    @Schema(description = "评价备注")
    private String evaluationRemark;
}