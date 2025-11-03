package com.pblog.user.service;


import com.pblog.common.dto.PasswordLoginDTO;

import java.util.Map;

public interface UserService {
    Map<String,String> login(PasswordLoginDTO passwordLoginDTO) throws Exception;
}
