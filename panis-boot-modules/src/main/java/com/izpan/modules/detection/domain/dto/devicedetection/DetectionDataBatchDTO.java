package com.izpan.modules.detection.domain.dto.devicedetection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DetectionDataBatchDTO", description = "批量检测数据接收 DTO")
public class DetectionDataBatchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "检测数据列表")
    private List<DetectionDataDTO> dataList;
}
