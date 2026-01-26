package com.izpan.modules.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "验证邮箱验证码DTO")
public class EmailVerifyCodeDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;
}