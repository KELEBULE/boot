package com.izpan.modules.equipment.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FactoryAreaVO", description = "厂区 VO 对象")
public class FactoryAreaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识(用于前端选择)")
    @JsonProperty("uniqueKey")
    private String uniqueKey;

    @Schema(description = "区域ID")
    @JsonProperty("areaId")
    private Long areaId;

    @Schema(description = "位置编码")
    @JsonProperty("locationCode")
    private String locationCode;

    @Schema(description = "区域名称")
    @JsonProperty("areaName")
    private String areaName;

    @Schema(description = "工厂ID")
    @JsonProperty("factoryId")
    private Long factoryId;

    @Schema(description = "父级ID")
    @JsonProperty("parentId")
    private Long parentId;

    @Schema(description = "区域类型")
    @JsonProperty("areaType")
    private Integer areaType;

    @Schema(description = "区域状态")
    @JsonProperty("areaStatus")
    private Integer areaStatus;

    @Schema(description = "排序")
    @JsonProperty("areaOrder")
    private Integer areaOrder;

    @Schema(description = "创建时间")
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    @Schema(description = "设备列表")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FactoryDeviceVO> children;
}
