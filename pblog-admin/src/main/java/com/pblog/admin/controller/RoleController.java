package com.pblog.admin.controller;

import com.pblog.admin.service.RoleService;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 用于列表展示，只填充部分字段
     * @return
     */
    @GetMapping("/getAll")
    public ResponseResult<List<RoleVO>> getAllRoles(){
        List<RoleVO> res = roleService.getAllRoles();
        return ResponseResult.success(res);
    }

    @GetMapping("/queryById/{id}")
    public ResponseResult<RoleVO> getRoleById(@PathVariable("id") Integer id){
        RoleVO roleVO = roleService.getRoleById(id);
        return ResponseResult.success(roleVO);
    }

    @PostMapping("/add")
    public ResponseResult<String> addRole(@RequestBody RoleVO role){
        roleService.addRole(role);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    public ResponseResult<String> updateRole(@RequestBody RoleVO role){
        roleService.updateRole(role);
        return ResponseResult.success();
    }

    @PostMapping("/delete/{id}")
    public ResponseResult<String> deleteRoleById(@PathVariable("id") Integer id){
        roleService.deleteRoleById(id);
        return ResponseResult.success();
    }

    /**
     * 启用/禁用
     */
    @PostMapping("/status/{id}")
    public ResponseResult<String> enableRole(@PathVariable("id") Integer id){
        roleService.enableRole(id);
        return ResponseResult.success();
    }
}
