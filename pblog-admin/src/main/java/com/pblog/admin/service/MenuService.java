package com.pblog.admin.service;

import com.pblog.common.vo.MenuVO;

import java.util.List;

public interface MenuService {

    List<MenuVO> getAllMenu();

    MenuVO queryMenuById(Integer id);

    void addMenu(MenuVO menu);

    void updateMenu(MenuVO menu);

    void deleteMenuById(Integer id);

    void enableMenuById(Integer id);
}
