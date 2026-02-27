package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "PartThresholdConfigDTO", description = "部件阈值配置 DTO 对象")
public class PartThresholdConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "部件ID不能为空")
    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "一级报警比较运算符(>=, <=, >, <, =)")
    private String level1Operator;

    @Schema(description = "一级报警阈值")
    private BigDecimal level1Value;

    @Schema(description = "二级报警比较运算符")
    private String level2Operator;

    @Schema(description = "二级报警阈值")
    private BigDecimal level2Value;

    @Schema(description = "三级报警比较运算符")
    private String level3Operator;

    @Schema(description = "三级报警阈值")
    private BigDecimal level3Value;

    @Schema(description = "检测间隔(秒)")
    private Integer checkInterval;

    @Schema(description = "状态 1启用 0禁用")
    private Integer configStatus;
}
