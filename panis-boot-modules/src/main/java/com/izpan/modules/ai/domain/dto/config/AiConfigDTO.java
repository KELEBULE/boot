/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.izpan.modules.ai.domain.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI配置请求 DTO
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.domain.dto.config.AiConfigDTO
 * @CreateTime 2024-12-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AiConfigDTO", description = "AI配置请求 DTO")
public class AiConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "默认模型")
    private String defaultModel;

    @Schema(description = "温度参数")
    private Double temperature;

    @Schema(description = "最大Token数")
    private Integer maxTokens;

    @Schema(description = "系统提示词")
    private String systemPrompt;
}
