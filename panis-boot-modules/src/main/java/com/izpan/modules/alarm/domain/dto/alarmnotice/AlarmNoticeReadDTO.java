package com.izpan.modules.alarm.domain.dto.alarmnotice;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "AlarmNoticeReadDTO", description = "告警通知 标记已读 DTO 对象")
public class AlarmNoticeReadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "通知ID列表")
    private List<Long> ids;

    @Schema(description = "通知用户ID")
    private Long notifyUserId;
}
