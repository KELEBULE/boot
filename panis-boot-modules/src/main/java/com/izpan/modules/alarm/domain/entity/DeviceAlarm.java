package com.izpan.modules.alarm.domain.entity;

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
@TableName("device_alarm")
public class DeviceAlarm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "alarm_id", type = IdType.AUTO)
    private Long alarmId;

    private String alarmCode;

    private Long deviceId;

    private Long partId;

    private Long ruleId;

    private Integer alarmType;

    private Integer alarmLevel;

    private LocalDateTime alarmTime;

    private BigDecimal currentValue;

    private BigDecimal thresholdValue;

    private Long confirmUserId;

    private LocalDateTime confirmTime;

    private Integer confirmStatus;

    private Long handleUserId;

    private LocalDateTime handleTime;

    private Long clearUserId;

    private LocalDateTime clearTime;

    private Integer clearStatus;

    private Integer alarmDuration;

    private Integer isFalseAlarm;

    private Long workOrderId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
