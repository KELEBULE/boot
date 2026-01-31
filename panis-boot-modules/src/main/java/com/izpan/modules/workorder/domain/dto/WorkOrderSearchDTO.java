package com.izpan.modules.workorder.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备工单查询 DTO 对象
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName WorkOrderSearchDTO
 * @CreateTime 2026-01-27
 */

@Getter
@Setter
@Schema(name = "WorkOrderSearchDTO", description = "设备工单查询 DTO 对象")
public class WorkOrderSearchDTO implements Serializable {

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

    @Schema(description = "优先级 1-紧急 2-高 3-中 4-低")
    private Integer priority;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "指派处理人ID")
    private Long assigneeId;

    @Schema(description = "工单状态 0-待处理 1-处理中 2-待审核 3-已完成 4-已取消")
    private Integer orderStatus;
}