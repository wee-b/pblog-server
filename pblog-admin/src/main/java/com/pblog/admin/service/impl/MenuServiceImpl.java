package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.Assert;
import com.pblog.admin.mapper.MenuMapper;
import com.pblog.admin.service.MenuService;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.entity.rabc.PbMenu;
import com.pblog.common.vo.MenuVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(PbMenu)表服务实现类
 *
 * @author makejava
 * @since 2025-09-22 23:20:34
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;


    @Override
    public List<MenuVO> getAllMenu() {

        List<PbMenu> menus = menuMapper.selectList(null);
        List<MenuVO> collect1 = menus.stream()
                .map(menu -> {
                    MenuVO vo = new MenuVO();
                    // 只填充部分字段
                    vo.setId(menu.getId());
                    vo.setMenuName(menu.getMenuName());
                    vo.setParentId(menu.getParentId());
                    vo.setOrderNum(menu.getOrderNum());
                    return vo;
                })
                .collect(Collectors.toList());

        return collect1;
    }

    @Override
    public MenuVO queryMenuById(Integer id) {
        PbMenu menu = menuMapper.selectById(id);
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }

    @Override
    public void addMenu(MenuVO menu) {
        Assert.notNull(menu, "菜单信息不能为空");
        Assert.hasText(menu.getMenuName(), "菜单名称不能为空");

        PbMenu pbMenu = new PbMenu();
        BeanUtils.copyProperties(menu, pbMenu);
        pbMenu.setStatus(DefaultConstants.DEFAULT_STATUS);
        pbMenu.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);
        menuMapper.insert(pbMenu);
    }

    @Override
    public void updateMenu(MenuVO menu) {
        Assert.notNull(menu, "菜单信息不能为空");
        Assert.notNull(menu.getId(), "菜单ID不能为空");

        // 校验菜单是否存在
        PbMenu existingMenu = menuMapper.selectById(menu.getId());
        Assert.notNull(existingMenu, "菜单不存在");

        // 拷贝属性并更新
        PbMenu updateMenu = new PbMenu();
        BeanUtils.copyProperties(menu, updateMenu);
        // 保留原有创建信息，避免被覆盖
        updateMenu.setCreateBy(existingMenu.getCreateBy());
        updateMenu.setCreateTime(existingMenu.getCreateTime());
        updateMenu.setDelFlag(existingMenu.getDelFlag()); // 不允许通过更新修改删除状态

        menuMapper.updateById(updateMenu);
    }

    @Override
    public void deleteMenuById(Integer id) {
        Assert.notNull(id, "菜单ID不能为空");

        // 校验是否存在子菜单
        long childCount = menuMapper.selectCount(
                new LambdaQueryWrapper<PbMenu>()
                        .eq(PbMenu::getParentId, id)
                        .eq(PbMenu::getDelFlag, DefaultConstants.DEFAULT_DELFLAG)
        );
        Assert.isTrue(childCount == 0, "存在子菜单，不允许删除");

        menuMapper.deleteById(id);
    }

    @Override
    public void enableMenuById(Integer id) {
        Assert.notNull(id, "菜单ID不能为空");

        PbMenu menu = new PbMenu();
        menu.setId(id);
        // 切换状态（1正常 <-> 0停用）
        PbMenu current = menuMapper.selectById(id);
        Assert.notNull(current, "菜单不存在");

        String status = DefaultConstants.DEFAULT_STATUS.equals(current.getStatus()) ? DefaultConstants.Banned_Status : DefaultConstants.DEFAULT_STATUS;
        menu.setStatus(status);
        menuMapper.updateById(menu);
    }
}
