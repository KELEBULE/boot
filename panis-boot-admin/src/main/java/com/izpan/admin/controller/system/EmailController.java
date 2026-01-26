package com.izpan.admin.controller.system;

import com.izpan.common.api.Result;
import com.izpan.modules.system.domain.dto.EmailSendCodeDTO;
import com.izpan.modules.system.domain.dto.EmailVerifyCodeDTO;
import com.izpan.modules.system.service.IEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "邮箱验证")
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    @NonNull
    private IEmailService emailService;

    @PostMapping("/send_code")
    @Operation(summary = "发送邮箱验证码")
    public Result<Void> sendVerificationCode(@RequestBody EmailSendCodeDTO dto) {
        emailService.sendVerificationCode(dto.getEmail());
        return Result.success();
    }

    @PostMapping("/verify_code")
    @Operation(summary = "验证邮箱验证码")
    public Result<Boolean> verifyCode(@RequestBody EmailVerifyCodeDTO dto) {
        boolean isValid = emailService.verifyCode(dto.getEmail(), dto.getCode());
        return Result.data(isValid);
    }
}