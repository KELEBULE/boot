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
@TableName("part_threshold_config")
public class PartThresholdConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("partId")
    private Long partId;

    @JsonProperty("level1Operator")
    private String level1Operator;

    @JsonProperty("level1Value")
    private BigDecimal level1Value;

    @JsonProperty("level2Operator")
    private String level2Operator;

    @JsonProperty("level2Value")
    private BigDecimal level2Value;

    @JsonProperty("level3Operator")
    private String level3Operator;

    @JsonProperty("level3Value")
    private BigDecimal level3Value;

    @JsonProperty("checkInterval")
    private Integer checkInterval;

    @JsonProperty("configStatus")
    private Integer configStatus;

    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
