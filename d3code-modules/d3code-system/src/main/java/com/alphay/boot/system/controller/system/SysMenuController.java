package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.tree.Tree;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysMenuQueryParam;
import com.alphay.boot.system.domain.SysMenu;
import com.alphay.boot.system.domain.bo.SysMenuBo;
import com.alphay.boot.system.domain.vo.RouterVo;
import com.alphay.boot.system.domain.vo.SysMenuVo;
import com.alphay.boot.system.service.ISysMenuService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController {

  @Resource private ISysMenuService menuService;

  /**
   * 获取路由信息
   *
   * @return 路由信息
   */
  @GetMapping("/getRouters")
  public R<List<RouterVo>> getRouters() {
    List<SysMenu> menus = menuService.queryMenuTreeByUserId(LoginHelper.getUserId());
    return R.ok(menuService.buildMenus(menus));
  }

  /** 获取菜单列表 */
  @SaCheckRole(
      value = {TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY},
      mode = SaMode.OR)
  @SaCheckPermission("system:menu:list")
  @GetMapping("/list")
  public R<List<SysMenuVo>> list(SysMenuQueryParam menu) {
    List<SysMenuVo> menus = menuService.queryList(menu, LoginHelper.getUserId());
    return R.ok(menus);
  }

  /**
   * 根据菜单编号获取详细信息
   *
   * @param menuId 菜单ID
   */
  @SaCheckRole(
      value = {TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY},
      mode = SaMode.OR)
  @SaCheckPermission("system:menu:query")
  @GetMapping(value = "/{menuId}")
  public R<SysMenuVo> getInfo(@PathVariable Long menuId) {
    return R.ok(menuService.getVoById(menuId));
  }

  /** 获取菜单下拉树列表 */
  @SaCheckPermission("system:menu:query")
  @GetMapping("/treeselect")
  public R<List<Tree<Long>>> treeselect(SysMenuQueryParam menu) {
    List<SysMenuVo> menus = menuService.queryList(menu, LoginHelper.getUserId());
    return R.ok(menuService.buildMenuTreeSelect(menus));
  }

  /**
   * 加载对应角色菜单列表树
   *
   * @param roleId 角色ID
   */
  @SaCheckPermission("system:menu:query")
  @GetMapping(value = "/roleMenuTreeselect/{roleId}")
  public R<MenuTreeSelectVo> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
    List<SysMenuVo> menus = menuService.queryList(LoginHelper.getUserId());
    MenuTreeSelectVo selectVo =
        new MenuTreeSelectVo(
            menuService.fetchMenuListByRoleId(roleId), menuService.buildMenuTreeSelect(menus));
    return R.ok(selectVo);
  }

  /**
   * 加载对应租户套餐菜单列表树
   *
   * @param packageId 租户套餐ID
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:menu:query")
  @GetMapping(value = "/tenantPackageMenuTreeselect/{packageId}")
  public R<MenuTreeSelectVo> tenantPackageMenuTreeselect(
      @PathVariable("packageId") Long packageId) {
    List<SysMenuVo> menus = menuService.queryList(LoginHelper.getUserId());
    List<Tree<Long>> list = menuService.buildMenuTreeSelect(menus);
    // 删除租户管理菜单
    list.removeIf(menu -> menu.getId() == 6L);
    List<Long> ids = new ArrayList<>();
    if (packageId > 0L) {
      ids = menuService.fetchMenuListByPackageId(packageId);
    }
    MenuTreeSelectVo selectVo = new MenuTreeSelectVo(ids, list);
    return R.ok(selectVo);
  }

  /** 新增菜单 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:menu:add")
  @Log(title = "菜单管理", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysMenuBo menu) {
    if (!menuService.checkMenuNameUnique(menu)) {
      return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
    } else if (SystemConstants.YES_FRAME.equals(menu.getIsFrame())
        && !StringUtils.ishttp(menu.getPath())) {
      return R.fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
    }
    return toAjax(menuService.insertMenu(menu));
  }

  /** 修改菜单 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:menu:edit")
  @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysMenuBo menu) {
    if (!menuService.checkMenuNameUnique(menu)) {
      return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
    } else if (SystemConstants.YES_FRAME.equals(menu.getIsFrame())
        && !StringUtils.ishttp(menu.getPath())) {
      return R.fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
    } else if (menu.getMenuId().equals(menu.getParentId())) {
      return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
    }
    return toAjax(menuService.updateMenu(menu));
  }

  /**
   * 删除菜单
   *
   * @param menuId 菜单ID
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:menu:remove")
  @Log(title = "菜单管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{menuId}")
  public R<Void> remove(@PathVariable("menuId") Long menuId) {
    if (menuService.hasChildByMenuId(menuId)) {
      return R.warn("存在子菜单,不允许删除");
    }
    if (menuService.checkMenuExistRole(menuId)) {
      return R.warn("菜单已分配,不允许删除");
    }
    return toAjax(menuService.deleteMenuById(menuId));
  }

  /**
   * 批量级联删除菜单
   *
   * @param menuIds 菜单ID串
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:menu:remove")
  @Log(title = "菜单管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/cascade/{menuIds}")
  public R<Void> remove(@PathVariable("menuIds") Long[] menuIds) {
    List<Long> menuIdList = List.of(menuIds);
    if (menuService.hasChildByMenuId(menuIdList)) {
      return R.warn("存在子菜单,不允许删除");
    }
    menuService.deleteMenuById(menuIdList);
    return R.ok();
  }

  public record MenuTreeSelectVo(List<Long> checkedKeys, List<Tree<Long>> menus) {}
}
