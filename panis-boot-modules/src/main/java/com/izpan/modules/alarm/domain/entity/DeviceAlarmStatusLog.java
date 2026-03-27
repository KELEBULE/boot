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
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_alarm_status_log")
public class DeviceAlarmStatusLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    private Long alarmId;

    private String alarmCode;

    private Integer beforeStatus;

    private Integer afterStatus;

    private Integer operateType;

    private Long operateUserId;

    private String operateUserName;

    private LocalDateTime operateTime;

    private Integer operateSource;

    private String operateRemark;

    private LocalDateTime createTime;
}
