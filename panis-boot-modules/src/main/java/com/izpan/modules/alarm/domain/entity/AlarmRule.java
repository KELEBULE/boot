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
import java.time.LocalTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("alarm_rule")
public class AlarmRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "rule_id", type = IdType.AUTO)
    private Long ruleId;

    private String ruleCode;

    private String ruleName;

    private String deviceIds;

    private String alarmLevels;

    private String notifyTargetIds;

    private LocalTime pushStartTime;

    private LocalTime pushEndTime;

    private Integer pushInterval;

    private Integer ruleStatus;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
