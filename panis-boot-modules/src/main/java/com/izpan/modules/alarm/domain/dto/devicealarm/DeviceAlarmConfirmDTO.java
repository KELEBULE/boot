package com.izpan.modules.alarm.domain.dto.devicealarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceAlarmConfirmDTO", description = "设备报警确认 DTO")
public class DeviceAlarmConfirmDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "报警ID不能为空")
    @Schema(description = "报警ID")
    private Long alarmId;

    @Schema(description = "是否误报: 0-否, 1-是")
    private Integer isFalseAlarm;
}
