package com.izpan.modules.alarm.domain.dto.alarmnotice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Schema(name = "AlarmNoticeSearchDTO", description = "告警通知 查询 DTO 对象")
public class AlarmNoticeSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "告警等级(1:一级 2:二级 3:三级)")
    private Integer alarmLevel;

    @Schema(description = "阅读状态(0:未读 1:已读)")
    private Integer readStatus;

    @Schema(description = "通知用户ID")
    private Long notifyUserId;
}
