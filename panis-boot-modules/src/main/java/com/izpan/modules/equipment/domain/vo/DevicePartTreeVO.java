package com.izpan.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 设备部件树形结构 VO
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @CreateTime 2026-02-22
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DevicePartTreeVO", description = "设备部件树形结构 VO")
public class DevicePartTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识 (device_设备ID)")
    private String uniqueKey;

    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备编码")
    private String code;

    @Schema(description = "类型: device")
    private String type;

    @Schema(description = "厂区ID")
    private Long locationId;

    @Schema(description = "子节点列表 (部件)")
    private List<PartSimpleVO> children;
}
