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
@Schema(name = "DeviceAlarmStatusLogVO", description = "报警状态变更日志 VO 对象")
public class DeviceAlarmStatusLogVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    private Long logId;

    @Schema(description = "报警记录ID")
    private Long alarmId;

    @Schema(description = "报警编号")
    private String alarmCode;

    @Schema(description = "变更前状态：0-未确认 1-已确认 2-已处理 3-已消除")
    private Integer beforeStatus;

    @Schema(description = "变更后状态：0-未确认 1-已确认 2-已处理 3-已消除")
    private Integer afterStatus;

    @Schema(description = "操作类型：1-确认 2-处理 3-消除 4-误报标记 5-重新激活")
    private Integer operateType;

    @Schema(description = "操作人ID")
    private Long operateUserId;

    @Schema(description = "操作人姓名")
    private String operateUserName;

    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    @Schema(description = "操作来源：1-手动 2-系统自动")
    private Integer operateSource;

    @Schema(description = "操作备注")
    private String operateRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
