-- AI配置表
CREATE TABLE IF NOT EXISTS `ai_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_type` VARCHAR(50) DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/JSON',
    `description` VARCHAR(500) COMMENT '配置描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI配置表';

-- 初始化默认配置
INSERT INTO `ai_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('default_model', 'qwen3:14b', 'STRING', '默认使用的AI模型'),
('temperature', '0.7', 'NUMBER', '温度参数，控制回答的随机性'),
('max_tokens', '1000', 'NUMBER', '最大生成Token数'),
('system_prompt', '你是一个专业的设备管理AI助手，帮助用户查询和分析设备报警数据。

你的职责：
1. 理解用户的问题，判断是否需要查询数据库
2. 当需要查询数据时，调用相应的工具函数获取数据
3. 基于查询结果，用自然语言向用户解释和分析数据

回答要求：
1. 使用中文回答
2. 对查询到的数据进行简要分析
3. 如果数据为空，告知用户没有找到相关数据
4. 对于报警数据，分析其严重程度和可能的原因
5. 提供专业的建议和解决方案', 'STRING', '系统提示词');
