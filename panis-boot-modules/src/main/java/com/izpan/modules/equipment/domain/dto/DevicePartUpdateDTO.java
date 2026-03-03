package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "DevicePartUpdateDTO", description = "设备部件更新 DTO 对象")
public class DevicePartUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "部件ID")
    private Long partId;

    @Schema(description = "部件编号")
    private String partCode;

    @Schema(description = "部件名称")
    private String partName;

    @Schema(description = "设备ID")
    private Long deviceId;

    @Schema(description = "部件类型")
    private String partType;

    @Schema(description = "是否监控 0-否 1-是")
    private Integer monitorEnabled;

    @Schema(description = "安装位置")
    private String installPosition;

    @Schema(description = "部件状态 0-禁用 1-启用")
    private Integer partStatus;

    @Schema(description = "模型节点名称(用于3D模型定位)")
    private String modelNodeName;
}
