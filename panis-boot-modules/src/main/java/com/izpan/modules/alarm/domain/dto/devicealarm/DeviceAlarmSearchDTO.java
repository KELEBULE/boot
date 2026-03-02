package com.izpan.modules.alarm.domain.dto.devicealarm;

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
@Schema(name = "DeviceAlarmSearchDTO", description = "设备报警查询 DTO")
public class DeviceAlarmSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报警编码")
    private String alarmCode;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "报警类型")
    private Integer alarmType;

    @Schema(description = "报警级别")
    private Integer alarmLevel;

    @Schema(description = "报警时间开始")
    private LocalDateTime alarmTimeStart;

    @Schema(description = "报警时间结束")
    private LocalDateTime alarmTimeEnd;

    @Schema(description = "确认状态")
    private Integer confirmStatus;

    @Schema(description = "清除状态")
    private Integer clearStatus;
}
