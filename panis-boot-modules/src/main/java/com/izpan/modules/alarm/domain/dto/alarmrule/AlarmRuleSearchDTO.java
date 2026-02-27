package com.izpan.modules.alarm.domain.dto.alarmrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Schema(name = "AlarmRuleSearchDTO", description = "报警规则 查询 DTO 对象")
public class AlarmRuleSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则状态(0:禁用 1:启用)")
    private String ruleStatus;
}
