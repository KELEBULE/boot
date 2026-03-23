package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MonitorDeviceTreeVO", description = "监控中心设备树形结构 VO")
public class MonitorDeviceTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识")
    private String uniqueKey;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "类型: factory-工厂, area-厂区, device-设备, part-部件")
    private String type;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "所属设备ID(仅部件有)")
    private Long deviceId;

    @Schema(description = "3D模型URL(仅设备有)")
    private String modelUrl;

    @Schema(description = "设备图片URL(仅设备有)")
    private String imageUrl;

    @Schema(description = "设备状态(仅设备有)")
    private Integer deviceStatus;

    @Schema(description = "部件状态(仅部件有)")
    private Integer partStatus;

    @Schema(description = "是否有报警")
    private Boolean hasAlarm;

    @Schema(description = "子节点列表")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MonitorDeviceTreeVO> children;
}
