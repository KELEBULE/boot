package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "FactoryAreaSearchDTO", description = "厂区查询 DTO 对象")
public class FactoryAreaSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "位置编码")
    private String locationCode;

    @Schema(description = "区域名称")
    private String areaName;

    @Schema(description = "工厂ID")
    private Long factoryId;

    @Schema(description = "区域状态")
    private Integer areaStatus;
}
