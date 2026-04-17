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
package com.izpan.admin.controller.ai;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.izpan.common.api.Result;
import com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileDeleteDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileUploadDTO;
import com.izpan.modules.ai.domain.vo.AiChatVO;
import com.izpan.modules.ai.domain.vo.AiSessionVO;
import com.izpan.modules.ai.facade.IAiChatFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * AI聊天 Controller 控制层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.admin.controller.ai.AiChatController
 * @CreateTime 2024-12-20
 */
@RestController
@Tag(name = "AI聊天")
@RequiredArgsConstructor
@RequestMapping("ai_chat")
public class AiChatController {

    @NonNull
    private IAiChatFacade aiChatFacade;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(operationId = "1", summary = "AI聊天（流式响应）")
    public SseEmitter streamChat(
            @Parameter(description = "消息内容") @RequestParam String message,
            @Parameter(description = "模型名称") @RequestParam(defaultValue = "qwen3") String model,
            @Parameter(description = "温度参数") @RequestParam(defaultValue = "0.7") Double temperature,
            @Parameter(description = "最大token数") @RequestParam(defaultValue = "1000") Integer maxTokens,
            @Parameter(description = "会话ID") @RequestParam(required = false) String sessionId) {

        AiChatRequestDTO request = AiChatRequestDTO.builder()
                .message(message)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .sessionId(sessionId)
                .stream(true)
                .build();

        return aiChatFacade.streamChat(request);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(operationId = "2", summary = "获取会话历史")
    public Result<List<AiChatVO>> getSessionHistory(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        return Result.data(aiChatFacade.getSessionHistory(sessionId));
    }

    @GetMapping("/sessions")
    @Operation(operationId = "3", summary = "获取会话列表")
    public Result<List<AiSessionVO>> getSessionList(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        return Result.data(aiChatFacade.getSessionList(userId));
    }

    @DeleteMapping("/session/{sessionId}")
    @Operation(operationId = "4", summary = "清除会话历史")
    public Result<Boolean> clearSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        return Result.status(aiChatFacade.clearSession(sessionId));
    }

    @GetMapping("/models")
    @Operation(operationId = "5", summary = "获取可用模型列表")
    public Result<List<String>> getAvailableModels() {
        return Result.data(aiChatFacade.getAvailableModels());
    }

    @PostMapping("/file/upload")
    @Operation(operationId = "6", summary = "上传文件")
    public Result<List<String>> uploadFiles(
            @Parameter(description = "文件上传对象", required = true) @Valid @RequestBody AiFileUploadDTO uploadDTO) {
        return Result.data(aiChatFacade.uploadFiles(uploadDTO));
    }

    @DeleteMapping("/file")
    @Operation(operationId = "7", summary = "删除文件")
    public Result<Boolean> deleteFiles(
            @Parameter(description = "文件删除对象", required = true) @Valid @RequestBody AiFileDeleteDTO deleteDTO) {
        return Result.status(aiChatFacade.deleteFiles(deleteDTO));
    }
}
