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
@Schema(name = "DeviceAlarmTopVO", description = "设备报警TOP排行 VO")
public class DeviceAlarmTopVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备报警排行列表")
    private List<DeviceAlarmItem> deviceAlarmList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceAlarmItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "设备ID")
        private Long deviceId;

        @Schema(description = "设备名称")
        private String deviceName;

        @Schema(description = "一级报警数量")
        private Long level1Count;

        @Schema(description = "二级报警数量")
        private Long level2Count;

        @Schema(description = "三级报警数量")
        private Long level3Count;

        @Schema(description = "总报警数量")
        private Long totalCount;
    }
}
