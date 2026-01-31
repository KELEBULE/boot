package com.izpan.admin.controller.workorder;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.page.RPage;
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.facade.IWorkOrderFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备工单管理控制器
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.admin.controller.workorder.WorkOrderController
 * @CreateTime 2026-01-27
 */

@RestController
@Tag(name = "设备工单管理")
@RequiredArgsConstructor
@RequestMapping("/device_work_order")
public class WorkOrderController {

    private final IWorkOrderFacade workOrderFacade;

    @GetMapping("/page")
    @SaCheckRole("ADMIN")
    @Operation(summary = "获取设备工单列表")
    public Result<RPage<WorkOrderVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                           @Parameter(description = "查询对象") WorkOrderSearchDTO workOrderSearchDTO) {
        return Result.data(workOrderFacade.listWorkOrderPage(pageQuery, workOrderSearchDTO));
    }

    @GetMapping("/{id}")
//    @SaCheckPermission("workorder:get")
    @SaCheckRole("ADMIN")

    @Operation(summary = "根据ID获取设备工单详细信息")
    public Result<WorkOrderVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(workOrderFacade.getWorkOrderById(id));
    }

    @PostMapping("/")
    @SaCheckRole("ADMIN")
//    @SaCheckPermission("workorder:add")
    @Operation(summary = "新增设备工单")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid WorkOrderAddDTO workOrderAddDTO) {
        return Result.status(workOrderFacade.addWorkOrder(workOrderAddDTO));
    }
}