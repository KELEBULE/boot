-- AI聊天会话表
CREATE TABLE IF NOT EXISTS `ai_chat_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `model` VARCHAR(50) DEFAULT 'llama2' COMMENT '使用的模型',
    `title` VARCHAR(200) DEFAULT '新对话' COMMENT '会话标题',
    `last_active_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建用户',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新用户',
    `update_user_id` BIGINT DEFAULT NULL COMMENT '更新用户ID',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除(0:否,1:是)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI聊天会话表';

-- AI聊天历史表
CREATE TABLE IF NOT EXISTS `ai_chat_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色：user/assistant',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `tokens_used` INT DEFAULT NULL COMMENT '使用的token数',
    `processing_time` BIGINT DEFAULT NULL COMMENT '处理时间(ms)',
    `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建用户',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新用户',
    `update_user_id` BIGINT DEFAULT NULL COMMENT '更新用户ID',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除(0:否,1:是)',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI聊天历史表';

-- AI文件上传表
CREATE TABLE IF NOT EXISTS `ai_file_upload` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `session_id` VARCHAR(64) DEFAULT NULL COMMENT '关联的会话ID',
    `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建用户',
    `create_user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新用户',
    `update_user_id` BIGINT DEFAULT NULL COMMENT '更新用户ID',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除(0:否,1:是)',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI文件上传表';