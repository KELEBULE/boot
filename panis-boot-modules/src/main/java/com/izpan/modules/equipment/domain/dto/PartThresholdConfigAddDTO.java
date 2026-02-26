package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "PartThresholdConfigAddDTO", description = "部件阈值配置新增 DTO 对象")
public class PartThresholdConfigAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "部件ID不能为空")
    @Schema(description = "部件ID")
    private Long partId;

    @NotBlank(message = "配置名称不能为空")
    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "温度下限")
    private BigDecimal tempMin;

    @Schema(description = "温度上限")
    private BigDecimal tempMax;

    @Schema(description = "预警下限")
    private BigDecimal warningMin;

    @Schema(description = "预警上限")
    private BigDecimal warningMax;

    @Schema(description = "检测间隔(秒)")
    private Integer checkInterval;

    @Schema(description = "状态 1启用 0禁用")
    private Integer configStatus;
}
