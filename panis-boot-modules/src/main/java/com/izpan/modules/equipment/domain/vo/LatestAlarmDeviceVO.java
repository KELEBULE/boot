package com.izpan.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LatestAlarmDeviceVO", description = "最新报警设备信息 VO")
public class LatestAlarmDeviceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "3D模型URL")
    private String modelUrl;

    @Schema(description = "设备图片URL")
    private String imageUrl;

    @Schema(description = "报警部件ID")
    private Long alarmPartId;

    @Schema(description = "报警部件编码")
    private String alarmPartCode;

    @Schema(description = "报警部件名称")
    private String alarmPartName;

    @Schema(description = "报警部件模型节点名称")
    private String alarmPartModelNodeName;

    @Schema(description = "报警级别")
    private Integer alarmLevel;

    @Schema(description = "报警时间")
    private String alarmTime;

    @Schema(description = "是否有模型")
    private Boolean hasModel;
}
