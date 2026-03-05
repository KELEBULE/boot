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
@Schema(name = "FrequentAlarmTimeVO", description = "频繁报警时间统计 VO")
public class FrequentAlarmTimeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "小时分布列表")
    private List<HourlyItem> hourlyDistribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "小时(0-23)")
        private Integer hour;

        @Schema(description = "报警次数")
        private Long alarmCount;
    }
}
