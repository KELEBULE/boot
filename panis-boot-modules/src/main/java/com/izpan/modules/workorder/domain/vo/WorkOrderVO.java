package com.izpan.modules.workorder.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 设备工单 VO 展示类
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.vo.WorkOrderVO
 * @CreateTime 2026-01-27
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "WorkOrderVO", description = "设备工单 VO 对象")
public class WorkOrderVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工单ID")
    @JsonProperty("orderId")
    private Long orderId;

    @Schema(description = "工单编号")
    @JsonProperty("orderCode")
    private String orderCode;

    @Schema(description = "设备ID")
    @JsonProperty("deviceId")
    private Long deviceId;

    @Schema(description = "设备编号")
    @JsonProperty("deviceCode")
    private String deviceCode;

    @Schema(description = "设备名称")
    @JsonProperty("deviceName")
    private String deviceName;

    @Schema(description = "关联告警ID")
    @JsonProperty("alarmId")
    private Long alarmId;

    @Schema(description = "工单类型 1-维修 2-保养 3-巡检 4-校准")
    @JsonProperty("orderType")
    private Integer orderType;

    @Schema(description = "工单来源 1-告警生成 2-计划任务 3-手动创建")
    @JsonProperty("orderSource")
    private Integer orderSource;

    @Schema(description = "故障时间")
    @JsonProperty("faultTime")
    private String faultTime;

    @Schema(description = "故障描述")
    @JsonProperty("faultDescription")
    private String faultDescription;

    @Schema(description = "修复要求")
    @JsonProperty("repairRequirement")
    private String repairRequirement;

    @Schema(description = "优先级 1-紧急 2-高 3-中 4-低")
    @JsonProperty("priority")
    private Integer priority;

    @Schema(description = "创建人ID")
    @JsonProperty("creatorId")
    private Long creatorId;

    @Schema(description = "创建人姓名")
    @JsonProperty("creatorName")
    private String creatorName;

    @Schema(description = "指派处理人ID")
    @JsonProperty("assigneeId")
    private Long assigneeId;

    @Schema(description = "指派处理人姓名")
    @JsonProperty("assigneeName")
    private String assigneeName;

    @Schema(description = "实际处理人ID")
    @JsonProperty("processorId")
    private Long processorId;

    @Schema(description = "实际处理人姓名")
    @JsonProperty("processorName")
    private String processorName;

    @Schema(description = "审核人ID")
    @JsonProperty("reviewerId")
    private Long reviewerId;

    @Schema(description = "审核人姓名")
    @JsonProperty("reviewerName")
    private String reviewerName;

    @Schema(description = "计划开始时间")
    @JsonProperty("planStartTime")
    private String planStartTime;

    @Schema(description = "计划完成时间")
    @JsonProperty("planEndTime")
    private String planEndTime;

    @Schema(description = "实际开始时间")
    @JsonProperty("actualStartTime")
    private String actualStartTime;

    @Schema(description = "实际完成时间")
    @JsonProperty("actualEndTime")
    private String actualEndTime;

    @Schema(description = "工单状态 0-待处理 1-处理中 2-待审核 3-已完成 4-已取消")
    @JsonProperty("orderStatus")
    private Integer orderStatus;

    @Schema(description = "处理耗时(分钟)")
    @JsonProperty("handleDuration")
    private Integer handleDuration;

    @Schema(description = "维修结果")
    @JsonProperty("repairResult")
    private String repairResult;

    @Schema(description = "维修费用")
    @JsonProperty("repairCost")
    private BigDecimal repairCost;

    @Schema(description = "更换备件")
    @JsonProperty("spareParts")
    private String spareParts;

    @Schema(description = "审核结果")
    @JsonProperty("reviewResult")
    private String reviewResult;

    @Schema(description = "审核时间")
    @JsonProperty("reviewTime")
    private String reviewTime;

    @Schema(description = "评价分数(1-5)")
    @JsonProperty("evaluationScore")
    private Integer evaluationScore;

    @Schema(description = "评价备注")
    @JsonProperty("evaluationRemark")
    private String evaluationRemark;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private String createTime;

    @Schema(description = "更新时间")
    @JsonProperty("updateTime")
    private String updateTime;

    @Schema(description = "流转日志列表")
    @JsonProperty("flowLogs")
    private List<WorkOrderLogVO> flowLogs;
}
