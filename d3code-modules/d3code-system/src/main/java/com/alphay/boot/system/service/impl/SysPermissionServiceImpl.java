package com.alphay.boot.system.service.impl;

import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.service.ISysMenuService;
import com.alphay.boot.system.service.ISysPermissionService;
import com.alphay.boot.system.service.ISysRoleService;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 用户权限处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysPermissionServiceImpl implements ISysPermissionService {

  @Resource private ISysRoleService roleService;
  @Resource private ISysMenuService menuService;

  /**
   * 获取角色数据权限
   *
   * @param userId 用户id
   * @return 角色权限信息
   */
  @Override
  public Set<String> getRolePermission(Long userId) {
    Set<String> roles = new HashSet<>();
    // 管理员拥有所有权限
    if (LoginHelper.isSuperAdmin(userId)) {
      roles.add(TenantConstants.SUPER_ADMIN_ROLE_KEY);
    } else {
      roles.addAll(roleService.fetchPermissionByUserId(userId));
    }
    return roles;
  }

  /**
   * 获取菜单数据权限
   *
   * @param userId 用户id
   * @return 菜单权限信息
   */
  @Override
  public Set<String> getMenuPermission(Long userId) {
    Set<String> perms = new HashSet<>();
    // 管理员拥有所有权限
    if (LoginHelper.isSuperAdmin(userId)) {
      perms.add("*:*:*");
    } else {
      perms.addAll(menuService.fetchMenuPermsByUserId(userId));
    }
    return perms;
  }
}
