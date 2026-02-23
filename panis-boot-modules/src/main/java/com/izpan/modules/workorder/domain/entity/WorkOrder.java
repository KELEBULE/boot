package com.izpan.modules.workorder.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设备工单 Entity 实体类
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.entity.WorkOrder
 * @CreateTime 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_work_order")
public class WorkOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("deviceId")
    private Long deviceId;

    @JsonProperty("alarmId")
    private Long alarmId;

    @JsonProperty("orderType")
    private Integer orderType;

    @JsonProperty("orderSource")
    private Integer orderSource;

    @JsonProperty("faultTime")
    private String faultTime;

    @JsonProperty("faultDescription")
    private String faultDescription;

    @JsonProperty("repairRequirement")
    private String repairRequirement;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("creatorId")
    private Long creatorId;

    @JsonProperty("assigneeId")
    private Long assigneeId;

    @JsonProperty("processorId")
    private Long processorId;

    @JsonProperty("reviewerId")
    private Long reviewerId;

    @JsonProperty("createTime")
    private String createTime;

    @JsonProperty("planStartTime")
    private String planStartTime;

    @JsonProperty("planEndTime")
    private String planEndTime;

    @JsonProperty("actualStartTime")
    private String actualStartTime;

    @JsonProperty("actualEndTime")
    private String actualEndTime;

    @JsonProperty("orderStatus")
    private Integer orderStatus;

    @JsonProperty("handleDuration")
    private Integer handleDuration;

    @JsonProperty("repairResult")
    private String repairResult;

    @JsonProperty("repairCost")
    private BigDecimal repairCost;

    @JsonProperty("spareParts")
    private String spareParts;

    @JsonProperty("reviewResult")
    private String reviewResult;

    @JsonProperty("reviewTime")
    private String reviewTime;

    @JsonProperty("evaluationScore")
    private Integer evaluationScore;

    @JsonProperty("evaluationRemark")
    private String evaluationRemark;

    @JsonProperty("updateTime")
    private String updateTime;
}
