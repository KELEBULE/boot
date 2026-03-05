package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceDetailStatsVO", description = "设备详情统计 VO 对象")
public class DeviceDetailStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    @JsonProperty("deviceId")
    private Long deviceId;

    @Schema(description = "设备编码")
    @JsonProperty("deviceCode")
    private String deviceCode;

    @Schema(description = "设备名称")
    @JsonProperty("deviceName")
    private String deviceName;

    @Schema(description = "设备状态 0-报废 1-正常 2-维护中 3-故障")
    @JsonProperty("deviceStatus")
    private Integer deviceStatus;

    @Schema(description = "设备图片URL")
    @JsonProperty("imageUrl")
    private String imageUrl;

    @Schema(description = "未处理报警数量")
    @JsonProperty("alarmCount")
    private Long alarmCount;

    @Schema(description = "进行中工单数量")
    @JsonProperty("workOrderCount")
    private Long workOrderCount;

    @Schema(description = "累计运行时长(小时)")
    @JsonProperty("totalWorkHours")
    private Integer totalWorkHours;
}
