package com.izpan.admin.controller.alarm;

import java.util.List;

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
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmConfirmDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmCreateWorkOrderDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmExportDTO;
import com.izpan.modules.alarm.domain.dto.devicealarm.DeviceAlarmSearchDTO;
import com.izpan.modules.alarm.domain.vo.AlarmLevelDistributionVO;
import com.izpan.modules.alarm.domain.vo.DailyAlarmTrendVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmExportVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmLevelStatsVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmTopVO;
import com.izpan.modules.alarm.domain.vo.DeviceAlarmVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmPartVO;
import com.izpan.modules.alarm.domain.vo.FrequentAlarmTimeVO;
import com.izpan.modules.alarm.domain.vo.TemperatureTrendVO;
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

    @GetMapping("/detail/{id}")
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

    @GetMapping("/level_distribution")
    @Operation(operationId = "6", summary = "获取报警等级分布统计")
    public Result<AlarmLevelDistributionVO> getAlarmLevelDistribution() {
        return Result.data(deviceAlarmFacade.getAlarmLevelDistribution());
    }

    @GetMapping("/device_top")
    @Operation(operationId = "7", summary = "获取设备报警TOP排行")
    public Result<DeviceAlarmTopVO> getDeviceAlarmTop(
            @Parameter(description = "返回数量，默认10") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.data(deviceAlarmFacade.getDeviceAlarmTop(limit));
    }

    @GetMapping("/stats/frequent_parts")
    @Operation(operationId = "8", summary = "获取频繁报警部件统计")
    public Result<FrequentAlarmPartVO> getFrequentAlarmParts(
            @Parameter(description = "设备ID") @RequestParam Long deviceId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getFrequentAlarmParts(deviceId, startTime, endTime));
    }

    @GetMapping("/stats/frequent_time")
    @Operation(operationId = "9", summary = "获取频繁报警时间统计")
    public Result<FrequentAlarmTimeVO> getFrequentAlarmTime(
            @Parameter(description = "设备ID") @RequestParam Long deviceId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getFrequentAlarmTime(deviceId, startTime, endTime));
    }

    @GetMapping("/stats/level_stats")
    @Operation(operationId = "10", summary = "获取设备报警等级统计")
    public Result<DeviceAlarmLevelStatsVO> getAlarmLevelStats(
            @Parameter(description = "设备ID") @RequestParam Long deviceId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getAlarmLevelStats(deviceId, startTime, endTime));
    }

    @GetMapping("/stats/temperature_trend")
    @Operation(operationId = "11", summary = "获取温度趋势统计")
    public Result<TemperatureTrendVO> getTemperatureTrend(
            @Parameter(description = "设备ID") @RequestParam Long deviceId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getTemperatureTrend(deviceId, startTime, endTime));
    }

    @GetMapping("/stats/daily_trend")
    @Operation(operationId = "12", summary = "获取每日报警趋势统计")
    public Result<DailyAlarmTrendVO> getDailyAlarmTrend(
            @Parameter(description = "设备ID") @RequestParam Long deviceId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getDailyAlarmTrend(deviceId, startTime, endTime));
    }

    @GetMapping("/stats/part_temperature_trend")
    @Operation(operationId = "13", summary = "获取部件温度趋势统计")
    public Result<TemperatureTrendVO> getPartTemperatureTrend(
            @Parameter(description = "部件ID") @RequestParam Long partId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getPartTemperatureTrend(partId, startTime, endTime));
    }

    @GetMapping("/stats/part_alarm_temperature_trend")
    @Operation(operationId = "14", summary = "获取部件报警温度趋势统计")
    public Result<TemperatureTrendVO> getPartAlarmTemperatureTrend(
            @Parameter(description = "部件ID") @RequestParam Long partId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getPartAlarmTemperatureTrend(partId, startTime, endTime));
    }

    @GetMapping("/stats/part_hourly_alarm_distribution")
    @Operation(operationId = "15", summary = "获取部件小时报警分布统计")
    public Result<FrequentAlarmTimeVO> getPartHourlyAlarmDistribution(
            @Parameter(description = "部件ID") @RequestParam Long partId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getPartHourlyAlarmDistribution(partId, startTime, endTime));
    }

    @GetMapping("/stats/part_alarm_level_distribution")
    @Operation(operationId = "16", summary = "获取部件报警等级分布统计")
    public Result<DeviceAlarmLevelStatsVO> getPartAlarmLevelDistribution(
            @Parameter(description = "部件ID") @RequestParam Long partId,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        return Result.data(deviceAlarmFacade.getPartAlarmLevelDistribution(partId, startTime, endTime));
    }
}
