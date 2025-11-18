package com.pblog.admin.controller;

import com.pblog.admin.service.MenuService;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.MenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 用于列表展示，只填充部分字段
     * @return
     */
    @GetMapping("/getAll")
    public ResponseResult<List<MenuVO>> getAllMenu() {
        List<MenuVO> res = menuService.getAllMenu();
        return ResponseResult.success(res);
    }

    @GetMapping("/queryById/{id}")
    public ResponseResult<MenuVO> queryMenuById(@PathVariable("id") Integer id) {
        MenuVO menuVO = menuService.queryMenuById(id);
        return ResponseResult.success(menuVO);
    }

    @PostMapping("/add")
    public ResponseResult<String> addMenu(@RequestBody MenuVO menu) {
        menuService.addMenu(menu);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    public ResponseResult<String> updateMenu(@RequestBody MenuVO menu) {
        menuService.updateMenu(menu);
        return ResponseResult.success();
    }

    @PostMapping("/delete/{id}")
    public ResponseResult<String> deleteMenuById(@PathVariable("id") Integer id) {
        menuService.deleteMenuById(id);
        return ResponseResult.success();
    }

    @PostMapping("/status/{id}")
    public ResponseResult<String> enableMenuById(@PathVariable("id") Integer id) {
        menuService.enableMenuById(id);
        return ResponseResult.success();
    }

}
