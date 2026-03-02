package com.izpan.modules.detection.domain.bo;

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
public class DeviceDetectionRecordBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long recordId;

    private Long deviceId;

    private Long partId;

    private Integer detectStatus;

    private Integer isFalseAlarm;

    private LocalDateTime detectTimeStart;

    private LocalDateTime detectTimeEnd;

    private String sensorCode;
}
