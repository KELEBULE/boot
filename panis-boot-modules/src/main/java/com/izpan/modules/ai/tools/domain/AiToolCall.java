package com.izpan.modules.ai.tools.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiToolCall implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ToolCallFunction function;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCallFunction implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private Map<String, Object> arguments;
    }
}
