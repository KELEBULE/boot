package com.izpan.modules.equipment.domain.vo;

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
@Schema(name = "DeviceStatusOverviewVO", description = "设备状态概览 VO")
public class DeviceStatusOverviewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备状态分布列表")
    private List<StatusItem> statusDistribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "状态值: 1-正常, 2-维修, 0-停用")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;

        @Schema(description = "设备数量")
        private Long count;

        @Schema(description = "占比百分比(精确到小数点后一位)")
        private Double percentage;
    }
}
