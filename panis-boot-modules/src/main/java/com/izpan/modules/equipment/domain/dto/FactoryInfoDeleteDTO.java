package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "FactoryInfoDeleteDTO", description = "工厂删除 DTO 对象")
public class FactoryInfoDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工厂ID列表")
    private List<Long> ids;
}
