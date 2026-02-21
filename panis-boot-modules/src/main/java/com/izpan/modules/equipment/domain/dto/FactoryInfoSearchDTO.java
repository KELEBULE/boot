package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "FactoryInfoSearchDTO", description = "工厂查询 DTO 对象")
public class FactoryInfoSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工厂编码")
    private String factoryCode;

    @Schema(description = "工厂名称")
    private String factoryName;

    @Schema(description = "状态")
    private Integer status;
}
