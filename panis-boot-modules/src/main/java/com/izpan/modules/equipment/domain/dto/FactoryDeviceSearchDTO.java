package com.izpan.modules.equipment.domain.dto;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FactoryDeviceSearchDTO", description = "工厂设备查询 DTO 对象")
public class FactoryDeviceSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备编号")
    private String deviceCode;

    @Schema(description = "设备名称")

    private String deviceName;

    @Schema(description = "设备状态 0-报废 1-正常 2-维护中 3-故障")
    private Integer deviceStatus;

    @Schema(description = "位置ID")
    private Long locationId;

    @Schema(description = "制造商")
    private String manufacturer;
}
