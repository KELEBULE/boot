package com.izpan.admin.controller.equipment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryInfoAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryInfoUpdateDTO;
import com.izpan.modules.equipment.domain.vo.FactoryAreaTreeVO;
import com.izpan.modules.equipment.domain.vo.FactoryInfoVO;
import com.izpan.modules.equipment.domain.vo.LatestAlarmDeviceVO;
import com.izpan.modules.equipment.domain.vo.MonitorDeviceTreeVO;
import com.izpan.modules.equipment.facade.IFactoryInfoFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "工厂管理")
@RequiredArgsConstructor
@RequestMapping("/factory_info")
public class FactoryInfoController {

    private final IFactoryInfoFacade factoryInfoFacade;

    @GetMapping("/page")
    @SaCheckPermission("factory:info:page")
    @Operation(summary = "获取工厂分页列表")
    public Result<RPage<FactoryInfoVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") FactoryInfoSearchDTO searchDTO) {
        return Result.data(factoryInfoFacade.listFactoryInfoPage(pageQuery, searchDTO));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有启用的工厂列表")
    public Result<List<FactoryInfoVO>> list() {
        return Result.data(factoryInfoFacade.listAllFactoryInfo());
    }

    @GetMapping("/tree")
    @Operation(summary = "获取工厂-厂区树形结构")
    public Result<List<FactoryAreaTreeVO>> tree() {
        return Result.data(factoryInfoFacade.getFactoryAreaTree());
    }

    @GetMapping("/monitor_tree")
    @Operation(summary = "获取监控中心设备树形结构(工厂-厂区-设备-部件)")
    public Result<List<MonitorDeviceTreeVO>> monitorTree(
            @Parameter(description = "工厂ID") @RequestParam(required = false) Long factoryId,
            @Parameter(description = "厂区ID") @RequestParam(required = false) Long areaId,
            @Parameter(description = "设备ID") @RequestParam(required = false) Long deviceId) {
        return Result.data(factoryInfoFacade.getMonitorDeviceTree(factoryId, areaId, deviceId));
    }

    @GetMapping("/latest_alarm_device")
    @Operation(summary = "获取最新报警设备信息(用于监控中心默认显示)")
    public Result<LatestAlarmDeviceVO> latestAlarmDevice() {
        return Result.data(factoryInfoFacade.getLatestAlarmDevice());
    }

    @GetMapping("/{id}")
    @SaCheckPermission("factory:info:get")
    @Operation(summary = "根据ID获取工厂详细信息")
    public Result<FactoryInfoVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(factoryInfoFacade.getFactoryInfoById(id));
    }

    @PostMapping("/")
    @SaCheckPermission("factory:info:add")
    @Operation(summary = "新增工厂")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid FactoryInfoAddDTO addDTO) {
        return Result.status(factoryInfoFacade.addFactoryInfo(addDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("factory:info:update")
    @Operation(summary = "更新工厂")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid FactoryInfoUpdateDTO updateDTO) {
        return Result.status(factoryInfoFacade.updateFactoryInfo(updateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("factory:info:delete")
    @Operation(summary = "批量删除工厂")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody FactoryInfoDeleteDTO deleteDTO) {
        return Result.status(factoryInfoFacade.deleteFactoryInfo(deleteDTO));
    }
}
