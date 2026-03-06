package com.izpan.modules.alarm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AlarmPushVO", description = "报警推送 VO 对象")
public class AlarmPushVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报警ID")
    private Long alarmId;

    @Schema(description = "报警编码")
    private String alarmCode;

    @Schema(description = "报警等级")
    private Integer alarmLevel;

    @Schema(description = "报警等级名称")
    private String alarmLevelName;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "部件名称")
    private String partName;

    @Schema(description = "报警时间")
    private LocalDateTime alarmTime;

    @Schema(description = "监测温度(当前值)")
    private BigDecimal currentValue;

    @Schema(description = "阈值")
    private BigDecimal thresholdValue;

    @Schema(description = "报警消息")
    private String alarmMessage;

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "推送时间")
    private LocalDateTime pushTime;
}
