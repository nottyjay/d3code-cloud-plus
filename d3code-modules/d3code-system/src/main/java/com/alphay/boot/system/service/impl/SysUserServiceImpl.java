package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.*;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.mybatis.utils.MybatisUtil;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.api.domain.param.SysUserQueryParam;
import com.alphay.boot.system.domain.*;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.vo.SysPostVo;
import com.alphay.boot.system.domain.vo.SysRoleVo;
import com.alphay.boot.system.domain.vo.SysUserExportVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.mapper.*;
import com.alphay.boot.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户 业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImplX<SysUserMapper, SysUser, SysUserVo>
    implements ISysUserService {

  @Resource private SysDeptMapper deptMapper;
  @Resource private SysRoleMapper roleMapper;
  @Resource private SysPostMapper postMapper;
  @Resource private SysUserRoleMapper userRoleMapper;
  @Resource private SysUserPostMapper userPostMapper;

  @Override
  public PageResult<SysUserVo> queryPageList(SysUserQueryParam param) {
    return listPageVo(param, this.buildQueryWrapper(param));
  }

  /**
   * 根据条件分页查询用户列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  @Override
  public List<SysUserExportVo> queryExportList(SysUserQueryParam param) {
    QueryWrapper<SysUser> wrapper =
        this.queryWrapper()
            .eq("u.del_flag", SystemConstants.NORMAL)
            .likeIfPresent("u.user_name", param.getUserName())
            .likeIfPresent("u.nick_name", param.getNickName())
            .eqIfPresent("u.status", param.getStatus())
            .likeIfPresent("u.phonenumber", param.getPhonenumber())
            .betweenIfPresent("u.create_time", param.getCreateTime())
            .and(
                ObjectUtil.isNotNull(param.getDeptId()),
                w -> {
                  List<SysDept> deptList = deptMapper.selectListByParentId(param.getDeptId());
                  List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
                  ids.add(param.getDeptId());
                  w.in("u.dept_id", ids);
                })
            .orderByAsc("u.user_id");
    return baseMapper.selectUserExportList(wrapper);
  }

  private Wrapper<SysUser> buildQueryWrapper(SysUserQueryParam param) {
    LambdaQueryWrapper<SysUser> wrapper =
        this.lambdaQueryWrapper()
            .eq(SysUser::getDelFlag, SystemConstants.NORMAL)
            .eqIfPresent(SysUser::getUserId, param.getUserId())
            .inIfPresent(
                SysUser::getUserId, StringUtils.splitTo(param.getUserIds(), Convert::toLong))
            .likeIfPresent(SysUser::getUserName, param.getUserName())
            .likeIfPresent(SysUser::getNickName, param.getNickName())
            .eqIfPresent(SysUser::getStatus, param.getStatus())
            .likeIfPresent(SysUser::getPhonenumber, param.getPhonenumber())
            .betweenIfPresent(SysUser::getCreateTime, param.getCreateTime())
            .and(
                ObjectUtil.isNotNull(param.getDeptId()),
                w -> {
                  List<SysDept> deptList = deptMapper.selectListByParentId(param.getDeptId());
                  List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
                  ids.add(param.getDeptId());
                  w.in(SysUser::getDeptId, ids);
                })
            .orderByAsc(SysUser::getUserId);
    if (StringUtils.isNotBlank(param.getExcludeUserIds())) {
      wrapper.notIn(SysUser::getUserId, StringUtils.splitList(param.getExcludeUserIds()));
    }
    return wrapper;
  }

  /**
   * 根据条件分页查询已分配用户角色列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  @Override
  public PageResult<SysUserVo> queryPageAllocatedList(SysUserQueryParam param) {
    QueryWrapper<SysUser> wrapper =
        this.queryWrapper()
            .eq("u.del_flag", SystemConstants.NORMAL)
            .eq(ObjectUtil.isNotNull(param.getRoleId()), "r.role_id", param.getRoleId())
            .like(StringUtils.isNotBlank(param.getUserName()), "u.user_name", param.getUserName())
            .eq(StringUtils.isNotBlank(param.getStatus()), "u.status", param.getStatus())
            .like(
                StringUtils.isNotBlank(param.getPhonenumber()),
                "u.phonenumber",
                param.getPhonenumber())
            .orderByAsc("u.user_id");
    Page<SysUserVo> page =
        baseMapper.selectAllocatedList(MybatisUtil.buildPage(param, param.getSortOrder()), wrapper);
    return PageResult.build(page);
  }

  /**
   * 根据条件分页查询未分配用户角色列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  @Override
  public PageResult<SysUserVo> queryPageUnallocatedList(SysUserQueryParam param) {
    List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(param.getRoleId());
    QueryWrapper<SysUser> wrapper =
        this.queryWrapper()
            .eq("u.del_flag", SystemConstants.NORMAL)
            .and(w -> w.ne("r.role_id", param.getRoleId()).or().isNull("r.role_id"))
            .notInIfPresent("u.user_id", userIds)
            .likeIfPresent("u.user_name", param.getUserName())
            .likeIfPresent("u.phonenumber", param.getPhonenumber())
            .orderByAsc("u.user_id");
    Page<SysUserVo> page =
        baseMapper.selectUnallocatedList(
            MybatisUtil.buildPage(param, param.getSortOrder()), wrapper);
    return PageResult.build(page);
  }

  /**
   * 通过用户名查询用户
   *
   * @param userName 用户名
   * @return 用户对象信息
   */
  @Override
  public SysUserVo selectUserByUserName(String userName) {
    return baseMapper.selectOne(
        this.lambdaQueryWrapper().eq(SysUser::getUserName, userName), getVoClass());
  }

  /**
   * 通过手机号查询用户
   *
   * @param phonenumber 手机号
   * @return 用户对象信息
   */
  @Override
  public SysUserVo selectUserByPhonenumber(String phonenumber) {
    return baseMapper.selectOne(
        new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhonenumber, phonenumber), getVoClass());
  }

  /**
   * 通过用户ID查询用户
   *
   * @param userId 用户ID
   * @return 用户对象信息
   */
  @Override
  public SysUserVo getVoById(Serializable userId) {
    SysUserVo user = super.getVoById(userId);
    if (ObjectUtil.isNull(user)) {
      return user;
    }
    user.setRoles(roleMapper.selectRolesByUserId(user.getUserId()));
    return user;
  }

  /**
   * 通过用户ID串查询用户
   *
   * @param userIds 用户ID串
   * @param deptId 部门id
   * @return 用户列表信息
   */
  @Override
  public List<SysUserVo> queryListByIds(List<Long> userIds, Long deptId) {
    return baseMapper.selectUserList(
        new LambdaQueryWrapper<SysUser>()
            .select(
                SysUser::getUserId,
                SysUser::getUserName,
                SysUser::getNickName,
                SysUser::getEmail,
                SysUser::getPhonenumber)
            .eq(SysUser::getStatus, SystemConstants.NORMAL)
            .eq(ObjectUtil.isNotNull(deptId), SysUser::getDeptId, deptId)
            .in(CollUtil.isNotEmpty(userIds), SysUser::getUserId, userIds));
  }

  /**
   * 查询用户所属角色组
   *
   * @param userId 用户ID
   * @return 结果
   */
  @Override
  public String selectUserRoleGroup(Long userId) {
    List<SysRoleVo> list = roleMapper.selectRolesByUserId(userId);
    if (CollUtil.isEmpty(list)) {
      return StringUtils.EMPTY;
    }
    return StreamUtils.join(list, SysRoleVo::getRoleName);
  }

  /**
   * 查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 结果
   */
  @Override
  public String selectUserPostGroup(Long userId) {
    List<SysPostVo> list = postMapper.selectPostsByUserId(userId);
    if (CollUtil.isEmpty(list)) {
      return StringUtils.EMPTY;
    }
    return StreamUtils.join(list, SysPostVo::getPostName);
  }

  /**
   * 校验用户名称是否唯一
   *
   * @param param 用户信息
   * @return 结果
   */
  @Override
  public boolean checkUserNameUnique(SysUserQueryParam param) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, param.getUserName())
                .ne(
                    ObjectUtil.isNotNull(param.getUserId()),
                    SysUser::getUserId,
                    param.getUserId()));
    return !exist;
  }

  /**
   * 校验手机号码是否唯一
   *
   * @param param 用户信息
   */
  @Override
  public boolean checkPhoneUnique(SysUserQueryParam param) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhonenumber, param.getPhonenumber())
                .ne(
                    ObjectUtil.isNotNull(param.getUserId()),
                    SysUser::getUserId,
                    param.getUserId()));
    return !exist;
  }

  /**
   * 校验email是否唯一
   *
   * @param param 用户信息
   */
  @Override
  public boolean checkEmailUnique(SysUserQueryParam param) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, param.getEmail())
                .ne(
                    ObjectUtil.isNotNull(param.getUserId()),
                    SysUser::getUserId,
                    param.getUserId()));
    return !exist;
  }

  /**
   * 校验用户是否允许操作
   *
   * @param userId 用户ID
   */
  @Override
  public void checkUserAllowed(Long userId) {
    if (ObjectUtil.isNotNull(userId) && LoginHelper.isSuperAdmin(userId)) {
      throw new ServiceException("不允许操作超级管理员用户");
    }
  }

  /**
   * 校验用户是否有数据权限
   *
   * @param userId 用户id
   */
  @Override
  public void checkUserDataScope(Long userId) {
    if (ObjectUtil.isNull(userId)) {
      return;
    }
    if (LoginHelper.isSuperAdmin()) {
      return;
    }
    if (baseMapper.countUserById(userId) == 0) {
      throw new ServiceException("没有权限访问用户数据！");
    }
  }

  /**
   * 新增保存用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertUser(SysUserBo user) {
    SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
    // 新增用户信息
    int rows = baseMapper.insert(sysUser);
    user.setUserId(sysUser.getUserId());
    // 新增用户岗位关联
    insertUserPost(user, false);
    // 新增用户与角色管理
    insertUserRole(user, false);
    return rows;
  }

  /**
   * 注册用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public boolean registerUser(SysUserBo user, String tenantId) {
    user.setCreateBy(0L);
    user.setUpdateBy(0L);
    SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
    sysUser.setTenantId(tenantId);
    return baseMapper.insert(sysUser) > 0;
  }

  /**
   * 修改保存用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  @CacheEvict(cacheNames = CacheNames.SYS_NICKNAME, key = "#user.userId")
  @Transactional(rollbackFor = Exception.class)
  public int updateUser(SysUserBo user) {
    // 新增用户与角色管理
    insertUserRole(user, true);
    // 新增用户与岗位管理
    insertUserPost(user, true);
    SysUser sysUser = MapstructUtils.convert(user, SysUser.class);
    // 防止错误更新后导致的数据误删除
    int flag = baseMapper.updateById(sysUser);
    if (flag < 1) {
      throw new ServiceException("修改用户" + user.getUserName() + "信息失败");
    }
    return flag;
  }

  /**
   * 用户授权角色
   *
   * @param userId 用户ID
   * @param roleIds 角色组
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void insertUserAuth(Long userId, Long[] roleIds) {
    insertUserRole(userId, roleIds, true);
  }

  /**
   * 修改用户状态
   *
   * @param userId 用户ID
   * @param status 帐号状态
   * @return 结果
   */
  @Override
  public int updateUserStatus(Long userId, String status) {
    return baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysUser>()
            .set(SysUser::getStatus, status)
            .eq(SysUser::getUserId, userId));
  }

  /**
   * 修改用户基本信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_NICKNAME, key = "#user.userId")
  @Override
  public int updateUserProfile(SysUserBo user) {
    return baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysUser>()
            .set(ObjectUtil.isNotNull(user.getNickName()), SysUser::getNickName, user.getNickName())
            .set(SysUser::getPhonenumber, user.getPhonenumber())
            .set(SysUser::getEmail, user.getEmail())
            .set(SysUser::getSex, user.getSex())
            .eq(SysUser::getUserId, user.getUserId()));
  }

  /**
   * 修改用户头像
   *
   * @param userId 用户ID
   * @param avatar 头像地址
   * @return 结果
   */
  @Override
  public boolean updateUserAvatar(Long userId, Long avatar) {
    return baseMapper.update(
            null,
            new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getAvatar, avatar)
                .eq(SysUser::getUserId, userId))
        > 0;
  }

  /**
   * 重置用户密码
   *
   * @param userId 用户ID
   * @param password 密码
   * @return 结果
   */
  @Override
  public int resetUserPwd(Long userId, String password) {
    return baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysUser>()
            .set(SysUser::getPassword, password)
            .eq(SysUser::getUserId, userId));
  }

  /**
   * 新增用户角色信息
   *
   * @param user 用户对象
   * @param clear 清除已存在的关联数据
   */
  private void insertUserRole(SysUserBo user, boolean clear) {
    this.insertUserRole(user.getUserId(), user.getRoleIds(), clear);
  }

  /**
   * 新增用户岗位信息
   *
   * @param user 用户对象
   * @param clear 清除已存在的关联数据
   */
  private void insertUserPost(SysUserBo user, boolean clear) {
    Long[] posts = user.getPostIds();
    if (ArrayUtil.isNotEmpty(posts)) {
      if (clear) {
        // 删除用户与岗位关联
        userPostMapper.delete(
            new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, user.getUserId()));
      }
      // 新增用户与岗位管理
      List<SysUserPost> list =
          StreamUtils.toList(
              List.of(posts),
              postId -> {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                return up;
              });
      userPostMapper.insertBatch(list);
    }
  }

  /**
   * 新增用户角色信息
   *
   * @param userId 用户ID
   * @param roleIds 角色组
   * @param clear 清除已存在的关联数据
   */
  private void insertUserRole(Long userId, Long[] roleIds, boolean clear) {
    if (ArrayUtil.isNotEmpty(roleIds)) {
      List<Long> roleList = new ArrayList<>(List.of(roleIds));
      if (!LoginHelper.isSuperAdmin(userId)) {
        roleList.remove(SystemConstants.SUPER_ADMIN_ID);
      }
      // 判断是否具有此角色的操作权限
      List<SysRoleVo> roles =
          roleMapper.selectRoleList(new QueryWrapper<SysRole>().in("r.role_id", roleList));
      if (CollUtil.isEmpty(roles)) {
        throw new ServiceException("没有权限访问角色的数据");
      }
      if (clear) {
        // 删除用户与角色关联
        userRoleMapper.delete(
            new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
      }
      // 新增用户与角色管理
      List<SysUserRole> list =
          StreamUtils.toList(
              roleList,
              roleId -> {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                return ur;
              });
      userRoleMapper.insertBatch(list);
    }
  }

  /**
   * 通过用户ID删除用户
   *
   * @param userId 用户ID
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteUserById(Long userId) {
    // 删除用户与角色关联
    userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    // 删除用户与岗位表
    userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, userId));
    // 防止更新失败导致的数据删除
    int flag = baseMapper.deleteById(userId);
    if (flag < 1) {
      throw new ServiceException("删除用户失败!");
    }
    return flag;
  }

  /**
   * 批量删除用户信息
   *
   * @param userIds 需要删除的用户ID
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteUserByIds(Long[] userIds) {
    for (Long userId : userIds) {
      checkUserAllowed(userId);
      checkUserDataScope(userId);
    }
    List<Long> ids = List.of(userIds);
    // 删除用户与角色关联
    userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, ids));
    // 删除用户与岗位表
    userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().in(SysUserPost::getUserId, ids));
    // 防止更新失败导致的数据删除
    int flag = baseMapper.deleteByIds(ids);
    if (flag < 1) {
      throw new ServiceException("删除用户失败!");
    }
    return flag;
  }

  /**
   * 通过部门id查询当前部门所有用户
   *
   * @param deptId 部门ID
   * @return 用户信息集合信息
   */
  @Override
  public List<SysUserVo> selectUserListByDept(Long deptId) {
    LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
    lqw.eq(SysUser::getDeptId, deptId);
    lqw.orderByAsc(SysUser::getUserId);
    return baseMapper.selectList(lqw, getVoClass());
  }

  @Override
  public List<Long> selectUserIdsByRoleIds(List<Long> roleIds) {
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds));
    return StreamUtils.toList(userRoles, SysUserRole::getUserId);
  }

  /**
   * 通过用户ID查询用户账户
   *
   * @param userId 用户ID
   * @return 用户账户
   */
  @Cacheable(cacheNames = CacheNames.SYS_USER_NAME, key = "#userId")
  @Override
  public String selectUserNameById(Long userId) {
    SysUser sysUser =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getUserName)
                .eq(SysUser::getUserId, userId));
    return ObjectUtils.notNullGetter(sysUser, SysUser::getUserName);
  }

  /**
   * 通过用户ID查询用户昵称
   *
   * @param userId 用户ID
   * @return 用户昵称
   */
  @Override
  @Cacheable(cacheNames = CacheNames.SYS_NICKNAME, key = "#userId")
  public String selectNicknameById(Long userId) {
    SysUser sysUser =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getNickName)
                .eq(SysUser::getUserId, userId));
    return ObjectUtils.notNullGetter(sysUser, SysUser::getNickName);
  }

  @Override
  public String selectNicknameByIds(String userIds) {
    List<String> list = new ArrayList<>();
    for (Long id : StringUtils.splitTo(userIds, Convert::toLong)) {
      String nickname = SpringUtils.getAopProxy(this).selectNicknameById(id);
      if (StringUtils.isNotBlank(nickname)) {
        list.add(nickname);
      }
    }
    return String.join(StringUtils.SEPARATOR, list);
  }

  /**
   * 通过用户ID查询用户手机号
   *
   * @param userId 用户id
   * @return 用户手机号
   */
  @Override
  public String selectPhonenumberById(Long userId) {
    SysUser sysUser =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getPhonenumber)
                .eq(SysUser::getUserId, userId));
    return ObjectUtils.notNullGetter(sysUser, SysUser::getPhonenumber);
  }

  /**
   * 通过用户ID查询用户邮箱
   *
   * @param userId 用户id
   * @return 用户邮箱
   */
  @Override
  public String selectEmailById(Long userId) {
    SysUser sysUser =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getEmail)
                .eq(SysUser::getUserId, userId));
    return ObjectUtils.notNullGetter(sysUser, SysUser::getEmail);
  }

  @Override
  public SysUserVo selectUserByEmail(String email) {
    return this.getOneVo(SysUser::getEmail, email);
  }
}
