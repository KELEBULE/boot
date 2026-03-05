package com.izpan.modules.equipment.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "DeviceScrapDTO", description = "设备报废 DTO 对象")
public class DeviceScrapDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "设备ID列表不能为空")
    @Schema(description = "设备ID列表")
    private List<Long> deviceIds;

    @NotEmpty(message = "备注信息不能为空")
    @Size(min = 20, max = 500, message = "备注信息长度必须在20-500字之间")
    @Schema(description = "报废原因（20-500字）")
    private String changeReason;

    @Size(max = 3, message = "最多上传3张图片")
    @Schema(description = "图片URL列表（最多3张）")
    private List<String> imageUrls;
}
