package com.pblog.user.service;


import com.pblog.common.dto.EmailCodeLoginDTO;
import com.pblog.common.dto.EmailLoginDTO;
import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.dto.RegisterDTO;

import java.util.Map;

public interface UserService {
    Map<String,String> passwordLogin(PasswordLoginDTO passwordLoginDTO) throws Exception;

    Map<String, String> emailLogin(EmailLoginDTO emailLoginDTO);

    Map<String, String> emailCodeLogin(EmailCodeLoginDTO emailCodeLoginDTO);

    String register(RegisterDTO registerDTO);
}
