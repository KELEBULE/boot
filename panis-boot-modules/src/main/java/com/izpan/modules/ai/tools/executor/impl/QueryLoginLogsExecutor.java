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
import com.izpan.modules.monitor.domain.entity.MonLogsLogin;
import com.izpan.modules.monitor.repository.mapper.MonLogsLoginMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryLoginLogsExecutor implements IAiToolExecutor {

    private static final String REQUIRED_PERMISSION = "mon:logs:login:page";

    private final MonLogsLoginMapper loginLogMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getToolName() {
        return "query_login_logs";
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

            LambdaQueryWrapper<MonLogsLogin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(MonLogsLogin::getCreateTime);

            Page<MonLogsLogin> page = loginLogMapper.selectPage(
                    new Page<>(1, limit), queryWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotal());
            result.put("list", formatLoginLogList(page.getRecords()));

            return AiToolResult.success(getToolName(), result, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("查询登录日志失败", e);
            return AiToolResult.failure(getToolName(), "查询登录日志失败: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> formatLoginLogList(List<MonLogsLogin> logs) {
        return logs.stream().map(log -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", log.getId());
            map.put("userName", log.getUserName());
            map.put("userRealName", log.getUserRealName());
            map.put("ip", log.getIp());
            map.put("ipAddr", log.getIpAddr());
            map.put("userAgent", log.getUserAgent());
            map.put("status", log.getStatus());
            map.put("statusName", "1".equals(log.getStatus()) ? "成功" : "失败");
            map.put("message", log.getMessage());
            map.put("createTime", log.getCreateTime() != null ? log.getCreateTime().format(FORMATTER) : null);
            return map;
        }).toList();
    }
}
