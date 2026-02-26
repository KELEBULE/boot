package com.izpan.admin.controller.equipment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.equipment.domain.dto.FactoryAreaAddDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaDeleteDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaSearchDTO;
import com.izpan.modules.equipment.domain.dto.FactoryAreaUpdateDTO;
import com.izpan.modules.equipment.domain.vo.FactoryAreaVO;
import com.izpan.modules.equipment.facade.IFactoryAreaFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "厂区管理")
@RequiredArgsConstructor
@RequestMapping("/factory_area")
public class FactoryAreaController {

    private final IFactoryAreaFacade factoryAreaFacade;

    @GetMapping("/page")
    @SaCheckPermission("factory:area:page")
    @Operation(summary = "获取厂区分页列表")
    public Result<RPage<FactoryAreaVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") FactoryAreaSearchDTO searchDTO) {
        return Result.data(factoryAreaFacade.listFactoryAreaPage(pageQuery, searchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("factory:area:get")
    @Operation(summary = "根据ID获取厂区详细信息")
    public Result<FactoryAreaVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(factoryAreaFacade.getFactoryAreaById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有启用的厂区列表")
    public Result<List<FactoryAreaVO>> list() {
        return Result.data(factoryAreaFacade.listAllFactoryArea());
    }

    @PostMapping("/")
    @SaCheckPermission("factory:area:add")
    @Operation(summary = "新增厂区")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid FactoryAreaAddDTO addDTO) {
        return Result.status(factoryAreaFacade.addFactoryArea(addDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("factory:area:update")
    @Operation(summary = "更新厂区")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid FactoryAreaUpdateDTO updateDTO) {
        return Result.status(factoryAreaFacade.updateFactoryArea(updateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("factory:area:delete")
    @Operation(summary = "批量删除厂区")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody FactoryAreaDeleteDTO deleteDTO) {
        return Result.status(factoryAreaFacade.deleteFactoryArea(deleteDTO));
    }
}
