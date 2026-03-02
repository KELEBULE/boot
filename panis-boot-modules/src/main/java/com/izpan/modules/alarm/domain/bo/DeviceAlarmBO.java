package com.izpan.modules.alarm.domain.bo;

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
public class DeviceAlarmBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long alarmId;

    private String alarmCode;

    private Long deviceId;

    private Long partId;

    private Integer alarmType;

    private Integer alarmLevel;

    private LocalDateTime alarmTimeStart;

    private LocalDateTime alarmTimeEnd;

    private Integer confirmStatus;

    private Integer clearStatus;

    private Long confirmUserId;

    private Long handleUserId;

    private Long clearUserId;
}
