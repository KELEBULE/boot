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
@TableName("factory_device")
public class FactoryDevice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("deviceId")
    private Long deviceId;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("deviceModel")
    private String deviceModel;

    @JsonProperty("typeId")
    private Long typeId;

    @JsonProperty("locationId")
    private Long locationId;

    @JsonProperty("installTime")
    private LocalDateTime installTime;

    @JsonProperty("startUseTime")
    private LocalDateTime startUseTime;

    @JsonProperty("scrapTime")
    private LocalDateTime scrapTime;

    @JsonProperty("deviceStatus")
    private Integer deviceStatus;

    @JsonProperty("scrapStatus")
    private Integer scrapStatus;

    @JsonProperty("totalWorkHours")
    private Integer totalWorkHours;

    @JsonProperty("lastOnlineTime")
    private LocalDateTime lastOnlineTime;

    @JsonProperty("deviceSn")
    private String deviceSn;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("maintainCycle")
    private Integer maintainCycle;

    @JsonProperty("lastMaintainTime")
    private LocalDateTime lastMaintainTime;

    @JsonProperty("warrantyPeriod")
    private Integer warrantyPeriod;

    @JsonProperty("deviceNote")
    private String deviceNote;

    @JsonProperty("modelUrl")
    private String modelUrl;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
