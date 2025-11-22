package com.pblog.user.service;

import com.pblog.common.Expection.BusinessException;
import com.pblog.common.vo.CaptchaVO;

public interface CodeService {

    String verifyEmailCode(String receiveEmail);

    void sendEmailCode(String email);

    void verifyEmailCode(String key, String code) throws BusinessException;

    CaptchaVO generateCaptcha();

    boolean verifyCaptcha(String captchaUuid, String userInputCode);
}
