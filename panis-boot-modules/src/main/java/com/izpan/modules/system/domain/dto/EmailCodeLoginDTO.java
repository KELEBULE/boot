package com.izpan.modules.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱验证码登录对象
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.system.domain.dto.EmailCodeLoginDTO
 * @CreateTime 2026-01-25
 */

@Getter
@Setter
@Schema(description = "邮箱验证码登录对象")
public class EmailCodeLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890L;

    @NotBlank
    @Schema(description = "邮箱地址")
    private String email;

    @NotBlank
    @Schema(description = "验证码")
    private String code;
}
