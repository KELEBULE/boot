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

package com.izpan.modules.ai.domain.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI聊天请求 DTO 对象
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.domain.dto.chat.AiChatRequestDTO
 * @CreateTime 2024-12-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AiChatRequestDTO", description = "AI聊天请求 DTO 对象")
public class AiChatRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息内容不能为空")
    private String message;

    @Schema(description = "模型名称，默认llama2")
    @Builder.Default
    private String model = "llama2";

    @Schema(description = "温度参数，默认0.7")
    @Builder.Default
    private Double temperature = 0.7;

    @Schema(description = "最大token数，默认1000")
    @Builder.Default
    private Integer maxTokens = 1000;

    @Schema(description = "是否流式响应，默认false")
    @Builder.Default
    private Boolean stream = false;

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "历史消息")
    private List<AiMessageDTO> history;

    @Schema(description = "上传的文件列表")
    private List<String> fileUrls;
}
