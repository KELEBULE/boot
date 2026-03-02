package com.izpan.modules.detection.domain.dto.devicedetection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceDetectionRecordSearchDTO", description = "设备检测记录查询 DTO")
public class DeviceDetectionRecordSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "检测状态")
    private Integer detectStatus;

    @Schema(description = "是否误报")
    private Integer isFalseAlarm;

    @Schema(description = "检测时间开始")
    private LocalDateTime detectTimeStart;

    @Schema(description = "检测时间结束")
    private LocalDateTime detectTimeEnd;

    @Schema(description = "传感器编码")
    private String sensorCode;
}
