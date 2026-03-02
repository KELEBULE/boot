package com.izpan.modules.alarm.domain.dto.devicealarm;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceAlarmExportDTO", description = "设备报警导出 DTO")
public class DeviceAlarmExportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报警ID列表")
    private List<Long> ids;
}
