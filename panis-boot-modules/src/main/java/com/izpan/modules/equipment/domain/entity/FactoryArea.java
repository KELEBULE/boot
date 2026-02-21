package com.izpan.modules.equipment.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("factory_area")
public class FactoryArea implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("areaId")
    private Long areaId;

    @JsonProperty("locationCode")
    private String locationCode;

    @JsonProperty("areaName")
    private String areaName;

    @JsonProperty("factoryId")
    private Long factoryId;

    @JsonProperty("parentId")
    private Long parentId;

    @JsonProperty("areaType")
    private Integer areaType;

    @JsonProperty("longitude")
    private BigDecimal longitude;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    @JsonProperty("areaStatus")
    private Integer areaStatus;

    @JsonProperty("areaOrder")
    private Integer areaOrder;

    @JsonProperty("createTime")
    private LocalDateTime createTime;
}
