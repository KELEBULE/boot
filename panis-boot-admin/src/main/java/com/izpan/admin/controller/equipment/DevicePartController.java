package com.izpan.admin.controller.equipment;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.izpan.common.api.Result;
import com.izpan.modules.equipment.domain.dto.DevicePartAddDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartDeleteDTO;
import com.izpan.modules.equipment.domain.dto.DevicePartUpdateDTO;
import com.izpan.modules.equipment.domain.vo.DevicePartVO;
import com.izpan.modules.equipment.facade.IDevicePartFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "设备部件管理")
@RequiredArgsConstructor
@RequestMapping("/device_part")
public class DevicePartController {

    private final IDevicePartFacade devicePartFacade;

    @GetMapping("/list/{deviceId}")
    @SaCheckPermission("device:part:list")
    @Operation(summary = "根据设备ID获取部件列表")
    public Result<List<DevicePartVO>> listByDeviceId(@Parameter(description = "设备ID") @PathVariable("deviceId") Long deviceId) {
        return Result.data(devicePartFacade.listDevicePartByDeviceId(deviceId));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("device:part:get")
    @Operation(summary = "根据ID获取设备部件详细信息")
    public Result<DevicePartVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(devicePartFacade.getDevicePartById(id));
    }

    @PostMapping("/")
    @SaCheckPermission("device:part:add")
    @Operation(summary = "新增设备部件")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid DevicePartAddDTO addDTO) {
        return Result.status(devicePartFacade.addDevicePart(addDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("device:part:update")
    @Operation(summary = "更新设备部件")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid DevicePartUpdateDTO updateDTO) {
        return Result.status(devicePartFacade.updateDevicePart(updateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("device:part:delete")
    @Operation(summary = "批量删除设备部件")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody DevicePartDeleteDTO deleteDTO) {
        return Result.status(devicePartFacade.deleteDevicePart(deleteDTO));
    }
}
