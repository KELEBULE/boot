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
@TableName("alarm_notice")
public class AlarmNotice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private String ruleName;

    private Long alarmId;

    private Long deviceId;

    private String deviceName;

    private String deviceCode;

    private Integer alarmType;

    private Integer alarmLevel;

    private String alarmMessage;

    private String currentValue;

    private String thresholdValue;

    private Long notifyUserId;

    private String notifyUserName;

    private Integer notifyStatus;

    private LocalDateTime notifyTime;

    private Integer readStatus;

    private LocalDateTime readTime;

    private LocalDateTime createTime;
}
