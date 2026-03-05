package com.izpan.modules.ai.tools.executor;

import com.izpan.modules.ai.tools.domain.AiToolResult;

import java.util.Map;

public interface IAiToolExecutor {

    String getToolName();

    AiToolResult execute(Map<String, Object> arguments);
}
