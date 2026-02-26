package com.izpan.admin.controller.workorder;

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
import com.izpan.modules.workorder.domain.dto.WorkOrderAddDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderDeleteDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderFlowDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderSearchDTO;
import com.izpan.modules.workorder.domain.dto.WorkOrderUpdateDTO;
import com.izpan.modules.workorder.domain.vo.WorkOrderLogVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderStatisticsVO;
import com.izpan.modules.workorder.domain.vo.WorkOrderVO;
import com.izpan.modules.workorder.facade.IWorkOrderFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @RequestMapping(value = "/page", method = {RequestMethod.GET, RequestMethod.POST})
    @SaCheckPermission("device:work:order:page")
    @RepeatSubmit(interval = -1)
    @Operation(summary = "获取设备工单列表")
    public Result<RPage<WorkOrderVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") WorkOrderSearchDTO workOrderSearchDTO) {
        return Result.data(workOrderFacade.listWorkOrderPage(pageQuery, workOrderSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("device:work:order:get")
    @Operation(summary = "根据ID获取设备工单详细信息")
    public Result<WorkOrderVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(workOrderFacade.getWorkOrderById(id));
    }

    @PostMapping("/")
    @SaCheckPermission("device:work:order:add")
    @Operation(summary = "新增设备工单")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody @Valid WorkOrderAddDTO workOrderAddDTO) {
        return Result.status(workOrderFacade.addWorkOrder(workOrderAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("device:work:order:update")
    @Operation(summary = "更新设备工单")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody @Valid WorkOrderUpdateDTO workOrderUpdateDTO) {
        return Result.status(workOrderFacade.updateWorkOrder(workOrderUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("device:work:order:delete")
    @Operation(summary = "批量删除设备工单")
    public Result<Boolean> delete(@Parameter(description = "删除对象") @RequestBody WorkOrderDeleteDTO workOrderDeleteDTO) {
        return Result.status(workOrderFacade.deleteWorkOrder(workOrderDeleteDTO));
    }

    @PostMapping("/flow")
    @SaCheckPermission("device:work:order:flow")
    @Operation(summary = "工单流转（状态变更、指派处理人等）")
    public Result<Boolean> flow(@Parameter(description = "流转对象") @RequestBody @Valid WorkOrderFlowDTO workOrderFlowDTO) {
        return Result.status(workOrderFacade.flowWorkOrder(workOrderFlowDTO));
    }

    @GetMapping("/logs/{orderId}")
    @SaCheckPermission("device:work:order:logs")
    @Operation(summary = "获取工单流转日志")
    public Result<List<WorkOrderLogVO>> logs(@Parameter(description = "工单ID") @PathVariable("orderId") Long orderId) {
        return Result.data(workOrderFacade.getWorkOrderLogs(orderId));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取当前用户本月工单统计数据")
    public Result<WorkOrderStatisticsVO> statistics() {
        return Result.data(workOrderFacade.getStatistics());
    }
}
