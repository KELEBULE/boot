package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FactoryDeviceVO", description = "工厂设备 VO 对象")
public class FactoryDeviceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识(用于前端选择)")
    @JsonProperty("uniqueKey")
    private String uniqueKey;

    @Schema(description = "设备ID")
    @JsonProperty("deviceId")
    private Long deviceId;

    @Schema(description = "设备编号")
    @JsonProperty("deviceCode")
    private String deviceCode;

    @Schema(description = "设备名称")
    @JsonProperty("deviceName")
    private String deviceName;

    @Schema(description = "设备型号")
    @JsonProperty("deviceModel")
    private String deviceModel;

    @Schema(description = "位置ID")
    @JsonProperty("locationId")
    private Long locationId;

    @Schema(description = "安装时间")
    @JsonProperty("installTime")
    private LocalDateTime installTime;

    @Schema(description = "启用时间")
    @JsonProperty("startUseTime")
    private LocalDateTime startUseTime;

    @Schema(description = "报废时间")
    @JsonProperty("scrapTime")
    private LocalDateTime scrapTime;

    @Schema(description = "设备状态 0-报废 1-正常 2-维护中 3-故障")
    @JsonProperty("deviceStatus")
    private Integer deviceStatus;

    @Schema(description = "报废状态 0-正常 1-已报废")
    @JsonProperty("scrapStatus")
    private Integer scrapStatus;

    @Schema(description = "累计工作时长(小时)")
    @JsonProperty("totalWorkHours")
    private Integer totalWorkHours;

    @Schema(description = "最后在线时间")
    @JsonProperty("lastOnlineTime")
    private LocalDateTime lastOnlineTime;

    @Schema(description = "设备SN码")
    @JsonProperty("deviceSn")
    private String deviceSn;

    @Schema(description = "制造商")
    @JsonProperty("manufacturer")
    private String manufacturer;

    @Schema(description = "维护周期(天)")
    @JsonProperty("maintainCycle")
    private Integer maintainCycle;

    @Schema(description = "上次维护时间")
    @JsonProperty("lastMaintainTime")
    private LocalDateTime lastMaintainTime;

    @Schema(description = "保修期(月)")
    @JsonProperty("warrantyPeriod")
    private Integer warrantyPeriod;

    @Schema(description = "设备备注")
    @JsonProperty("deviceNote")
    private String deviceNote;

    @Schema(description = "3D模型URL")
    @JsonProperty("modelUrl")
    private String modelUrl;

    @Schema(description = "设备图片URL")
    @JsonProperty("imageUrl")
    private String imageUrl;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;

    @Schema(description = "设备部件列表")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DevicePartVO> children;
}
