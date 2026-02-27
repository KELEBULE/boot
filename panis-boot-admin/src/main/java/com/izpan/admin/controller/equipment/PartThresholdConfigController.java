package com.izpan.admin.controller.equipment;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.izpan.common.api.Result;
import com.izpan.modules.equipment.domain.dto.PartThresholdConfigDTO;
import com.izpan.modules.equipment.domain.vo.PartThresholdConfigVO;
import com.izpan.modules.equipment.facade.IPartThresholdConfigFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "部件阈值配置管理")
@RequiredArgsConstructor
@RequestMapping("/part_threshold_config")
public class PartThresholdConfigController {

    private final IPartThresholdConfigFacade partThresholdConfigFacade;

    @GetMapping("/{partId}")
    @SaCheckPermission("part:threshold:get")
    @Operation(summary = "根据部件ID获取阈值配置")
    public Result<PartThresholdConfigVO> getByPartId(@Parameter(description = "部件ID") @PathVariable("partId") Long partId) {
        return Result.data(partThresholdConfigFacade.getByPartId(partId));
    }

    @PostMapping("/")
    @SaCheckPermission("part:threshold:save")
    @Operation(summary = "保存或更新部件阈值配置")
    public Result<Boolean> saveOrUpdate(@Parameter(description = "配置对象") @RequestBody @Valid PartThresholdConfigDTO dto) {
        return Result.status(partThresholdConfigFacade.saveOrUpdateConfig(dto));
    }
}
