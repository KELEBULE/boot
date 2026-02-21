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
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("factory_info")
public class FactoryInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("factoryId")
    private Long factoryId;

    @JsonProperty("factoryCode")
    private String factoryCode;

    @JsonProperty("factoryName")
    private String factoryName;

    @JsonProperty("factoryAddress")
    private String factoryAddress;

    @JsonProperty("contactPerson")
    private String contactPerson;

    @JsonProperty("contactPhone")
    private String contactPhone;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("createTime")
    private LocalDateTime createTime;
}
