package com.izpan.modules.alarm.domain.vo;

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
@Schema(name = "DeviceAlarmVO", description = "设备报警 VO 对象")
public class DeviceAlarmVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报警ID")
    private Long alarmId;

    @Schema(description = "报警编码")
    private String alarmCode;

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

    @Schema(description = "监测ID")
    private Long monitorId;

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "报警类型")
    private Integer alarmType;

    @Schema(description = "报警级别: 1-一级(最严重), 2-二级(中等), 3-三级(轻微)")
    private Integer alarmLevel;

    @Schema(description = "报警时间")
    private LocalDateTime alarmTime;

    @Schema(description = "当前值")
    private BigDecimal currentValue;

    @Schema(description = "阈值")
    private BigDecimal thresholdValue;

    @Schema(description = "确认人ID")
    private Long confirmUserId;

    @Schema(description = "确认人姓名")
    private String confirmUserName;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "确认状态")
    private Integer confirmStatus;

    @Schema(description = "处理人ID")
    private Long handleUserId;

    @Schema(description = "处理人姓名")
    private String handleUserName;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "清除人ID")
    private Long clearUserId;

    @Schema(description = "清除人姓名")
    private String clearUserName;

    @Schema(description = "清除时间")
    private LocalDateTime clearTime;

    @Schema(description = "清除状态")
    private Integer clearStatus;

    @Schema(description = "报警持续时长(秒)")
    private Integer alarmDuration;

    @Schema(description = "是否误报: 0-否, 1-是")
    private Integer isFalseAlarm;

    @Schema(description = "工单ID")
    private Long workOrderId;

    @Schema(description = "工单编号")
    private String workOrderCode;

    @Schema(description = "工单状态")
    private Integer workOrderStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
