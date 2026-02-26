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

    @Schema(description = "配置名称")
    @JsonProperty("configName")
    private String configName;

    @Schema(description = "温度下限")
    @JsonProperty("tempMin")
    private BigDecimal tempMin;

    @Schema(description = "温度上限")
    @JsonProperty("tempMax")
    private BigDecimal tempMax;

    @Schema(description = "预警下限")
    @JsonProperty("warningMin")
    private BigDecimal warningMin;

    @Schema(description = "预警上限")
    @JsonProperty("warningMax")
    private BigDecimal warningMax;

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
