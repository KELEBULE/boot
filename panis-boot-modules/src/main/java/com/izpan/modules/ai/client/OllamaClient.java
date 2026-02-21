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
package com.izpan.modules.ai.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Ollama客户端工具类
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.client.OllamaClient
 * @CreateTime 2024-12-20
 */
@Slf4j
@Component
public class OllamaClient {

    @Value("${ai.ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> generate(String model, String prompt, Double temperature, Integer maxTokens, Boolean stream) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", stream);
            requestBody.put("options", Map.of(
                    "temperature", temperature,
                    "num_predict", maxTokens
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ollamaUrl + "/api/generate",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
                });
            } else {
                log.error("Ollama API调用失败: {}", response.getStatusCode());
                return Map.of("error", "API调用失败");
            }
        } catch (Exception e) {
            log.error("调用Ollama API异常", e);
            return Map.of("error", e.getMessage());
        }
    }

    public List<String> listModels() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    ollamaUrl + "/api/tags",
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode models = root.get("models");
                List<String> modelNames = new ArrayList<>();
                if (models != null && models.isArray()) {
                    for (JsonNode model : models) {
                        modelNames.add(model.get("name").asText());
                    }
                }
                return modelNames;
            }
        } catch (Exception e) {
            log.error("获取模型列表失败", e);
        }
        return Arrays.asList("llama2", "mistral", "qwen:7b");
    }

    public Map<String, Object> healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    ollamaUrl + "/api/tags",
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return Map.of(
                        "status", "UP",
                        "endpoint", ollamaUrl
                );
            } else {
                return Map.of(
                        "status", "DOWN",
                        "error", "HTTP " + response.getStatusCode()
                );
            }
        } catch (Exception e) {
            return Map.of(
                    "status", "DOWN",
                    "error", e.getMessage()
            );
        }
    }

    public void streamGenerate(Map<String, Object> requestBody, SseEmitter emitter, StringBuilder fullResponse, String sessionId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.execute(
                    ollamaUrl + "/api/generate",
                    HttpMethod.POST,
                    request -> {
                        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        // 写入请求体
                        String jsonBody = objectMapper.writeValueAsString(requestBody);
                        request.getBody().write(jsonBody.getBytes());
                    },
                    (ResponseExtractor<Void>) response -> {
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getBody()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.trim().isEmpty()) {
                                    continue;
                                }

                                try {
                                    JsonNode chunk = objectMapper.readTree(line);
                                    String content = chunk.has("response") ? chunk.get("response").asText() : null;
                                    boolean done = chunk.has("done") && chunk.get("done").asBoolean();

                                    if (content != null && !content.isEmpty()) {
                                        fullResponse.append(content);
                                        Map<String, Object> eventData = new HashMap<>();
                                        eventData.put("content", content);
                                        eventData.put("done", done);
                                        emitter.send(SseEmitter.event()
                                                .data(eventData)
                                                .id(UUID.randomUUID().toString()));
                                    }

                                    if (done) {
                                        Map<String, Object> finalEventData = new HashMap<>();
                                        finalEventData.put("done", true);
                                        finalEventData.put("sessionId", sessionId);
                                        emitter.send(SseEmitter.event()
                                                .data(finalEventData)
                                                .id(UUID.randomUUID().toString()));
                                        emitter.complete();
                                        break;
                                    }
                                } catch (Exception e) {
                                    log.warn("解析流式数据失败: {}", line, e);
                                }
                            }
                        } catch (Exception e) {
                            log.error("读取流式响应异常", e);
                            emitter.completeWithError(e);
                        }
                        return null; // 明确返回Void类型
                    }
            );
        } catch (Exception e) {
            log.error("流式生成失败", e);
            emitter.completeWithError(e);
        }
    }

    public String buildPrompt(String message, List<Map<String, String>> history) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("<<SYS>>\n");
        prompt.append("你是一个有帮助的、专业的AI助手。请用中文回答用户的问题。\n");
        prompt.append("<</SYS>>\n\n");

        if (history != null && !history.isEmpty()) {
            for (Map<String, String> msg : history) {
                String role = msg.get("role");
                String content = msg.get("content");
                if ("user".equals(role)) {
                    prompt.append("[INST] ").append(content).append(" [/INST]\n");
                } else if ("assistant".equals(role)) {
                    prompt.append(content).append("\n");
                }
            }
        }

        prompt.append("[INST] ").append(message).append(" [/INST]");

        return prompt.toString();
    }
}
