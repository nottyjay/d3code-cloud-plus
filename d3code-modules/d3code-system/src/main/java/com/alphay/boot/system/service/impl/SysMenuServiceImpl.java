package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.TreeBuildUtils;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.api.domain.param.SysMenuQueryParam;
import com.alphay.boot.system.domain.SysMenu;
import com.alphay.boot.system.domain.SysRole;
import com.alphay.boot.system.domain.SysRoleMenu;
import com.alphay.boot.system.domain.SysTenantPackage;
import com.alphay.boot.system.domain.bo.SysMenuBo;
import com.alphay.boot.system.domain.vo.MetaVo;
import com.alphay.boot.system.domain.vo.RouterVo;
import com.alphay.boot.system.domain.vo.SysMenuVo;
import com.alphay.boot.system.mapper.SysMenuMapper;
import com.alphay.boot.system.mapper.SysRoleMapper;
import com.alphay.boot.system.mapper.SysRoleMenuMapper;
import com.alphay.boot.system.mapper.SysTenantPackageMapper;
import com.alphay.boot.system.service.ISysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单 业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends ServiceImplX<SysMenuMapper, SysMenu, SysMenuVo>
    implements ISysMenuService {

  @Resource private SysRoleMapper roleMapper;
  @Resource private SysRoleMenuMapper roleMenuMapper;
  @Resource private SysTenantPackageMapper tenantPackageMapper;

  /**
   * 根据用户查询系统菜单列表
   *
   * @param userId 用户ID
   * @return 菜单列表
   */
  @Override
  public List<SysMenuVo> queryList(Long userId) {
    return queryList(new SysMenuQueryParam(), userId);
  }

  /**
   * 查询系统菜单列表
   *
   * @param param 菜单信息
   * @return 菜单列表
   */
  @Override
  public List<SysMenuVo> queryList(SysMenuQueryParam param, Long userId) {
    List<SysMenuVo> menuList;
    // 管理员显示所有菜单信息
    if (LoginHelper.isSuperAdmin(userId)) {
      menuList =
          listVo(
              this.lambdaQueryWrapper()
                  .likeIfPresent(SysMenu::getMenuName, param.getMenuName())
                  .eqIfPresent(SysMenu::getVisible, param.getVisible())
                  .eqIfPresent(SysMenu::getStatus, param.getStatus())
                  .eqIfPresent(SysMenu::getMenuType, param.getMenuType())
                  .eqIfPresent(SysMenu::getParentId, param.getParentId())
                  .orderByAsc(SysMenu::getParentId)
                  .orderByAsc(SysMenu::getOrderNum));
    } else {
      QueryWrapper<SysMenu> wrapper =
          this.queryWrapper()
              .inSql("r.role_id", "select role_id from sys_user_role where user_id = " + userId)
              .likeIfPresent("m.menu_name", param.getMenuName())
              .eqIfPresent("m.visible", param.getVisible())
              .eqIfPresent("m.status", param.getStatus())
              .eqIfPresent("m.menu_type", param.getMenuType())
              .eqIfPresent("m.parent_id", param.getParentId())
              .orderByAsc("m.parent_id")
              .orderByAsc("m.order_num");
      List<SysMenu> list = baseMapper.selectMenuListByUserId(wrapper);
      menuList = MapstructUtils.convert(list, SysMenuVo.class);
    }
    return menuList;
  }

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return 权限列表
   */
  @Override
  public Set<String> fetchMenuPermsByUserId(Long userId) {
    List<String> perms = baseMapper.selectMenuPermsByUserId(userId);
    Set<String> permsSet = new HashSet<>();
    for (String perm : perms) {
      if (StringUtils.isNotEmpty(perm)) {
        permsSet.addAll(StringUtils.splitList(perm.trim()));
      }
    }
    return permsSet;
  }

  /**
   * 根据角色ID查询权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  @Override
  public Set<String> fetchMenuPermsByRoleId(Long roleId) {
    List<String> perms = baseMapper.selectMenuPermsByRoleId(roleId);
    Set<String> permsSet = new HashSet<>();
    for (String perm : perms) {
      if (StringUtils.isNotEmpty(perm)) {
        permsSet.addAll(StringUtils.splitList(perm.trim()));
      }
    }
    return permsSet;
  }

  /**
   * 根据用户ID查询菜单
   *
   * @param userId 用户名称
   * @return 菜单列表
   */
  @Override
  public List<SysMenu> queryMenuTreeByUserId(Long userId) {
    List<SysMenu> menus;
    if (LoginHelper.isSuperAdmin(userId)) {
      menus = baseMapper.selectMenuTreeAll();
    } else {
      menus = baseMapper.selectMenuTreeByUserId(userId);
    }
    return getChildPerms(menus, Constants.TOP_PARENT_ID);
  }

  /**
   * 根据角色ID查询菜单树信息
   *
   * @param roleId 角色ID
   * @return 选中菜单列表
   */
  @Override
  public List<Long> fetchMenuListByRoleId(Long roleId) {
    SysRole role = roleMapper.selectById(roleId);
    return baseMapper.selectMenuListByRoleId(roleId, role.getMenuCheckStrictly());
  }

  /**
   * 根据租户套餐ID查询菜单树信息
   *
   * @param packageId 租户套餐ID
   * @return 选中菜单列表
   */
  @Override
  public List<Long> fetchMenuListByPackageId(Long packageId) {
    SysTenantPackage tenantPackage = tenantPackageMapper.selectById(packageId);
    List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);
    if (CollUtil.isEmpty(menuIds)) {
      return new ArrayList<>();
    }
    List<Long> parentIds = null;
    if (tenantPackage.getMenuCheckStrictly()) {
      parentIds =
          baseMapper.selectObjs(
              new LambdaQueryWrapper<SysMenu>()
                  .select(SysMenu::getParentId)
                  .in(SysMenu::getMenuId, menuIds),
              x -> {
                return Convert.toLong(x);
              });
    }
    return baseMapper.selectObjs(
        new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getMenuId, menuIds)
            .notIn(CollUtil.isNotEmpty(parentIds), SysMenu::getMenuId, parentIds),
        x -> {
          return Convert.toLong(x);
        });
  }

  /**
   * 构建前端路由所需要的菜单 路由name命名规则 path首字母转大写 + id
   *
   * @param menus 菜单列表
   * @return 路由列表
   */
  @Override
  public List<RouterVo> buildMenus(List<SysMenu> menus) {
    List<RouterVo> routers = new LinkedList<>();
    for (SysMenu menu : menus) {
      String name = menu.getRouteName() + menu.getMenuId();
      RouterVo router = new RouterVo();
      router.setHidden("1".equals(menu.getVisible()));
      router.setName(name);
      router.setPath(menu.getRouterPath());
      router.setComponent(menu.getComponentInfo());
      router.setQuery(menu.getQueryParam());
      router.setMeta(
          new MetaVo(
              menu.getMenuName(),
              menu.getIcon(),
              StringUtils.equals("1", menu.getIsCache()),
              menu.getPath()));
      List<SysMenu> cMenus = menu.getChildren();
      if (CollUtil.isNotEmpty(cMenus) && SystemConstants.TYPE_DIR.equals(menu.getMenuType())) {
        router.setAlwaysShow(true);
        router.setRedirect("noRedirect");
        router.setChildren(buildMenus(cMenus));
      } else if (menu.isMenuFrame()) {
        String frameName = StringUtils.capitalize(menu.getPath()) + menu.getMenuId();
        router.setMeta(null);
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        children.setPath(menu.getPath());
        children.setComponent(menu.getComponent());
        children.setName(frameName);
        children.setMeta(
            new MetaVo(
                menu.getMenuName(),
                menu.getIcon(),
                StringUtils.equals("1", menu.getIsCache()),
                menu.getPath()));
        children.setQuery(menu.getQueryParam());
        childrenList.add(children);
        router.setChildren(childrenList);
      } else if (menu.getParentId().equals(Constants.TOP_PARENT_ID) && menu.isInnerLink()) {
        router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
        router.setPath("/");
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        String routerPath = SysMenu.innerLinkReplaceEach(menu.getPath());
        String innerLinkName = StringUtils.capitalize(routerPath) + menu.getMenuId();
        children.setPath(routerPath);
        children.setComponent(SystemConstants.INNER_LINK);
        children.setName(innerLinkName);
        children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
        childrenList.add(children);
        router.setChildren(childrenList);
      }
      routers.add(router);
    }
    return routers;
  }

  /**
   * 构建前端所需要下拉树结构
   *
   * @param menus 菜单列表
   * @return 下拉树结构列表
   */
  @Override
  public List<Tree<Long>> buildMenuTreeSelect(List<SysMenuVo> menus) {
    if (CollUtil.isEmpty(menus)) {
      return CollUtil.newArrayList();
    }
    return TreeBuildUtils.build(
        menus,
        (menu, tree) -> {
          Tree<Long> menuTree =
              tree.setId(menu.getMenuId())
                  .setParentId(menu.getParentId())
                  .setName(menu.getMenuName())
                  .setWeight(menu.getOrderNum());
          menuTree.put("menuType", menu.getMenuType());
          menuTree.put("icon", menu.getIcon());
        });
  }

  /**
   * 是否存在菜单子节点
   *
   * @param menuId 菜单ID
   * @return 结果
   */
  @Override
  public boolean hasChildByMenuId(Long menuId) {
    return baseMapper.exists(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
  }

  /**
   * 查询菜单使用数量
   *
   * @param menuId 菜单ID
   * @return 结果
   */
  @Override
  public boolean checkMenuExistRole(Long menuId) {
    return roleMenuMapper.exists(
        new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId));
  }

  /**
   * 是否存在菜单子节点
   *
   * @param menuIds 菜单ID串
   * @return 结果
   */
  @Override
  public boolean hasChildByMenuId(List<Long> menuIds) {
    return baseMapper.exists(
        new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getParentId, menuIds)
            .notIn(SysMenu::getMenuId, menuIds));
  }

  /**
   * 新增保存菜单信息
   *
   * @param bo 菜单信息
   * @return 结果
   */
  @Override
  public int insertMenu(SysMenuBo bo) {
    SysMenu menu = MapstructUtils.convert(bo, SysMenu.class);
    return baseMapper.insert(menu);
  }

  /**
   * 修改保存菜单信息
   *
   * @param bo 菜单信息
   * @return 结果
   */
  @Override
  public int updateMenu(SysMenuBo bo) {
    SysMenu menu = MapstructUtils.convert(bo, SysMenu.class);
    return baseMapper.updateById(menu);
  }

  /**
   * 删除菜单管理信息
   *
   * @param menuId 菜单ID
   * @return 结果
   */
  @Override
  public int deleteMenuById(Long menuId) {
    return baseMapper.deleteById(menuId);
  }

  /**
   * 批量删除菜单管理信息
   *
   * @param menuIds 菜单ID串
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMenuById(List<Long> menuIds) {
    baseMapper.deleteByIds(menuIds);
    roleMenuMapper.deleteByMenuIds(menuIds);
  }

  /**
   * 校验菜单名称是否唯一
   *
   * @param menu 菜单信息
   * @return 结果
   */
  @Override
  public boolean checkMenuNameUnique(SysMenuBo menu) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuName, menu.getMenuName())
                .eq(SysMenu::getParentId, menu.getParentId())
                .ne(ObjectUtil.isNotNull(menu.getMenuId()), SysMenu::getMenuId, menu.getMenuId()));
    return !exist;
  }

  /**
   * 根据父节点的ID获取所有子节点
   *
   * @param list 分类表
   * @param parentId 传入的父节点ID
   * @return String
   */
  private List<SysMenu> getChildPerms(List<SysMenu> list, Long parentId) {
    List<SysMenu> returnList = new ArrayList<>();
    for (SysMenu t : list) {
      // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
      if (t.getParentId().equals(parentId)) {
        recursionFn(list, t);
        returnList.add(t);
      }
    }
    return returnList;
  }

  /** 递归列表 */
  private void recursionFn(List<SysMenu> list, SysMenu t) {
    // 得到子节点列表
    List<SysMenu> childList = StreamUtils.filter(list, n -> n.getParentId().equals(t.getMenuId()));
    t.setChildren(childList);
    for (SysMenu tChild : childList) {
      // 判断是否有子节点
      if (list.stream().anyMatch(n -> n.getParentId().equals(tChild.getMenuId()))) {
        recursionFn(list, tChild);
      }
    }
  }
}
