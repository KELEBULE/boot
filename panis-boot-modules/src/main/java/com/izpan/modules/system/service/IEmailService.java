package com.izpan.modules.system.service;

import com.izpan.modules.system.domain.dto.EmailRegisterDTO;

public interface IEmailService {
    
    void sendVerificationCode(String email);
    
    boolean verifyCode(String email, String code);

    void registerWithEmail(EmailRegisterDTO emailRegisterDTO);
}