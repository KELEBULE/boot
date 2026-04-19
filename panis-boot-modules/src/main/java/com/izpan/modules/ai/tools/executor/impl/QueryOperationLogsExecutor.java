package com.izpan.modules.ai.tools.executor.impl;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.IAiToolExecutor;
import com.izpan.modules.ai.tools.util.AiToolPermissionChecker;
import com.izpan.modules.monitor.domain.entity.MonLogsOperation;
import com.izpan.modules.monitor.repository.mapper.MonLogsOperationMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryOperationLogsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "mon:logs:operation:page";

    private final MonLogsOperationMapper operationLogMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getToolName() {
        return "query_operation_logs";
    }

    @Override
    public AiToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        
        if (!AiToolPermissionChecker.hasPermission(REQUIRED_PERMISSION)) {
            return AiToolResult.failure(getToolName(), AiToolPermissionChecker.getPermissionDeniedMessage(REQUIRED_PERMISSION));
        }
        
        try {
            int limit = arguments.containsKey("limit") 
                    ? ((Number) arguments.get("limit")).intValue() : 20;

            LambdaQueryWrapper<MonLogsOperation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(MonLogsOperation::getCreateTime);

            Page<MonLogsOperation> page = operationLogMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatOperationLogList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            return AiToolResult.failure(getToolName(), "查询操作日志失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatOperationLogList(List<MonLogsOperation> logs) {
        return logs.stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("requestId", log.getRequestId());
            map.put("ip", log.getIp());
            map.put("ipAddr", log.getIpAddr());
            map.put("requestUri", log.getRequestUri());
            map.put("requestMethod", log.getRequestMethod());
            map.put("operation", log.getOperation());
            map.put("methodName", log.getMethodName());
            map.put("methodParams", log.getMethodParams());
            map.put("useTime", log.getUseTime());
            map.put("createTime", log.getCreateTime() != null ? log.getCreateTime().format(FORMATTER) : null);
            return map;
        }).toList();
    }
}
