package com.izpan.modules.system.service.impl;

import com.izpan.common.exception.BizException;
import com.izpan.infrastructure.util.RedisUtil;
import com.izpan.modules.system.domain.dto.EmailRegisterDTO;
import com.izpan.modules.system.domain.entity.SysUser;
import com.izpan.modules.system.service.IEmailService;
import com.izpan.modules.system.service.ISysUserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final ISysUserService sysUserService;



    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String CODE_PREFIX = "email:code:";
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int CODE_LENGTH = 6;

    @Override
    public void sendVerificationCode(String email) {
        String code = generateCode();
        
        RedisUtil.set(CODE_PREFIX + email, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("邮箱验证码");
            helper.setText("您的验证码是：" + code + "，有效期为" + CODE_EXPIRE_MINUTES + "分钟。请勿泄露给他人。", true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("发送邮件失败", e);
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = (String) RedisUtil.get(CODE_PREFIX + email);
        if (storedCode == null) {
            return false;
        }
        boolean isValid = storedCode.equals(code);
        if (isValid) {
            RedisUtil.del(CODE_PREFIX + email);
        }
        return isValid;
    }

    @Override
    public void registerWithEmail(EmailRegisterDTO emailRegisterDTO) {
        String email = emailRegisterDTO.getEmail();
        String code = emailRegisterDTO.getCode();
        String userName = emailRegisterDTO.getUserName();
        String password = emailRegisterDTO.getPassword();
        String realName = emailRegisterDTO.getRealName();

        boolean isValid = verifyCode(email, code);
        if (!isValid) {
            throw new BizException("验证码无效或已过期");
        }

        SysUser existingUser = sysUserService.getUserByEmail(email);
        if (existingUser != null) {
            throw new BizException("该邮箱已被注册");
        }

        SysUser existingUserName = sysUserService.getUserByUserName(userName);
        if (existingUserName != null) {
            throw new BizException("该用户名已被使用");
        }

        String salt = RandomStringUtils.secureStrong().nextAlphabetic(6);
        String sha256HexPwd = DigestUtils.sha256Hex(password);
        String encryptedPassword = DigestUtils.sha256Hex(sha256HexPwd + salt);

        SysUser newUser = SysUser.builder()
                .userName(userName)
                .password(encryptedPassword)
                .realName(realName)
                .email(email)
                .salt(salt)
                .status("1")
                .gender("0")
                .build();

        sysUserService.save(newUser);
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}