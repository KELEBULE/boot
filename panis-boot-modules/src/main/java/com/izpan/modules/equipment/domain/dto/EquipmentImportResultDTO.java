package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "EquipmentImportResultDTO", description = "设备导入结果 DTO 对象")
public class EquipmentImportResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "总记录数")
    private Integer totalCount;

    @Schema(description = "成功数")
    private Integer successCount;

    @Schema(description = "失败数")
    private Integer failCount;

    @Schema(description = "新增工厂数")
    private Integer factoryCount;

    @Schema(description = "新增厂区数")
    private Integer areaCount;

    @Schema(description = "新增设备数")
    private Integer deviceCount;

    @Schema(description = "新增部件数")
    private Integer partCount;

    @Schema(description = "错误信息列表")
    private List<String> errorMessages;
}
