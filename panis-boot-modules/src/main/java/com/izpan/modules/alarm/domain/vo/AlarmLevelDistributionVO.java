package com.izpan.modules.alarm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AlarmLevelDistributionVO", description = "报警等级分布统计 VO")
public class AlarmLevelDistributionVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报警等级分布列表")
    private List<LevelItem> levelDistribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "报警等级: 1-一级, 2-二级, 3-三级")
        private Integer level;

        @Schema(description = "等级名称")
        private String levelName;

        @Schema(description = "报警数量")
        private Long count;

        @Schema(description = "占比百分比(精确到小数点后一位)")
        private Double percentage;
    }
}
