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
package com.izpan.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.izpan.common.api.Result;
import com.izpan.modules.ai.domain.dto.config.AiConfigDTO;
import com.izpan.modules.ai.domain.vo.AiConfigVO;
import com.izpan.modules.ai.service.IAiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI配置 Controller 控制层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.admin.controller.ai.AiConfigController
 * @CreateTime 2024-12-20
 */
@RestController
@Tag(name = "AI配置")
@RequiredArgsConstructor
@RequestMapping("ai_config")
public class AiConfigController {

    @NonNull
    private IAiConfigService aiConfigService;

    @GetMapping
    @Operation(summary = "获取AI配置")
    public Result<AiConfigVO> getConfig() {
        return Result.data(aiConfigService.getConfig());
    }

    @PutMapping
    @SaCheckRole("ADMIN")
    @Operation(summary = "更新AI配置")
    public Result<Boolean> updateConfig(@RequestBody AiConfigDTO configDTO) {
        return Result.status(aiConfigService.updateConfig(configDTO));
    }

    @GetMapping("/models")
    @Operation(summary = "获取可用模型列表")
    public Result<List<String>> getAvailableModels() {
        return Result.data(aiConfigService.getAvailableModels());
    }

    @GetMapping("/status")
    @Operation(summary = "获取Ollama服务状态")
    public Result<Map<String, Object>> getServiceStatus() {
        return Result.data(aiConfigService.getServiceStatus());
    }
}
