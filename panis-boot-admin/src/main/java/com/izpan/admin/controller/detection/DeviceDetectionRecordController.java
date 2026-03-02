package com.izpan.admin.controller.detection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.detection.domain.dto.devicedetection.DeviceDetectionRecordSearchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataBatchDTO;
import com.izpan.modules.detection.domain.dto.devicedetection.DetectionDataDTO;
import com.izpan.modules.detection.domain.vo.DeviceDetectionRecordVO;
import com.izpan.modules.detection.facade.IDeviceDetectionRecordFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "设备检测记录管理")
@RequiredArgsConstructor
@RequestMapping("/device_detection_record")
public class DeviceDetectionRecordController {

    @NonNull
    private IDeviceDetectionRecordFacade deviceDetectionRecordFacade;

    @GetMapping("/page")
    @SaCheckPermission("detection:record:page")
    @Operation(operationId = "1", summary = "获取设备检测记录列表")
    public Result<RPage<DeviceDetectionRecordVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") DeviceDetectionRecordSearchDTO deviceDetectionRecordSearchDTO) {
        return Result.data(deviceDetectionRecordFacade.listDeviceDetectionRecordPage(pageQuery, deviceDetectionRecordSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("detection:record:get")
    @Operation(operationId = "2", summary = "根据ID获取设备检测记录详细信息")
    public Result<DeviceDetectionRecordVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(deviceDetectionRecordFacade.get(id));
    }

    @PostMapping("/receive")
    @Operation(operationId = "3", summary = "接收单条检测数据")
    public Result<Void> receiveDetectionData(@Parameter(description = "检测数据") @RequestBody DetectionDataDTO dto) {
        deviceDetectionRecordFacade.receiveDetectionData(dto);
        return Result.success();
    }

    @PostMapping("/receive/batch")
    @Operation(operationId = "4", summary = "批量接收检测数据")
    public Result<Void> receiveDetectionDataBatch(@Parameter(description = "批量检测数据") @RequestBody DetectionDataBatchDTO batchDTO) {
        deviceDetectionRecordFacade.receiveDetectionDataBatch(batchDTO);
        return Result.success();
    }
}
