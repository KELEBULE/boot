package com.izpan.modules.ai.tools.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiToolResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String toolName;

    private boolean success;

    private Object data;

    private String errorMessage;

    private long executionTime;

    public static AiToolResult success(String toolName, Object data, long executionTime) {
        return AiToolResult.builder()
                .toolName(toolName)
                .success(true)
                .data(data != null ? data : "操作成功，但无返回数据")
                .executionTime(executionTime)
                .build();
    }

    public static AiToolResult failure(String toolName, String errorMessage) {
        return AiToolResult.builder()
                .toolName(toolName)
                .success(false)
                .errorMessage(errorMessage != null ? errorMessage : "操作失败，原因未知")
                .build();
    }
}
