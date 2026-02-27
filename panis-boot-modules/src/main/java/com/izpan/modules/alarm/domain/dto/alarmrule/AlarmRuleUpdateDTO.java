package com.izpan.modules.alarm.domain.dto.alarmrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Schema(name = "AlarmRuleUpdateDTO", description = "报警规则 更新 DTO 对象")
public class AlarmRuleUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "设备ID列表")
    private List<Long> deviceIds;

    @Schema(description = "允许推送的告警等级列表")
    private List<Integer> alarmLevels;

    @Schema(description = "通知目标ID列表")
    private List<String> notifyTargetIds;

    @Schema(description = "规则状态(0:禁用 1:启用)")
    private Integer ruleStatus;

    @Schema(description = "备注")
    private String remark;
}
