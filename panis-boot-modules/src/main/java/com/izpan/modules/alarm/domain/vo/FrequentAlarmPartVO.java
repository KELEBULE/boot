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
@Schema(name = "FrequentAlarmPartVO", description = "频繁报警部件统计 VO")
public class FrequentAlarmPartVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部件列表")
    private List<PartItem> partList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "部件ID")
        private Long partId;

        @Schema(description = "部件名称")
        private String partName;

        @Schema(description = "部件编码")
        private String partCode;

        @Schema(description = "报警次数")
        private Long alarmCount;

        @Schema(description = "占比百分比")
        private Double percentage;
    }
}
