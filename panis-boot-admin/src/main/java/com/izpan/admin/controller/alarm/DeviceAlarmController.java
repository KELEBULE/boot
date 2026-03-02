package com.izpan.admin.controller.alarm;

import java.util.List;

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
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmConfirmDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmCreateWorkOrderDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmExportDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmSearchDTO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;
import com.izpan.modules.alarm.facade.IDeviceAlarmFacade;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.idev.excel.EasyExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@Tag(name = "设备报警记录管理")
@RequiredArgsConstructor
@RequestMapping("/device_alarm")
public class DeviceAlarmController {

    @NonNull
    private IDeviceAlarmFacade deviceAlarmFacade;

    @GetMapping("/page")
    @SaCheckPermission("alarm:device:page")
    @Operation(operationId = "1", summary = "获取设备报警记录列表")
    public Result<RPage<DeviceAlarmVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
            @Parameter(description = "查询对象") DeviceAlarmSearchDTO deviceAlarmSearchDTO) {
        return Result.data(deviceAlarmFacade.listDeviceAlarmPage(pageQuery, deviceAlarmSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("alarm:device:get")
    @Operation(operationId = "2", summary = "根据ID获取设备报警记录详细信息")
    public Result<DeviceAlarmVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(deviceAlarmFacade.get(id));
    }

    @PutMapping("/confirm")
    @SaCheckPermission("alarm:device:confirm")
    @Operation(operationId = "3", summary = "确认报警")
    public Result<Boolean> confirm(@Parameter(description = "确认对象") @RequestBody DeviceAlarmConfirmDTO deviceAlarmConfirmDTO) {
        return Result.status(deviceAlarmFacade.confirm(deviceAlarmConfirmDTO));
    }

    @PostMapping("/create_work_order")
    @SaCheckPermission("alarm:device:handle")
    @Operation(operationId = "4", summary = "创建工单")
    public Result<Boolean> createWorkOrder(@Parameter(description = "创建工单对象") @RequestBody DeviceAlarmCreateWorkOrderDTO deviceAlarmCreateWorkOrderDTO) {
        return Result.status(deviceAlarmFacade.createWorkOrder(deviceAlarmCreateWorkOrderDTO));
    }

    @SneakyThrows
    @PostMapping("/export")
    @SaCheckPermission("alarm:device:export")
    @Operation(operationId = "5", summary = "导出报警记录")
    public void export(@RequestBody DeviceAlarmExportDTO deviceAlarmExportDTO, HttpServletResponse response) {
        List<DeviceAlarmExportVO> exportList = deviceAlarmFacade.queryExportList(deviceAlarmExportDTO.getIds());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("报警记录", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), DeviceAlarmExportVO.class)
                .sheet("报警记录")
                .doWrite(exportList);
    }
}
