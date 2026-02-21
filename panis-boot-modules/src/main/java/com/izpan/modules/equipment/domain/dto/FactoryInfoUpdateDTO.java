package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "FactoryInfoUpdateDTO", description = "工厂更新 DTO 对象")
public class FactoryInfoUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工厂ID")
    private Long factoryId;

    @Schema(description = "工厂编码")
    private String factoryCode;

    @Schema(description = "工厂名称")
    private String factoryName;

    @Schema(description = "工厂地址")
    private String factoryAddress;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "状态")
    private Integer status;
}
