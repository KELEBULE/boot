package com.izpan.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 自定义业务异常
 *
 * @Author lingma
 * @ProjectName panis-boot
 * @ClassName com.izpan.common.exception.BusinessException
 * @CreateTime 2026-02-21
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5693633393981860488L;

    /**
     * 错误信息
     */
    private final String msg;

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }
}