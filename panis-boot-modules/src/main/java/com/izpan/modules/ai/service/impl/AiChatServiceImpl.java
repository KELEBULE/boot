/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.izpan.modules.ai.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.modules.ai.client.OllamaClient;
import com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO;
import com.izpan.modules.ai.domain.entity.AiChatHistory;
import com.izpan.modules.ai.domain.entity.AiChatSession;
import com.izpan.modules.ai.repository.mapper.AiChatHistoryMapper;
import com.izpan.modules.ai.repository.mapper.AiChatSessionMapper;
import com.izpan.modules.ai.service.IAiChatService;
import com.izpan.modules.ai.service.IAiConfigService;
import com.izpan.modules.ai.tools.domain.AiToolDefinition;
import com.izpan.modules.ai.tools.domain.AiToolResult;
import com.izpan.modules.ai.tools.executor.AiToolExecutorDispatcher;
import com.izpan.modules.ai.tools.registry.AiToolRegistry;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiChatServiceImpl extends ServiceImpl<AiChatHistoryMapper, AiChatHistory> implements IAiChatService {

    private static final Set<String> TOOL_SUPPORTED_MODELS = Set.of(
            "llama3.1", "llama3.1:8b", "llama3.1:70b",
            "llama3.2", "llama3.2:1b", "llama3.2:3b",
            "llama3.3", "llama3.3:70b",
            "mistral", "mistral:7b",
            "qwen2.5", "qwen2.5:7b", "qwen2.5:14b",
            "qwen3", "qwen3:8b", "qwen3:14b",
            "deepseek-r1", "deepseek-r1:7b", "deepseek-r1:8b",
            "llava", "llava:7b",
            "phi3", "phi3:mini"
    );

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private AiChatSessionMapper sessionMapper;

    @Autowired
    private AiChatHistoryMapper historyMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AiToolRegistry toolRegistry;

    @Autowired
    private AiToolExecutorDispatcher executorDispatcher;

    @Autowired
    private IAiConfigService aiConfigService;

    private final Map<String, List<Map<String, String>>> sessionHistoryCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        executorDispatcher.init();
    }

    private boolean isToolSupported(String model) {
        if (model == null) {
            return false;
        }
        String lowerModel = model.toLowerCase();
        return TOOL_SUPPORTED_MODELS.stream()
                .anyMatch(supported -> lowerModel.contains(supported.toLowerCase()) || supported.toLowerCase().contains(lowerModel));
    }

    @Override
    public SseEmitter streamChat(AiChatRequestDTO request) {
        SseEmitter emitter = new SseEmitter(3600000L);

        final String sessionId = request.getSessionId();
        final String actualSessionId = (sessionId == null || sessionId.isEmpty()) ? generateSessionId() : sessionId;

        final Long userId = GlobalUserHolder.getUserId();
        final String model = request.getModel();
        final String userMessage = request.getMessage();

        updateSessionLastActiveTime(actualSessionId, userId, model, userMessage);
        saveChatHistory(actualSessionId, "user", request.getMessage(), null, null);

        boolean useTools = isToolSupported(model);

        CompletableFuture.runAsync(() -> {
            try {
                if (useTools) {
                    streamChatWithTools(request, emitter, actualSessionId, userMessage, model);
                } else {
                    log.info("模型 {} 不支持工具调用，使用普通流式对话模式", model);
                    streamChatWithoutTools(request, emitter, actualSessionId, userMessage, model);
                }

                updateSessionLastActiveTime(actualSessionId, userId, model, userMessage);

            } catch (Exception e) {
                log.error("流式聊天失败", e);
                emitter.completeWithError(e);
            }
        });

        emitter.onCompletion(() -> log.info("SSE连接完成, sessionId: {}", actualSessionId));
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时, sessionId: {}", actualSessionId);
            emitter.complete();
        });
        emitter.onError((ex) -> {
            log.error("SSE连接错误, sessionId: {}", actualSessionId, ex);
            emitter.completeWithError(ex);
        });

        return emitter;
    }

    private void streamChatWithTools(AiChatRequestDTO request, SseEmitter emitter,
            String actualSessionId, String userMessage, String model) throws Exception {
        List<Map<String, Object>> messages = buildMessagesWithHistory(actualSessionId, userMessage);
        List<Map<String, Object>> tools = buildToolsDefinition();

        Map<String, Object> response = ollamaClient.chat(
                model, messages, tools, request.getTemperature(), request.getMaxTokens(), false
        );

        List<Map<String, Object>> toolCalls = ollamaClient.extractToolCalls(response);

        if (!toolCalls.isEmpty()) {
            log.info("流式聊天检测到工具调用，数量: {}", toolCalls.size());

            List<Map<String, Object>> toolResults = new ArrayList<>();
            for (Map<String, Object> toolCall : toolCalls) {
                Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                String toolName = (String) function.get("name");
                Map<String, Object> arguments = (Map<String, Object>) function.get("arguments");

                AiToolResult result = executorDispatcher.execute(toolName, arguments);

                Map<String, Object> toolResultMap = new HashMap<>();
                toolResultMap.put("toolName", toolName);
                toolResultMap.put("success", result.isSuccess());
                toolResultMap.put("data", result.getData() != null ? result.getData() : result.getErrorMessage());
                toolResults.add(toolResultMap);

                Map<String, Object> toolEvent = new HashMap<>();
                toolEvent.put("type", "tool_call");
                toolEvent.put("toolName", toolName);
                toolEvent.put("success", result.isSuccess());
                toolEvent.put("data", result.getData() != null ? result.getData() : result.getErrorMessage());
                emitter.send(SseEmitter.event()
                        .data(toolEvent)
                        .id(UUID.randomUUID().toString()));
            }

            messages.add(Map.of("role", "assistant", "content", ""));
            for (int i = 0; i < toolCalls.size(); i++) {
                try {
                    messages.add(Map.of(
                            "role", "tool",
                            "content", objectMapper.writeValueAsString(toolResults.get(i))
                    ));
                } catch (JsonProcessingException e) {
                    log.error("序列化工具结果失败", e);
                    messages.add(Map.of("role", "tool", "content", "{}"));
                }
            }

            Map<String, Object> streamRequest = new HashMap<>();
            streamRequest.put("model", model);
            streamRequest.put("messages", messages);
            streamRequest.put("stream", true);
            streamRequest.put("options", Map.of(
                    "temperature", request.getTemperature(),
                    "num_predict", request.getMaxTokens() * 2
            ));

            StringBuilder fullResponse = new StringBuilder();
            long startTime = System.currentTimeMillis();

            ollamaClient.streamChat(streamRequest, emitter, fullResponse, actualSessionId);

            Long processingTime = System.currentTimeMillis() - startTime;
            saveChatHistory(actualSessionId, "assistant", fullResponse.toString(), null, processingTime);
        } else {
            Map<String, Object> streamRequest = new HashMap<>();
            streamRequest.put("model", model);
            streamRequest.put("messages", messages);
            streamRequest.put("stream", true);
            streamRequest.put("options", Map.of(
                    "temperature", request.getTemperature(),
                    "num_predict", request.getMaxTokens()
            ));

            StringBuilder fullResponse = new StringBuilder();
            long startTime = System.currentTimeMillis();

            ollamaClient.streamChat(streamRequest, emitter, fullResponse, actualSessionId);

            Long processingTime = System.currentTimeMillis() - startTime;
            saveChatHistory(actualSessionId, "assistant", fullResponse.toString(), null, processingTime);
        }
    }

    private void streamChatWithoutTools(AiChatRequestDTO request, SseEmitter emitter,
            String actualSessionId, String userMessage, String model) throws Exception {
        String prompt = buildPromptWithToolContext(userMessage);

        Map<String, Object> streamRequest = new HashMap<>();
        streamRequest.put("model", model);
        streamRequest.put("prompt", prompt);
        streamRequest.put("stream", true);
        streamRequest.put("options", Map.of(
                "temperature", request.getTemperature(),
                "num_predict", request.getMaxTokens()
        ));

        StringBuilder fullResponse = new StringBuilder();
        long startTime = System.currentTimeMillis();

        ollamaClient.streamGenerate(streamRequest, emitter, fullResponse, actualSessionId);

        Long processingTime = System.currentTimeMillis() - startTime;
        saveChatHistory(actualSessionId, "assistant", fullResponse.toString(), null, processingTime);
    }

    @Override
    public List<String> getAvailableModels() {
        return ollamaClient.listModels();
    }

    @Override
    public void saveChatHistory(String sessionId, String role, String content, Integer tokensUsed, Long processingTime) {
        AiChatHistory history = AiChatHistory.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .tokensUsed(tokensUsed)
                .processingTime(processingTime)
                .build();

        historyMapper.insert(history);

        List<Map<String, String>> historyList = sessionHistoryCache.computeIfAbsent(
                sessionId,
                k -> new ArrayList<>()
        );

        historyList.add(Map.of("role", role, "content", content));

        if (historyList.size() > 20) {
            historyList = historyList.subList(historyList.size() - 20, historyList.size());
            sessionHistoryCache.put(sessionId, historyList);
        }
    }

    @Override
    public void updateSessionLastActiveTime(String sessionId, Long userId, String model) {
        updateSessionLastActiveTime(sessionId, userId, model, null);
    }

    @Override
    public void updateSessionLastActiveTime(String sessionId, Long userId, String model, String title) {
        LambdaQueryWrapper<AiChatSession> queryWrapper = new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getSessionId, sessionId);

        AiChatSession session = sessionMapper.selectOne(queryWrapper);
        if (session == null) {
            String sessionTitle = title != null ? truncateTitle(title) : "新对话";
            session = AiChatSession.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .model(model)
                    .title(sessionTitle)
                    .lastActiveTime(LocalDateTime.now())
                    .build();
            sessionMapper.insert(session);
        } else {
            session.setLastActiveTime(LocalDateTime.now());
            if (title != null && "新对话".equals(session.getTitle())) {
                session.setTitle(truncateTitle(title));
            }
            sessionMapper.updateById(session);
        }
    }

    private String truncateTitle(String title) {
        if (title == null) {
            return "新对话";
        }
        return title.length() > 50 ? title.substring(0, 50) + "..." : title;
    }

    @Override
    public void clearSessionHistory(String sessionId) {
        LambdaQueryWrapper<AiChatHistory> historyQueryWrapper = new LambdaQueryWrapper<AiChatHistory>()
                .eq(AiChatHistory::getSessionId, sessionId);
        historyMapper.delete(historyQueryWrapper);

        LambdaQueryWrapper<AiChatSession> sessionQueryWrapper = new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getSessionId, sessionId);
        sessionMapper.delete(sessionQueryWrapper);

        sessionHistoryCache.remove(sessionId);
    }

    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private List<Map<String, String>> getSessionHistory(String sessionId) {
        return sessionHistoryCache.getOrDefault(sessionId, new ArrayList<>());
    }

    private List<Map<String, Object>> buildMessagesWithHistory(String sessionId, String userMessage) {
        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", buildSystemPrompt()
        ));

        List<Map<String, String>> history = getSessionHistory(sessionId);
        for (Map<String, String> msg : history) {
            messages.add(Map.of(
                    "role", msg.get("role"),
                    "content", msg.get("content")
            ));
        }

        messages.add(Map.of("role", "user", "content", userMessage));
        return messages;
    }

    private String buildSystemPrompt() {
        String configPrompt = aiConfigService.getSystemPrompt();
        if (configPrompt != null && !configPrompt.isEmpty()) {
            return configPrompt;
        }
        return """
                你是一个专业的设备管理AI助手，帮助用户查询和分析设备报警数据。
                
                你的职责：
                1. 理解用户的问题，判断是否需要查询数据库
                2. 当需要查询数据时，调用相应的工具函数获取数据
                3. 基于查询结果，用自然语言向用户解释和分析数据
                
                回答要求：
                1. 使用中文回答
                2. 对查询到的数据进行简要分析
                3. 如果数据为空，告知用户没有找到相关数据
                4. 对于报警数据，分析其严重程度和可能的原因
                5. 提供专业的建议和解决方案
                """;
    }

    private String buildPromptWithToolContext(String userMessage) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的设备管理AI助手。\n\n");
        prompt.append("重要提示：你当前使用的模型不支持实时数据查询功能。\n");
        prompt.append("如果用户询问需要查询数据库的问题（如报警、设备、工单等），请礼貌地告知用户：\n");
        prompt.append("\"抱歉，当前模型不支持实时数据查询功能。如需查询设备、报警、工单等数据，请使用支持工具调用的模型（如 llama3.1、mistral、qwen2.5 等）。\"\n\n");
        prompt.append("用户问题：").append(userMessage).append("\n\n");
        prompt.append("请根据以上提示回答用户的问题。");
        return prompt.toString();
    }

    private List<Map<String, Object>> buildToolsDefinition() {
        List<Map<String, Object>> tools = new ArrayList<>();

        for (AiToolDefinition toolDef : toolRegistry.getAllTools()) {
            Map<String, Object> tool = new HashMap<>();
            tool.put("type", toolDef.getType());

            Map<String, Object> function = new HashMap<>();
            function.put("name", toolDef.getFunction().getName());
            function.put("description", toolDef.getFunction().getDescription());

            if (toolDef.getFunction().getParameters() != null) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("type", toolDef.getFunction().getParameters().getType());

                Map<String, Object> properties = new HashMap<>();
                if (toolDef.getFunction().getParameters().getProperties() != null) {
                    toolDef.getFunction().getParameters().getProperties().forEach((key, prop) -> {
                        Map<String, Object> propMap = new HashMap<>();
                        propMap.put("type", prop.getType());
                        propMap.put("description", prop.getDescription());
                        if (prop.getEnumValues() != null && !prop.getEnumValues().isEmpty()) {
                            propMap.put("enum", prop.getEnumValues());
                        }
                        properties.put(key, propMap);
                    });
                }
                parameters.put("properties", properties);

                if (toolDef.getFunction().getParameters().getRequired() != null) {
                    parameters.put("required", toolDef.getFunction().getParameters().getRequired());
                }

                function.put("parameters", parameters);
            }

            tool.put("function", function);
            tools.add(tool);
        }

        return tools;
    }
}
