package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "FactoryAreaDeleteDTO", description = "厂区删除 DTO 对象")
public class FactoryAreaDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "区域ID列表")
    private List<Long> ids;
}
