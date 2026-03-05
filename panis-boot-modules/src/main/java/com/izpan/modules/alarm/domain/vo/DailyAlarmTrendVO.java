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
@Schema(name = "DailyAlarmTrendVO", description = "每日报警趋势统计 VO")
public class DailyAlarmTrendVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "每日数据列表")
    private List<DailyItem> dailyData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "日期")
        private String date;

        @Schema(description = "报警次数")
        private Long alarmCount;
    }
}
