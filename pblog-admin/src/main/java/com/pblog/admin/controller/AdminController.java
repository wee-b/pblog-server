package com.pblog.admin.controller;

import com.pblog.admin.service.AdminService;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.dto.admin.AdminRegisterDTO;
import com.pblog.common.entity.User;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.UserAdminInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseResult<Map<String,String>> login(@RequestBody PasswordLoginDTO passwordLoginDTO) {
        log.info("管理员登录请求:{}", passwordLoginDTO);
        Map<String,String> res = adminService.login(passwordLoginDTO);
        return ResponseResult.success(res);
    }

    @PostMapping("/logout")
    public ResponseResult<String> logout() {
        log.info("管理员退出请求");
        adminService.logout();
        return ResponseResult.success();
    }

    @PostMapping("/status")
    public ResponseResult edit(@RequestParam String username){
        log.info("启用/禁用账号请求:{}" ,username);
        adminService.edit(username);
        return ResponseResult.success();
    }

    @PostMapping("/addPerson")
    public ResponseResult addPerson(@RequestBody AdminRegisterDTO adminRegisterDTO) {
        log.info("管理员注册请求");
        adminService.addPerson(adminRegisterDTO);
        return ResponseResult.success();
    }

    @GetMapping("/getUserAllInfo")
    public ResponseResult<User> getUserAllInfo(@RequestParam String username) {
        log.info("获取用户详细信息请求");
        User res = adminService.getUserAllInfo(username);
        return ResponseResult.success(res);
    }

    @GetMapping("/UserPageQuery")
    public ResponseResult<List<UserAdminInfoVO>> UserPageQuery(@RequestBody PageQueryDTO pageQueryDTO) {
        log.info("用户信息分页查询请求");
        List<UserAdminInfoVO> res = adminService.UserPageQuery(pageQueryDTO);
        return ResponseResult.success(res);
    }

    @PutMapping("/updateInfo")
    public ResponseResult updatePerson(@RequestBody AdminRegisterDTO adminRegisterDTO) {
        log.info("管理员更新信息请求");
        adminService.updatePerson(adminRegisterDTO);
        return ResponseResult.success();
    }

}
