package com.izpan.modules.alarm.domain.dto.alarmrule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Schema(name = "AlarmRuleDeleteDTO", description = "报警规则 删除 DTO 对象")
public class AlarmRuleDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID列表")
    private List<Long> ids;
}
