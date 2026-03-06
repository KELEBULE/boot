package com.izpan.modules.alarm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AlarmRuleVO", description = "报警规则 VO 对象")
public class AlarmRuleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "设备ID列表")
    private List<Long> deviceIds;

    @Schema(description = "允许推送的告警等级列表")
    private List<Integer> alarmLevels;

    @Schema(description = "通知目标ID列表")
    private List<String> notifyTargetIds;

    @Schema(description = "推送开始时间")
    private LocalTime pushStartTime;

    @Schema(description = "推送结束时间")
    private LocalTime pushEndTime;

    @Schema(description = "重复推送间隔(分钟)")
    private Integer pushInterval;

    @Schema(description = "规则状态(0:禁用 1:启用)")
    private Integer ruleStatus;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
