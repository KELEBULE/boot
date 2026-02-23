package com.izpan.modules.workorder.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备工单流转日志 Entity 实体类
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.entity.WorkOrderLog
 * @CreateTime 2026-02-21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_work_order_log")
public class WorkOrderLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("logId")
    private Long logId;

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("orderCode")
    private String orderCode;

    @JsonProperty("actionType")
    private Integer actionType;

    @JsonProperty("fromStatus")
    private Integer fromStatus;

    @JsonProperty("toStatus")
    private Integer toStatus;

    @JsonProperty("fromAssignee")
    private Long fromAssignee;

    @JsonProperty("toAssignee")
    private Long toAssignee;

    @JsonProperty("actionRemark")
    private String actionRemark;

    @JsonProperty("operatorId")
    private Long operatorId;

    @JsonProperty("operatorName")
    private String operatorName;

    @JsonProperty("createTime")
    private String createTime;
}
