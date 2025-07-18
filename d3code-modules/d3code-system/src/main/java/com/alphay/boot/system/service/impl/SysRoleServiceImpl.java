package com.alphay.boot.system.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.api.domain.param.SysRoleQueryParam;
import com.alphay.boot.system.api.model.LoginUser;
import com.alphay.boot.system.domain.SysRole;
import com.alphay.boot.system.domain.SysRoleDept;
import com.alphay.boot.system.domain.SysRoleMenu;
import com.alphay.boot.system.domain.SysUserRole;
import com.alphay.boot.system.domain.bo.SysRoleBo;
import com.alphay.boot.system.domain.vo.SysRoleVo;
import com.alphay.boot.system.mapper.SysRoleDeptMapper;
import com.alphay.boot.system.mapper.SysRoleMapper;
import com.alphay.boot.system.mapper.SysRoleMenuMapper;
import com.alphay.boot.system.mapper.SysUserRoleMapper;
import com.alphay.boot.system.service.ISysRoleService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import java.util.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色 业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImplX<SysRoleMapper, SysRole, SysRoleVo>
    implements ISysRoleService {

  @Resource private SysRoleMenuMapper roleMenuMapper;
  @Resource private SysUserRoleMapper userRoleMapper;
  @Resource private SysRoleDeptMapper roleDeptMapper;

  @Override
  public PageResult<SysRoleVo> queryPageList(SysRoleQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 根据条件分页查询角色数据
   *
   * @param param 角色信息
   * @return 角色数据集合信息
   */
  @Override
  public List<SysRoleVo> queryList(SysRoleQueryParam param) {
    return baseMapper.selectRoleList(this.buildQueryWrapper(param));
  }

  private Wrapper<SysRole> buildQueryWrapper(SysRoleQueryParam param) {
    QueryWrapper<SysRole> wrapper =
        this.queryWrapper()
            .eq("r.del_flag", SystemConstants.NORMAL)
            .eqIfPresent("r.role_id", param.getRoleId())
            .likeIfPresent("r.role_name", param.getRoleName())
            .eqIfPresent("r.status", param.getStatus())
            .likeIfPresent("r.role_key", param.getRoleKey())
            .betweenIfPresent("r.create_time", param.getCreateTime())
            .orderByAsc("r.role_sort")
            .orderByAsc("r.create_time");
    return wrapper;
  }

  /**
   * 根据用户ID查询角色
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  @Override
  public List<SysRoleVo> queryListByUserId(Long userId) {
    return baseMapper.selectRolesByUserId(userId);
  }

  /**
   * 根据用户ID查询角色列表(包含被授权状态)
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  @Override
  public List<SysRoleVo> queryListAuthByUserId(Long userId) {
    List<SysRoleVo> userRoles = baseMapper.selectRolesByUserId(userId);
    List<SysRoleVo> roles = queryAll();
    // 使用HashSet提高查找效率
    Set<Long> userRoleIds = StreamUtils.toSet(userRoles, SysRoleVo::getRoleId);
    for (SysRoleVo role : roles) {
      if (userRoleIds.contains(role.getRoleId())) {
        role.setFlag(true);
      }
    }
    return roles;
  }

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return 权限列表
   */
  @Override
  public Set<String> fetchPermissionByUserId(Long userId) {
    List<SysRoleVo> perms = baseMapper.selectRolesByUserId(userId);
    Set<String> permsSet = new HashSet<>();
    for (SysRoleVo perm : perms) {
      if (ObjectUtil.isNotNull(perm)) {
        permsSet.addAll(StringUtils.splitList(perm.getRoleKey().trim()));
      }
    }
    return permsSet;
  }

  /**
   * 查询所有角色
   *
   * @return 角色列表
   */
  @Override
  public List<SysRoleVo> queryAll() {
    return this.queryList(new SysRoleQueryParam());
  }

  /**
   * 根据用户ID获取角色选择框列表
   *
   * @param userId 用户ID
   * @return 选中角色ID列表
   */
  @Override
  public List<Long> fetchListByUserId(Long userId) {
    List<SysRoleVo> list = baseMapper.selectRolesByUserId(userId);
    return StreamUtils.toList(list, SysRoleVo::getRoleId);
  }

  /**
   * 通过角色ID串查询角色
   *
   * @param roleIds 角色ID串
   * @return 角色列表信息
   */
  @Override
  public List<SysRoleVo> queryListByIds(List<Long> roleIds) {
    return baseMapper.selectRoleList(
        new QueryWrapper<SysRole>()
            .eq("r.status", SystemConstants.NORMAL)
            .in(CollUtil.isNotEmpty(roleIds), "r.role_id", roleIds));
  }

  /**
   * 校验角色名称是否唯一
   *
   * @param role 角色信息
   * @return 结果
   */
  @Override
  public boolean checkRoleNameUnique(SysRoleQueryParam role) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
    return !exist;
  }

  /**
   * 校验角色权限是否唯一
   *
   * @param role 角色信息
   * @return 结果
   */
  @Override
  public boolean checkRoleKeyUnique(SysRoleQueryParam role) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, role.getRoleKey())
                .ne(ObjectUtil.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId()));
    return !exist;
  }

  /**
   * 校验角色是否允许操作
   *
   * @param role 角色信息
   */
  @Override
  public void checkRoleAllowed(SysRoleQueryParam role) {
    if (ObjectUtil.isNotNull(role.getRoleId()) && LoginHelper.isSuperAdmin(role.getRoleId())) {
      throw new ServiceException("不允许操作超级管理员角色");
    }
    String[] keys =
        new String[] {TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY};
    // 新增不允许使用 管理员标识符
    if (ObjectUtil.isNull(role.getRoleId()) && StringUtils.equalsAny(role.getRoleKey(), keys)) {
      throw new ServiceException("不允许使用系统内置管理员角色标识符!");
    }
    // 修改不允许修改 管理员标识符
    if (ObjectUtil.isNotNull(role.getRoleId())) {
      SysRole sysRole = baseMapper.selectById(role.getRoleId());
      // 如果标识符不相等 判断为修改了管理员标识符
      if (!StringUtils.equals(sysRole.getRoleKey(), role.getRoleKey())) {
        if (StringUtils.equalsAny(sysRole.getRoleKey(), keys)) {
          throw new ServiceException("不允许修改系统内置管理员角色标识符!");
        } else if (StringUtils.equalsAny(role.getRoleKey(), keys)) {
          throw new ServiceException("不允许使用系统内置管理员角色标识符!");
        }
      }
    }
  }

  /**
   * 校验角色是否有数据权限
   *
   * @param roleId 角色id
   */
  @Override
  public void checkRoleDataScope(Long roleId) {
    if (ObjectUtil.isNull(roleId)) {
      return;
    }
    if (LoginHelper.isSuperAdmin()) {
      return;
    }
    List<SysRoleVo> roles = this.queryList(new SysRoleQueryParam().setRoleId(roleId));
    if (CollUtil.isEmpty(roles)) {
      throw new ServiceException("没有权限访问角色数据！");
    }
  }

  /**
   * 通过角色ID查询角色使用数量
   *
   * @param roleId 角色ID
   * @return 结果
   */
  @Override
  public long countUserRoleByRoleId(Long roleId) {
    return userRoleMapper.selectCount(
        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
  }

  /**
   * 新增保存角色信息
   *
   * @param bo 角色信息
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertRole(SysRoleBo bo) {
    SysRole role = MapstructUtils.convert(bo, SysRole.class);
    // 新增角色信息
    baseMapper.insert(role);
    bo.setRoleId(role.getRoleId());
    return insertRoleMenu(bo);
  }

  /**
   * 修改保存角色信息
   *
   * @param bo 角色信息
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateRole(SysRoleBo bo) {
    SysRole role = MapstructUtils.convert(bo, SysRole.class);

    if (SystemConstants.DISABLE.equals(role.getStatus())
        && this.countUserRoleByRoleId(role.getRoleId()) > 0) {
      throw new ServiceException("角色已分配，不能禁用!");
    }
    // 修改角色信息
    baseMapper.updateById(role);
    // 删除角色与菜单关联
    roleMenuMapper.delete(
        new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getRoleId()));
    return insertRoleMenu(bo);
  }

  /**
   * 修改角色状态
   *
   * @param roleId 角色ID
   * @param status 角色状态
   * @return 结果
   */
  @Override
  public int updateRoleStatus(Long roleId, String status) {
    if (SystemConstants.DISABLE.equals(status) && this.countUserRoleByRoleId(roleId) > 0) {
      throw new ServiceException("角色已分配，不能禁用!");
    }
    return baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysRole>()
            .set(SysRole::getStatus, status)
            .eq(SysRole::getRoleId, roleId));
  }

  /**
   * 修改数据权限信息
   *
   * @param bo 角色信息
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, key = "#bo.roleId")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int authDataScope(SysRoleBo bo) {
    SysRole role = MapstructUtils.convert(bo, SysRole.class);
    // 修改角色信息
    baseMapper.updateById(role);
    // 删除角色与部门关联
    roleDeptMapper.delete(
        new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, role.getRoleId()));
    // 新增角色和部门信息（数据权限）
    return insertRoleDept(bo);
  }

  /**
   * 新增角色菜单信息
   *
   * @param role 角色对象
   */
  private int insertRoleMenu(SysRoleBo role) {
    int rows = 1;
    // 新增用户与角色管理
    List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
    for (Long menuId : role.getMenuIds()) {
      SysRoleMenu rm = new SysRoleMenu();
      rm.setRoleId(role.getRoleId());
      rm.setMenuId(menuId);
      list.add(rm);
    }
    if (list.size() > 0) {
      rows = roleMenuMapper.insertBatch(list) ? list.size() : 0;
    }
    return rows;
  }

  /**
   * 新增角色部门信息(数据权限)
   *
   * @param role 角色对象
   */
  private int insertRoleDept(SysRoleBo role) {
    int rows = 1;
    // 新增角色与部门（数据权限）管理
    List<SysRoleDept> list = new ArrayList<SysRoleDept>();
    for (Long deptId : role.getDeptIds()) {
      SysRoleDept rd = new SysRoleDept();
      rd.setRoleId(role.getRoleId());
      rd.setDeptId(deptId);
      list.add(rd);
    }
    if (list.size() > 0) {
      rows = roleDeptMapper.insertBatch(list) ? list.size() : 0;
    }
    return rows;
  }

  /**
   * 通过角色ID删除角色
   *
   * @param roleId 角色ID
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, key = "#roleId")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteRoleById(Long roleId) {
    // 删除角色与菜单关联
    roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
    // 删除角色与部门关联
    roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
    return baseMapper.deleteById(roleId);
  }

  /**
   * 批量删除角色信息
   *
   * @param roleIds 需要删除的角色ID
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_ROLE_CUSTOM, allEntries = true)
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteRoleByIds(Long[] roleIds) {
    for (Long roleId : roleIds) {
      SysRole role = baseMapper.selectById(roleId);
      checkRoleAllowed(BeanUtil.toBean(role, SysRoleQueryParam.class));
      checkRoleDataScope(roleId);
      if (countUserRoleByRoleId(roleId) > 0) {
        throw new ServiceException(String.format("%1$s已分配，不能删除!", role.getRoleName()));
      }
    }
    List<Long> ids = Arrays.asList(roleIds);
    // 删除角色与菜单关联
    roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, ids));
    // 删除角色与部门关联
    roleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().in(SysRoleDept::getRoleId, ids));
    return baseMapper.deleteByIds(ids);
  }

  /**
   * 取消授权用户角色
   *
   * @param userRole 用户和角色关联信息
   * @return 结果
   */
  @Override
  public int deleteAuthUser(SysUserRole userRole) {
    int rows =
        userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, userRole.getRoleId())
                .eq(SysUserRole::getUserId, userRole.getUserId()));
    if (rows > 0) {
      cleanOnlineUser(List.of(userRole.getUserId()));
    }
    return rows;
  }

  /**
   * 批量取消授权用户角色
   *
   * @param roleId 角色ID
   * @param userIds 需要取消授权的用户数据ID
   * @return 结果
   */
  @Override
  public int deleteAuthUsers(Long roleId, Long[] userIds) {
    List<Long> ids = List.of(userIds);
    int rows =
        userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, ids));
    if (rows > 0) {
      cleanOnlineUser(ids);
    }
    return rows;
  }

  /**
   * 批量选择授权用户角色
   *
   * @param roleId 角色ID
   * @param userIds 需要授权的用户数据ID
   * @return 结果
   */
  @Override
  public int insertAuthUsers(Long roleId, Long[] userIds) {
    // 新增用户与角色管理
    int rows = 1;
    List<Long> ids = List.of(userIds);
    List<SysUserRole> list =
        StreamUtils.toList(
            ids,
            userId -> {
              SysUserRole ur = new SysUserRole();
              ur.setUserId(userId);
              ur.setRoleId(roleId);
              return ur;
            });
    if (CollUtil.isNotEmpty(list)) {
      rows = userRoleMapper.insertBatch(list) ? list.size() : 0;
    }
    if (rows > 0) {
      cleanOnlineUser(ids);
    }
    return rows;
  }

  @Override
  public void cleanOnlineUserByRole(Long roleId) {
    // 如果角色未绑定用户 直接返回
    Long num =
        userRoleMapper.selectCount(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId));
    if (num == 0) {
      return;
    }
    List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
    if (CollUtil.isEmpty(keys)) {
      return;
    }
    // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
    keys.parallelStream()
        .forEach(
            key -> {
              String token = StringUtils.substringAfterLast(key, ":");
              // 如果已经过期则跳过
              if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return;
              }
              LoginUser loginUser = LoginHelper.getLoginUser(token);
              if (ObjectUtil.isNull(loginUser) || CollUtil.isEmpty(loginUser.getRoles())) {
                return;
              }
              if (loginUser.getRoles().stream().anyMatch(r -> r.getRoleId().equals(roleId))) {
                try {
                  StpUtil.logoutByTokenValue(token);
                } catch (NotLoginException ignored) {
                }
              }
            });
  }

  @Override
  public void cleanOnlineUser(List<Long> userIds) {
    List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
    if (CollUtil.isEmpty(keys)) {
      return;
    }
    // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
    keys.parallelStream()
        .forEach(
            key -> {
              String token = StringUtils.substringAfterLast(key, ":");
              // 如果已经过期则跳过
              if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return;
              }
              LoginUser loginUser = LoginHelper.getLoginUser(token);
              if (ObjectUtil.isNull(loginUser)) {
                return;
              }
              if (userIds.contains(loginUser.getUserId())) {
                try {
                  StpUtil.logoutByTokenValue(token);
                } catch (NotLoginException ignored) {
                }
              }
            });
  }
}
