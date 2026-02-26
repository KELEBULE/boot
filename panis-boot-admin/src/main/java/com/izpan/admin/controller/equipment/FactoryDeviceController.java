package com.izpan.admin.controller.equipment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.annotation.RepeatSubmit;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.vo.DevicePartTreeVO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;
import com.izpan.modules.equipment.facade.IFactoryDeviceFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "工厂设备管理")
@RequiredArgsConstructor
@RequestMapping("/factory_device")
public class FactoryDeviceController {

    private final IFactoryDeviceFacade factoryDeviceFacade;

    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    @SaCheckPermission("factory:device:page")
    @RepeatSubmit(interval = -1)
    @Operation(summary = "获取工厂设备分页列表")
    public Result<RPage<FactoryDeviceVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") FactoryDeviceSearchDTO searchDTO) {
        return Result.data(factoryDeviceFacade.listFactoryDevicePage(pageQuery, searchDTO));
    }

    @GetMapping("/tree/{locationId}")
    @Operation(summary = "根据厂区ID获取设备-部件树形结构")
    public Result<List<DevicePartTreeVO>> tree(@Parameter(description = "厂区ID") @PathVariable("locationId") Long locationId) {
        return Result.data(factoryDeviceFacade.getDevicePartTreeByLocationId(locationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取工厂设备详细信息")
    public Result<FactoryDeviceVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(factoryDeviceFacade.getFactoryDeviceById(id));
    }

    @PostMapping("/")
    @Operation(summary = "新增工厂设备")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid FactoryDeviceAddDTO addDTO) {
        return Result.status(factoryDeviceFacade.addFactoryDevice(addDTO));
    }

    @PutMapping("/")
    @Operation(summary = "更新工厂设备")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid FactoryDeviceUpdateDTO updateDTO) {
        return Result.status(factoryDeviceFacade.updateFactoryDevice(updateDTO));
    }

    @DeleteMapping("/")
    @Operation(summary = "批量删除工厂设备")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody FactoryDeviceDeleteDTO deleteDTO) {
        return Result.status(factoryDeviceFacade.deleteFactoryDevice(deleteDTO));
    }
}
