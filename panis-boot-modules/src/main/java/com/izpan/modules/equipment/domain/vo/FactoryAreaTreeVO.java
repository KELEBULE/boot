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
 * 工厂厂区树形结构 VO
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @CreateTime 2026-02-22
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FactoryAreaTreeVO", description = "工厂厂区树形结构 VO")
public class FactoryAreaTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识 (factory_工厂ID 或 area_厂区ID)")
    private String uniqueKey;

    @Schema(description = "ID (工厂ID或厂区ID)")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "类型: factory-工厂, area-厂区")
    private String type;

    @Schema(description = "子节点列表")
    private List<FactoryAreaTreeVO> children;
}
