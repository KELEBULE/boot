package com.izpan.modules.ai.tools.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.izpan.modules.ai.tools.domain.AiToolResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiToolExecutorDispatcher {

    private final List<IAiToolExecutor> executors;
    private final Map<String, IAiToolExecutor> executorMap = new HashMap<>();

    public void init() {
        for (IAiToolExecutor executor : executors) {
            executorMap.put(executor.getToolName(), executor);
            log.info("注册工具执行器: {}", executor.getToolName());
        }
    }

    public AiToolResult execute(String toolName, Map<String, Object> arguments) {
        IAiToolExecutor executor = executorMap.get(toolName);
        if (executor == null) {
            log.warn("未找到工具执行器: {}", toolName);
            return AiToolResult.failure(toolName, "未找到工具执行器: " + toolName);
        }

        log.info("执行工具: {}, 参数: {}", toolName, arguments);
        return executor.execute(arguments);
    }

    public boolean hasExecutor(String toolName) {
        return executorMap.containsKey(toolName);
    }

    public Map<String, IAiToolExecutor> getAllExecutors() {
        return new HashMap<>(executorMap);
    }
}
