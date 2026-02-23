package com.izpan.modules.workorder.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备工单流转日志 VO 展示类
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.workorder.domain.vo.WorkOrderLogVO
 * @CreateTime 2026-02-21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "WorkOrderLogVO", description = "设备工单流转日志 VO 对象")
public class WorkOrderLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    @JsonProperty("logId")
    private Long logId;

    @Schema(description = "工单ID")
    @JsonProperty("orderId")
    private Long orderId;

    @Schema(description = "工单编号")
    @JsonProperty("orderCode")
    private String orderCode;

    @Schema(description = "操作类型：1-创建 2-状态变更 3-指派处理人 4-开始处理 5-完成处理 6-审核 7-取消 8-评价")
    @JsonProperty("actionType")
    private Integer actionType;

    @Schema(description = "变更前状态")
    @JsonProperty("fromStatus")
    private Integer fromStatus;

    @Schema(description = "变更后状态")
    @JsonProperty("toStatus")
    private Integer toStatus;

    @Schema(description = "变更前处理人ID")
    @JsonProperty("fromAssignee")
    private Long fromAssignee;

    @Schema(description = "变更后处理人ID")
    @JsonProperty("toAssignee")
    private Long toAssignee;

    @Schema(description = "操作备注")
    @JsonProperty("actionRemark")
    private String actionRemark;

    @Schema(description = "操作人ID")
    @JsonProperty("operatorId")
    private Long operatorId;

    @Schema(description = "操作人姓名")
    @JsonProperty("operatorName")
    private String operatorName;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private String createTime;
}
