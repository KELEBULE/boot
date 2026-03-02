package com.izpan.modules.detection.domain.dto.devicedetection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DetectionDataDTO", description = "检测数据接收 DTO")
public class DetectionDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "检测值")
    private BigDecimal detectValue;

    @Schema(description = "检测时间")
    private LocalDateTime detectTime;

    @Schema(description = "传感器编码")
    private String sensorCode;

    @Schema(description = "数据来源")
    private String dataSource;
}
