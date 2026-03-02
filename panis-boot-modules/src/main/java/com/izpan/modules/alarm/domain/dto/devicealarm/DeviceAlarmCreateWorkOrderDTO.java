package com.izpan.modules.alarm.domain.dto.devicealarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "DeviceAlarmCreateWorkOrderDTO", description = "设备报警创建工单 DTO")
public class DeviceAlarmCreateWorkOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "报警ID不能为空")
    @Schema(description = "报警ID")
    private Long alarmId;

    @Schema(description = "工单类型: 1-维修, 2-保养, 3-巡检, 4-校准")
    private Integer orderType;

    @Schema(description = "故障时间")
    private LocalDateTime faultTime;

    @Schema(description = "故障描述")
    private String faultDescription;

    @Schema(description = "维修要求")
    private String repairRequirement;

    @Schema(description = "优先级: 1-紧急, 2-高, 3-中, 4-低")
    private Integer priority;

    @Schema(description = "指派人ID")
    private Long assigneeId;
}
