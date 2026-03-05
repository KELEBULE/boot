package com.izpan.modules.equipment.domain.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FactoryDeviceBatchStatusDTO", description = "工厂设备批量状态更新 DTO 对象")
public class FactoryDeviceBatchStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID列表")
    private List<Long> ids;

    @Schema(description = "设备状态：0-停用，1-正常，2-维修")
    private Integer deviceStatus;
}
