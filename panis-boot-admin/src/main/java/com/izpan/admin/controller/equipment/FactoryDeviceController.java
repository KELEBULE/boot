package com.izpan.admin.controller.equipment;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryDeviceUpdateDTO;
import com.izpan.modules.equipment.domain.vo.FactoryDeviceVO;
import com.izpan.modules.equipment.facade.IFactoryDeviceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "工厂设备管理")
@RequiredArgsConstructor
@RequestMapping("/factory_device")
public class FactoryDeviceController {

    private final IFactoryDeviceFacade factoryDeviceFacade;

    @GetMapping("/page")
    @SaCheckRole("ADMIN")
    @Operation(summary = "获取工厂设备分页列表")
    public Result<RPage<FactoryDeviceVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                               @Parameter(description = "查询对象") FactoryDeviceSearchDTO searchDTO) {
        return Result.data(factoryDeviceFacade.listFactoryDevicePage(pageQuery, searchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckRole("ADMIN")
    @Operation(summary = "根据ID获取工厂设备详细信息")
    public Result<FactoryDeviceVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(factoryDeviceFacade.getFactoryDeviceById(id));
    }

    @PostMapping("/")
    @SaCheckRole("ADMIN")
    @Operation(summary = "新增工厂设备")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid FactoryDeviceAddDTO addDTO) {
        return Result.status(factoryDeviceFacade.addFactoryDevice(addDTO));
    }

    @PutMapping("/")
    @SaCheckRole("ADMIN")
    @Operation(summary = "更新工厂设备")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid FactoryDeviceUpdateDTO updateDTO) {
        return Result.status(factoryDeviceFacade.updateFactoryDevice(updateDTO));
    }

    @DeleteMapping("/")
    @SaCheckRole("ADMIN")
    @Operation(summary = "批量删除工厂设备")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody FactoryDeviceDeleteDTO deleteDTO) {
        return Result.status(factoryDeviceFacade.deleteFactoryDevice(deleteDTO));
    }
}
