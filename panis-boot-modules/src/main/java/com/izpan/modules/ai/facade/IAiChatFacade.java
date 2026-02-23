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
package com.izpan.modules.ai.facade;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO;
import com.izpan.modules.ai.domain.dto.chat.AiChatResponseDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileDeleteDTO;
import com.izpan.modules.ai.domain.dto.file.AiFileUploadDTO;
import com.izpan.modules.ai.domain.vo.AiChatVO;
import com.izpan.modules.ai.domain.vo.AiSessionVO;

/**
 * AI聊天 门面接口层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.facade.IAiChatFacade
 * @CreateTime 2024-12-20
 */
public interface IAiChatFacade {

    AiChatResponseDTO chatCompletion(AiChatRequestDTO request);

    SseEmitter streamChat(AiChatRequestDTO request);

    List<AiChatVO> getSessionHistory(String sessionId);

    List<AiSessionVO> getSessionList(Long userId);

    List<String> getAvailableModels();

    List<String> uploadFiles(AiFileUploadDTO uploadDTO);

    boolean deleteFiles(AiFileDeleteDTO deleteDTO);

    boolean clearSession(String sessionId);
}
