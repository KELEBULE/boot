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
package com.izpan.modules.ai.facade.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.holder.GlobalUserHolder;
import com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO;
import com.izpan.modules.ai.domain.dto.chat.AiChatResponseDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileDeleteDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileUploadDTO;
import com.izpan.modules.ai.domain.entity.AiChatHistory;
import com.izpan.modules.ai.domain.entity.AiChatSession;
import com.izpan.modules.ai.domain.vo.AiChatVO;
import com.izpan.modules.ai.domain.vo.AiSessionVO;
import com.izpan.modules.ai.facade.IAiChatFacade;
import com.izpan.modules.ai.repository.mapper.AiChatHistoryMapper;
import com.izpan.modules.ai.repository.mapper.AiChatSessionMapper;
import com.izpan.modules.ai.repository.mapper.AiFileUploadMapper;
import com.izpan.modules.ai.service.IAiChatService;
import com.izpan.modules.ai.service.IAiFileService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * AI聊天 门面接口实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.facade.impl.AiChatFacadeImpl
 * @CreateTime 2024-12-20
 */
@Service
@RequiredArgsConstructor
public class AiChatFacadeImpl implements IAiChatFacade {

    @NonNull
    private IAiChatService aiChatService;

    @NonNull
    private IAiFileService aiFileService;

    @NonNull
    private AiChatHistoryMapper historyMapper;

    @NonNull
    private AiChatSessionMapper sessionMapper;

    @NonNull
    private AiFileUploadMapper fileUploadMapper;

    @Override
    public AiChatResponseDTO chatCompletion(AiChatRequestDTO request) {
        return aiChatService.chatCompletion(request);
    }

    @Override
    public SseEmitter streamChat(AiChatRequestDTO request) {
        return aiChatService.streamChat(request);
    }

    @Override
    public List<AiChatVO> getSessionHistory(String sessionId) {
        LambdaQueryWrapper<AiChatHistory> queryWrapper = new LambdaQueryWrapper<AiChatHistory>()
                .eq(AiChatHistory::getSessionId, sessionId)
                .orderByAsc(AiChatHistory::getCreateTime);

        IPage<AiChatHistory> page = historyMapper.selectPage(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 100), queryWrapper);
        return page.getRecords().stream()
                .map(history -> AiChatVO.builder()
                .id(history.getId())
                .sessionId(history.getSessionId())
                .role(history.getRole())
                .content(history.getContent())
                .tokensUsed(history.getTokensUsed())
                .processingTime(history.getProcessingTime())
                .createTime(history.getCreateTime())
                .build())
                .toList();
    }

    @Override
    public List<AiSessionVO> getSessionList(Long userId) {
        Long currentUserId = GlobalUserHolder.getUserId();
        LambdaQueryWrapper<AiChatSession> queryWrapper = new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getUserId, currentUserId)
                .orderByDesc(AiChatSession::getLastActiveTime);

        IPage<AiChatSession> page = sessionMapper.selectPage(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 50),
                queryWrapper
        );
        return page.getRecords().stream()
                .map(session -> AiSessionVO.builder()
                .id(session.getId())
                .sessionId(session.getSessionId())
                .title(session.getTitle())
                .model(session.getModel())
                .lastActiveTime(session.getLastActiveTime())
                .createTime(session.getCreateTime())
                .build())
                .toList();
    }

    @Override
    public List<String> getAvailableModels() {
        return aiChatService.getAvailableModels();
    }

    @Override
    public List<String> uploadFiles(AiFileUploadDTO uploadDTO) {
        return aiFileService.uploadFiles(uploadDTO);
    }

    @Override
    @Transactional
    public boolean deleteFiles(AiFileDeleteDTO deleteDTO) {
        try {
            aiFileService.deleteFiles(deleteDTO.getIds());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean clearSession(String sessionId) {
        try {
            aiChatService.clearSessionHistory(sessionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
