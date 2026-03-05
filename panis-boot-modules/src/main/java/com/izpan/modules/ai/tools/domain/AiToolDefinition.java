package com.izpan.modules.ai.tools.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiToolDefinition implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String type;

    private FunctionDefinition function;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FunctionDefinition implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private String description;
        private ParametersDefinition parameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParametersDefinition implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String type;
        private Map<String, PropertyDefinition> properties;
        private List<String> required;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyDefinition implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String type;
        private String description;
        private List<String> enumValues;
        private Object defaultValue;
    }
}
