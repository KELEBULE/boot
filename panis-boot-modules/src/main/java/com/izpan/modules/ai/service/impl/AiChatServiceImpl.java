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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.modules.ai.client.OllamaClient;
import com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO;
import com.izpan.modules.ai.domain.dto.chat.AiChatResponseDTO;
import com.izpan.modules.ai.domain.entity.AiChatHistory;
import com.izpan.modules.ai.domain.entity.AiChatSession;
import com.izpan.modules.ai.repository.mapper.AiChatHistoryMapper;
import com.izpan.modules.ai.repository.mapper.AiChatSessionMapper;
import com.izpan.modules.ai.service.IAiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI聊天 Service 服务接口实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.service.impl.AiChatServiceImpl
 * @CreateTime 2024-12-20
 */
@Slf4j
@Service
public class AiChatServiceImpl extends ServiceImpl<AiChatHistoryMapper, AiChatHistory> implements IAiChatService {

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private AiChatSessionMapper sessionMapper;

    @Autowired
    private AiChatHistoryMapper historyMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, List<Map<String, String>>> sessionHistoryCache = new ConcurrentHashMap<>();

    @Override
    public AiChatResponseDTO chatCompletion(AiChatRequestDTO request) {
        long startTime = System.currentTimeMillis();

        final String sessionId = request.getSessionId();
        final String actualSessionId = (sessionId == null || sessionId.isEmpty()) ? generateSessionId() : sessionId;

        List<Map<String, String>> history = getSessionHistory(actualSessionId);
        String prompt = ollamaClient.buildPrompt(request.getMessage(), history);

        Map<String, Object> response = ollamaClient.generate(
                request.getModel(),
                prompt,
                request.getTemperature(),
                request.getMaxTokens(),
                false
        );

        String reply = (String) response.get("response");
        Long processingTime = System.currentTimeMillis() - startTime;

        saveChatHistory(actualSessionId, "user", request.getMessage(), null, null);
        saveChatHistory(actualSessionId, "assistant", reply, null, processingTime);
        updateSessionLastActiveTime(actualSessionId, GlobalUserHolder.getUserId(), request.getModel());

        return AiChatResponseDTO.builder()
                .reply(reply)
                .model(request.getModel())
                .processingTime(processingTime)
                .timestamp(LocalDateTime.now())
                .sessionId(actualSessionId)
                .build();
    }

    @Override
    public SseEmitter streamChat(AiChatRequestDTO request) {
        SseEmitter emitter = new SseEmitter(3600000L);

        final String sessionId = request.getSessionId();
        final String actualSessionId = (sessionId == null || sessionId.isEmpty()) ? generateSessionId() : sessionId;

        List<Map<String, String>> history = getSessionHistory(actualSessionId);
        String prompt = ollamaClient.buildPrompt(request.getMessage(), history);

        saveChatHistory(actualSessionId, "user", request.getMessage(), null, null);

        final Long userId = GlobalUserHolder.getUserId();
        final String model = request.getModel();

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> streamRequest = new HashMap<>();
                streamRequest.put("model", request.getModel());
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
                updateSessionLastActiveTime(actualSessionId, userId, model);

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
        LambdaQueryWrapper<AiChatSession> queryWrapper = new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getSessionId, sessionId);

        AiChatSession session = sessionMapper.selectOne(queryWrapper);
        if (session == null) {
            session = AiChatSession.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .model(model)
                    .title("新对话")
                    .lastActiveTime(LocalDateTime.now())
                    .build();
            sessionMapper.insert(session);
        } else {
            session.setLastActiveTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }

    @Override
    public void clearSessionHistory(String sessionId) {
        LambdaQueryWrapper<AiChatHistory> queryWrapper = new LambdaQueryWrapper<AiChatHistory>()
                .eq(AiChatHistory::getSessionId, sessionId);
        historyMapper.delete(queryWrapper);
        sessionHistoryCache.remove(sessionId);
    }

    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private List<Map<String, String>> getSessionHistory(String sessionId) {
        return sessionHistoryCache.getOrDefault(sessionId, new ArrayList<>());
    }
}
