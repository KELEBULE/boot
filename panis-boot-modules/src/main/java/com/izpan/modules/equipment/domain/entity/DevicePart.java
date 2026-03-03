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
@TableName("device_part")
public class DevicePart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("partId")
    private Long partId;

    @JsonProperty("partCode")
    private String partCode;

    @JsonProperty("partName")
    private String partName;

    @JsonProperty("deviceId")
    private Long deviceId;

    @JsonProperty("partType")
    private String partType;

    @JsonProperty("monitorEnabled")
    private Integer monitorEnabled;

    @JsonProperty("installPosition")
    private String installPosition;

    @JsonProperty("partStatus")
    private Integer partStatus;

    @JsonProperty("modelNodeName")
    private String modelNodeName;

    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
