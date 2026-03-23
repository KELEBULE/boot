package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "FactoryDeviceUpdateDTO", description = "工厂设备更新 DTO 对象")
public class FactoryDeviceUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "设备编号")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备型号")
    private String deviceModel;

    @Schema(description = "位置ID")
    private Long locationId;

    @Schema(description = "安装时间")
    private LocalDateTime installTime;

    @Schema(description = "启用时间")
    private LocalDateTime startUseTime;

    @Schema(description = "报废时间")
    private LocalDateTime scrapTime;

    @Schema(description = "设备状态 0-报废 1-正常 2-维护中 3-故障")
    private Integer deviceStatus;

    @Schema(description = "报废状态 0-正常 1-已报废")
    private Integer scrapStatus;

    @Schema(description = "累计工作时长(小时)")
    private Integer totalWorkHours;

    @Schema(description = "设备SN码")
    private String deviceSn;

    @Schema(description = "制造商")
    private String manufacturer;

    @Schema(description = "维护周期(天)")
    private Integer maintainCycle;

    @Schema(description = "上次维护时间")
    private LocalDateTime lastMaintainTime;

    @Schema(description = "保修期(月)")
    private Integer warrantyPeriod;

    @Schema(description = "设备备注")
    private String deviceNote;

    @Schema(description = "3D模型URL")
    private String modelUrl;

    @Schema(description = "设备图片URL")
    private String imageUrl;
}
