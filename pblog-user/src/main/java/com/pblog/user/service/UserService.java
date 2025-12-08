package com.pblog.user.service;


import com.pblog.common.dto.*;
import com.pblog.common.dto.login.EmailCodeDTO;
import com.pblog.common.dto.login.PasswordLoginDTO;
import com.pblog.common.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface UserService {
    Map<String,String> passwordLogin(PasswordLoginDTO passwordLoginDTO);

    Map<String, String> emailLogin(EmailLoginDTO emailLoginDTO);

    Map<String, String> emailLoginOrRegister(EmailCodeDTO emailCodeLoginDTO);

    String register(RegisterDTO registerDTO);

    String resetPassword(ResetPasswordDTO resetPasswordDTO);

    Map<String, String> updateInfo(UserDTO userDTO);

    String updateEmail(EmailCodeDTO emailCodeDTO);

    String forgetPassword(String newPassword);

    String logout();

    String deleteAccount(HttpServletRequest request, HttpServletResponse response);

    UserInfoVO getUserInfo();

    UserInfoVO getUserInfoByUserName(String username);

    String getEmail();

}
