package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "DevicePartVO", description = "设备部件 VO 对象")
public class DevicePartVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识(用于前端选择)")
    @JsonProperty("uniqueKey")
    private String uniqueKey;

    @Schema(description = "部件ID")
    @JsonProperty("partId")
    private Long partId;

    @Schema(description = "部件编号")
    @JsonProperty("partCode")
    private String partCode;

    @Schema(description = "部件名称")
    @JsonProperty("partName")
    private String partName;

    @Schema(description = "设备ID")
    @JsonProperty("deviceId")
    private Long deviceId;

    @Schema(description = "部件类型")
    @JsonProperty("partType")
    private String partType;

    @Schema(description = "是否监控 0-否 1-是")
    @JsonProperty("monitorEnabled")
    private Integer monitorEnabled;

    @Schema(description = "部件状态 0-禁用 1-启用")
    @JsonProperty("partStatus")
    private Integer partStatus;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
