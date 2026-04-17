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
package com.izpan.modules.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.izpan.modules.ai.client.OllamaClient;
import com.izpan.modules.ai.domain.dto.config.AiConfigDTO;
import com.izpan.modules.ai.domain.entity.AiConfig;
import com.izpan.modules.ai.domain.vo.AiConfigVO;
import com.izpan.modules.ai.repository.mapper.AiConfigMapper;
import com.izpan.modules.ai.service.IAiConfigService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI配置 Service 服务实现层
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.ai.service.impl.AiConfigServiceImpl
 * @CreateTime 2024-12-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiConfigServiceImpl implements IAiConfigService {

    @NonNull
    private AiConfigMapper configMapper;

    @NonNull
    private OllamaClient ollamaClient;

    @Override
    public AiConfigVO getConfig() {
        Map<String, String> configMap = getConfigMap();
        return AiConfigVO.builder()
                .defaultModel(configMap.getOrDefault("default_model", "qwen3:14b"))
                .temperature(Double.parseDouble(configMap.getOrDefault("temperature", "0.7")))
                .maxTokens(Integer.parseInt(configMap.getOrDefault("max_tokens", "1000")))
                .systemPrompt(configMap.getOrDefault("system_prompt", ""))
                .availableModels(getAvailableModels())
                .serviceStatus(getServiceStatus())
                .build();
    }

    @Override
    @Transactional
    public boolean updateConfig(AiConfigDTO configDTO) {
        if (configDTO.getDefaultModel() != null) {
            updateConfigValue("default_model", configDTO.getDefaultModel());
        }
        if (configDTO.getTemperature() != null) {
            updateConfigValue("temperature", configDTO.getTemperature().toString());
        }
        if (configDTO.getMaxTokens() != null) {
            updateConfigValue("max_tokens", configDTO.getMaxTokens().toString());
        }
        if (configDTO.getSystemPrompt() != null) {
            updateConfigValue("system_prompt", configDTO.getSystemPrompt());
        }
        return true;
    }

    @Override
    public List<String> getAvailableModels() {
        return ollamaClient.listModels();
    }

    @Override
    public Map<String, Object> getServiceStatus() {
        return ollamaClient.healthCheck();
    }

    @Override
    public String getConfigValue(String key) {
        LambdaQueryWrapper<AiConfig> queryWrapper = new LambdaQueryWrapper<AiConfig>()
                .eq(AiConfig::getConfigKey, key);
        AiConfig config = configMapper.selectOne(queryWrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String getSystemPrompt() {
        return getConfigValue("system_prompt");
    }

    private Map<String, String> getConfigMap() {
        Map<String, String> configMap = new HashMap<>();
        List<AiConfig> configs = configMapper.selectList(null);
        for (AiConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    private void updateConfigValue(String key, String value) {
        LambdaQueryWrapper<AiConfig> queryWrapper = new LambdaQueryWrapper<AiConfig>()
                .eq(AiConfig::getConfigKey, key);
        AiConfig config = configMapper.selectOne(queryWrapper);
        if (config != null) {
            config.setConfigValue(value);
            configMapper.updateById(config);
        } else {
            config = new AiConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigType("STRING");
            configMapper.insert(config);
        }
    }
}
