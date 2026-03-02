package com.izpan.modules.detection.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("device_detection_record")
public class DeviceDetectionRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    private Long deviceId;

    private Long partId;

    private Long thresholdId;

    private BigDecimal detectValue;

    private Integer detectStatus;

    private BigDecimal level1Value;

    private BigDecimal level2Value;

    private BigDecimal level3Value;

    private Integer isFalseAlarm;

    private Long alarmId;

    private LocalDateTime detectTime;

    private String sensorCode;

    private String dataSource;

    private LocalDateTime createTime;
}
