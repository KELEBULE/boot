package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "FactoryAreaUpdateDTO", description = "厂区更新 DTO 对象")
public class FactoryAreaUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "区域ID")
    private Long areaId;

    @Schema(description = "位置编码")
    private String locationCode;

    @Schema(description = "区域名称")
    private String areaName;

    @Schema(description = "工厂ID")
    private Long factoryId;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "区域类型")
    private Integer areaType;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "区域状态")
    private Integer areaStatus;

    @Schema(description = "排序")
    private Integer areaOrder;
}
