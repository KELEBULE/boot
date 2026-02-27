package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PartThresholdConfigVO", description = "部件阈值配置 VO 对象")
public class PartThresholdConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "部件ID")
    @JsonProperty("partId")
    private Long partId;

    @Schema(description = "部件名称")
    @JsonProperty("partName")
    private String partName;

    @Schema(description = "部件编码")
    @JsonProperty("partCode")
    private String partCode;

    @Schema(description = "一级报警比较运算符")
    @JsonProperty("level1Operator")
    private String level1Operator;

    @Schema(description = "一级报警阈值")
    @JsonProperty("level1Value")
    private BigDecimal level1Value;

    @Schema(description = "二级报警比较运算符")
    @JsonProperty("level2Operator")
    private String level2Operator;

    @Schema(description = "二级报警阈值")
    @JsonProperty("level2Value")
    private BigDecimal level2Value;

    @Schema(description = "三级报警比较运算符")
    @JsonProperty("level3Operator")
    private String level3Operator;

    @Schema(description = "三级报警阈值")
    @JsonProperty("level3Value")
    private BigDecimal level3Value;

    @Schema(description = "检测间隔(秒)")
    @JsonProperty("checkInterval")
    private Integer checkInterval;

    @Schema(description = "状态 1启用 0禁用")
    @JsonProperty("configStatus")
    private Integer configStatus;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
