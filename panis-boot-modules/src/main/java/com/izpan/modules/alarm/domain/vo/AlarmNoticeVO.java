package com.izpan.modules.alarm.domain.vo;

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
@Schema(name = "AlarmNoticeVO", description = "告警通知 VO 对象")
public class AlarmNoticeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "告警规则ID")
    private Long ruleId;

    @Schema(description = "告警规则名称")
    private String ruleName;

    @Schema(description = "设备告警ID")
    private Long alarmId;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "告警类型")
    private Integer alarmType;

    @Schema(description = "告警等级(1:一级 2:二级 3:三级)")
    private Integer alarmLevel;

    @Schema(description = "告警消息内容")
    private String alarmMessage;

    @Schema(description = "当前值")
    private String currentValue;

    @Schema(description = "阈值")
    private String thresholdValue;

    @Schema(description = "通知用户ID")
    private Long notifyUserId;

    @Schema(description = "通知用户名称")
    private String notifyUserName;

    @Schema(description = "通知状态(0:待发送 1:已发送 2:发送失败)")
    private Integer notifyStatus;

    @Schema(description = "通知时间")
    private LocalDateTime notifyTime;

    @Schema(description = "阅读状态(0:未读 1:已读)")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
