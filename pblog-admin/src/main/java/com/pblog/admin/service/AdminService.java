package com.pblog.admin.service;

import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.dto.login.PasswordLoginDTO;
import com.pblog.common.dto.admin.AdminRegisterDTO;
import com.pblog.common.entity.User;
import com.pblog.common.vo.UserAdminInfoVO;

import java.util.List;
import java.util.Map;

public interface AdminService {

    Map<String, String> login(PasswordLoginDTO passwordLoginDTO);

    void logout();

    void edit(String username);

    List<UserAdminInfoVO> UserPageQuery(PageQueryDTO pageQueryDTO);

    User getUserAllInfo(String username);

    void addPerson(AdminRegisterDTO adminRegisterDTO);

    void updatePerson(AdminRegisterDTO adminRegisterDTO);
}
