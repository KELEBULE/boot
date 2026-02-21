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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.izpan.common.api.Result;
import com.izpan.modules.ai.client.OllamaClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * AI健康检查 Controller 控制层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.admin.controller.ai.AiHealthController
 * @CreateTime 2024-12-20
 */
@RestController
@Tag(name = "AI健康检查")
@RequiredArgsConstructor
@RequestMapping("ai_health")
public class AiHealthController {

    @NonNull
    private OllamaClient ollamaClient;

    @GetMapping
    @Operation(operationId = "1", summary = "AI服务健康检查")
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();

        healthStatus.put("service", "AI Assistant");
        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now());
        healthStatus.put("version", "1.0.0");

        Map<String, Object> ollamaStatus = ollamaClient.healthCheck();
        healthStatus.put("ollama", ollamaStatus);

        Runtime runtime = Runtime.getRuntime();
        healthStatus.put("system", Map.of(
                "freeMemory", runtime.freeMemory() / 1024 / 1024 + "MB",
                "totalMemory", runtime.totalMemory() / 1024 / 1024 + "MB",
                "maxMemory", runtime.maxMemory() / 1024 / 1024 + "MB",
                "availableProcessors", runtime.availableProcessors()
        ));

        return Result.data(healthStatus);
    }
}
