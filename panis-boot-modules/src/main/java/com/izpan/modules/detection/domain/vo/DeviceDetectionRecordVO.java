package com.izpan.modules.detection.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceDetectionRecordVO", description = "设备检测记录 VO 对象")
public class DeviceDetectionRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "部件名称")
    private String partName;

    @Schema(description = "阈值配置ID")
    private Long thresholdId;

    @Schema(description = "检测值")
    private BigDecimal detectValue;

    @Schema(description = "检测状态")
    private Integer detectStatus;

    @Schema(description = "一级报警值")
    private BigDecimal level1Value;

    @Schema(description = "二级报警值")
    private BigDecimal level2Value;

    @Schema(description = "三级报警值")
    private BigDecimal level3Value;

    @Schema(description = "是否误报")
    private Integer isFalseAlarm;

    @Schema(description = "报警ID")
    private Long alarmId;

    @Schema(description = "检测时间")
    private LocalDateTime detectTime;

    @Schema(description = "传感器编码")
    private String sensorCode;

    @Schema(description = "数据来源")
    private String dataSource;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
