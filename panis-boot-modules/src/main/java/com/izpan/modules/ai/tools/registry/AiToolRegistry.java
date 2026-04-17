package com.izpan.modules.ai.tools.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolDefinition;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AiToolRegistry {

    private final List<AiToolDefinition> tools = new ArrayList<>();
    private final Map<String, AiToolDefinition> toolMap = new HashMap<>();

    @PostConstruct
    public void init() {
        registerAlarmTools();
        registerDeviceTools();
        registerWorkOrderTools();
        registerMonitorTools();
        log.info("AI工具注册完成，共注册 {} 个工具", tools.size());
    }

    private void registerAlarmTools() {
        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_today_alarms")
                        .description("查询今天的设备报警记录。当用户询问'今天的报警'、'今日报警'、'今天有哪些报警'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "alarmLevel", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("报警等级筛选：1-一级(严重)，2-二级(警告)，3-三级(提示)。不传则查询所有等级。")
                                                .build(),
                                        "confirmStatus", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("确认状态筛选：0-未确认，1-已确认。不传则查询所有状态。")
                                                .build(),
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_alarms_by_time_range")
                        .description("查询指定时间范围内的设备报警记录。当用户询问'某段时间的报警'、'最近几天的报警'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "startTime", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("开始时间，格式：YYYY-MM-DD HH:mm:ss")
                                                .build(),
                                        "endTime", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("结束时间，格式：YYYY-MM-DD HH:mm:ss")
                                                .build(),
                                        "alarmLevel", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("报警等级筛选：1-一级(严重)，2-二级(警告)，3-三级(提示)")
                                                .build()
                                ))
                                .required(List.of("startTime", "endTime"))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_unconfirmed_alarms")
                        .description("查询未确认的报警记录。当用户询问'未确认的报警'、'待处理的报警'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_alarm_level_distribution")
                        .description("获取报警等级分布统计。当用户询问'报警等级分布'、'各级别报警数量'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of())
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_device_alarm_top")
                        .description("获取报警次数最多的设备排行。当用户询问'报警最多的设备'、'设备报警排名'、'哪个设备报警最多'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回数量限制，默认10")
                                                .defaultValue(10)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_alarm_statistics")
                        .description("获取报警统计数据概览。当用户询问'报警统计'、'报警概况'、'报警情况汇总'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of())
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_daily_alarm_trend")
                        .description("获取每日报警趋势。当用户询问'报警趋势'、'每天报警数量变化'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "deviceId", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("设备ID，不传则查询所有设备")
                                                .build(),
                                        "days", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("查询最近多少天的数据，默认7天")
                                                .defaultValue(7)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_alarm_detail")
                        .description("根据报警编号查询报警详情。当用户询问'查询报警ALMxxx'、'报警编号xxx的详情'、'查询某个报警信息'时使用此工具。报警编号格式通常为ALM+年月日+时分秒+序号，如ALM20260301175604000088。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "alarmCode", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("报警编号，如ALM20260301175604000088。用户提供的报警号通常就是这个编号。")
                                                .build()
                                ))
                                .required(List.of("alarmCode"))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_device_alarms")
                        .description("查询指定设备的报警记录。当用户询问'某设备的报警'、'设备xxx有哪些报警'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "deviceId", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("设备ID")
                                                .build(),
                                        "startTime", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("开始时间，格式：YYYY-MM-DD")
                                                .build(),
                                        "endTime", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("结束时间，格式：YYYY-MM-DD")
                                                .build()
                                ))
                                .required(List.of("deviceId"))
                                .build())
                        .build())
                .build());
    }

    private void registerDeviceTools() {
        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_devices")
                        .description("查询设备列表。当用户询问'设备列表'、'有哪些设备'、'设备信息'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "deviceStatus", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("设备状态：1-运行中，2-待机，3-故障，4-维护中")
                                                .build(),
                                        "deviceName", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("设备名称（模糊查询）")
                                                .build(),
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_device_status_distribution")
                        .description("获取设备状态分布统计。当用户询问'设备状态分布'、'各状态设备数量'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of())
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_device_detail")
                        .description("获取设备详细信息。当用户询问'设备详情'、'某个设备的信息'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "deviceId", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("设备ID")
                                                .build()
                                ))
                                .required(List.of("deviceId"))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_fault_devices")
                        .description("查询故障设备列表。当用户询问'故障设备'、'哪些设备出问题了'、'异常设备'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());
    }

    private void registerWorkOrderTools() {
        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_work_orders")
                        .description("查询工单列表。当用户询问'工单列表'、'有哪些工单'、'工单信息'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "orderStatus", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("工单状态：0-待处理，1-处理中，2-已完成，3-已关闭")
                                                .build(),
                                        "priority", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("优先级：1-低，2-中，3-高，4-紧急")
                                                .build(),
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_pending_work_orders")
                        .description("查询待处理的工单。当用户询问'待处理工单'、'我的待办工单'、'未完成的工单'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_work_order_statistics")
                        .description("获取工单统计数据。当用户询问'工单统计'、'工单概况'、'我的工单情况'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "timeRange", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("时间范围：week-近一周，month-近一月，quarter-近一季度")
                                                .defaultValue("week")
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("get_work_order_detail")
                        .description("获取工单详细信息。当用户询问'工单详情'、'某个工单的信息'、'查询工单WOxxx'时使用此工具。可以通过工单编号(如WO202603032217076736)或工单ID查询。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "orderCode", AiToolDefinition.PropertyDefinition.builder()
                                                .type("string")
                                                .description("工单编号，如WO202603032217076736。用户提供的工单号通常就是这个编号。")
                                                .build(),
                                        "orderId", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("工单ID（数字），如果用户明确说了工单ID则使用此参数")
                                                .build()
                                ))
                                .build())
                        .build())
                .build());
    }

    private void registerMonitorTools() {
        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_login_logs")
                        .description("查询登录日志。当用户询问'登录日志'、'谁登录过系统'、'登录记录'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());

        registerTool(AiToolDefinition.builder()
                .type("function")
                .function(AiToolDefinition.FunctionDefinition.builder()
                        .name("query_operation_logs")
                        .description("查询操作日志。当用户询问'操作日志'、'谁做了什么操作'、'操作记录'时使用此工具。")
                        .parameters(AiToolDefinition.ParametersDefinition.builder()
                                .type("object")
                                .properties(Map.of(
                                        "limit", AiToolDefinition.PropertyDefinition.builder()
                                                .type("integer")
                                                .description("返回记录数量限制，默认20条")
                                                .defaultValue(20)
                                                .build()
                                ))
                                .build())
                        .build())
                .build());
    }

    public void registerTool(AiToolDefinition tool) {
        tools.add(tool);
        toolMap.put(tool.getFunction().getName(), tool);
    }

    public List<AiToolDefinition> getAllTools() {
        return new ArrayList<>(tools);
    }

    public AiToolDefinition getTool(String name) {
        return toolMap.get(name);
    }

    public boolean hasTool(String name) {
        return toolMap.containsKey(name);
    }
}
