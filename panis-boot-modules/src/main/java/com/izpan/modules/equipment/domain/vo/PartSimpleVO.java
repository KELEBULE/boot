package com.izpan.modules.equipment.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部件简要信息 VO
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @CreateTime 2026-02-23
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PartSimpleVO", description = "部件简要信息 VO")
public class PartSimpleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识 (part_部件ID)")
    private String uniqueKey;

    @Schema(description = "部件ID")
    private Long id;

    @Schema(description = "部件名称")
    private String name;

    @Schema(description = "部件编码")
    private String code;

    @Schema(description = "类型: part")
    private String type;

    @Schema(description = "所属设备ID")
    private Long deviceId;
}
