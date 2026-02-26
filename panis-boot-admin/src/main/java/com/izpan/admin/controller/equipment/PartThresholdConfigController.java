package com.izpan.admin.controller.equipment;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.izpan.common.api.Result;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigAddDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDeleteDTO;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigUpdateDTO;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;
import com.izpan.modules.equipment.facade.IPartThresholdConfigFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "部件阈值配置管理")
@RequiredArgsConstructor
@RequestMapping("/part_threshold_config")
public class PartThresholdConfigController {

    private final IPartThresholdConfigFacade partThresholdConfigFacade;

    @GetMapping("/list/{partId}")
    @SaCheckPermission("part:threshold:list")
    @Operation(summary = "根据部件ID获取阈值配置列表")
    public Result<List<PartThresholdConfigVO>> listByPartId(@Parameter(description = "部件ID") @PathVariable("partId") Long partId) {
        return Result.data(partThresholdConfigFacade.listByPartId(partId));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("part:threshold:get")
    @Operation(summary = "根据ID获取阈值配置详细信息")
    public Result<PartThresholdConfigVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(partThresholdConfigFacade.getById(id));
    }

    @PostMapping("/")
    @SaCheckPermission("part:threshold:add")
    @Operation(summary = "新增部件阈值配置")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid PartThresholdConfigAddDTO addDTO) {
        return Result.status(partThresholdConfigFacade.addConfig(addDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("part:threshold:update")
    @Operation(summary = "更新部件阈值配置")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid PartThresholdConfigUpdateDTO updateDTO) {
        return Result.status(partThresholdConfigFacade.updateConfig(updateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("part:threshold:delete")
    @Operation(summary = "批量删除部件阈值配置")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody PartThresholdConfigDeleteDTO deleteDTO) {
        return Result.status(partThresholdConfigFacade.deleteConfig(deleteDTO));
    }
}
