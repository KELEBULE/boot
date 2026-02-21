package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FactoryInfoVO", description = "工厂 VO 对象")
public class FactoryInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "工厂ID")
    @JsonProperty("factoryId")
    private Long factoryId;

    @Schema(description = "工厂编码")
    @JsonProperty("factoryCode")
    private String factoryCode;

    @Schema(description = "工厂名称")
    @JsonProperty("factoryName")
    private String factoryName;

    @Schema(description = "工厂地址")
    @JsonProperty("factoryAddress")
    private String factoryAddress;

    @Schema(description = "联系人")
    @JsonProperty("contactPerson")
    private String contactPerson;

    @Schema(description = "联系电话")
    @JsonProperty("contactPhone")
    private String contactPhone;

    @Schema(description = "状态")
    @JsonProperty("status")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createTime;
}
