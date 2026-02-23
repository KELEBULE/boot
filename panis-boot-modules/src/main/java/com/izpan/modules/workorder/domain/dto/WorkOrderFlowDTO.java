package com.izpan.modules.workorder.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备工单流转 DTO 对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.dto.WorkOrderFlowDTO
 * @CreateTime 2026-02-21
 */
@Data
@Schema(name = "WorkOrderFlowDTO", description = "设备工单流转 DTO 对象")
public class WorkOrderFlowDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "工单ID不能为空")
    @Schema(description = "工单ID")
    private Long orderId;

    @Schema(description = "目标状态 0-待处理 1-处理中 2-待审核 3-已完成 4-已取消")
    private Integer targetStatus;

    @Schema(description = "指派处理人ID")
    private Long assigneeId;

    @Schema(description = "操作备注")
    private String remark;

    @Schema(description = "实际开始时间")
    private String actualStartTime;

    @Schema(description = "实际完成时间")
    private String actualEndTime;

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

    @Schema(description = "评价分数(1-5)")
    private Integer evaluationScore;

    @Schema(description = "评价备注")
    private String evaluationRemark;
}
