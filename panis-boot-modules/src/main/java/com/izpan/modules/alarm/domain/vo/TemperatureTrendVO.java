package com.izpan.modules.alarm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TemperatureTrendVO", description = "温度趋势统计 VO")
public class TemperatureTrendVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "趋势数据列表")
    private List<TrendItem> trendData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "检测时间")
        private String detectTime;

        @Schema(description = "检测温度值")
        private BigDecimal detectValue;

        @Schema(description = "一级报警阈值")
        private BigDecimal level1Value;

        @Schema(description = "二级报警阈值")
        private BigDecimal level2Value;

        @Schema(description = "三级报警阈值")
        private BigDecimal level3Value;
    }
}
