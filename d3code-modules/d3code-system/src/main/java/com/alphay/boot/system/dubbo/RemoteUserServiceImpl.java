package com.alphay.boot.system.dubbo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.enums.UserStatus;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.exception.user.UserException;
import com.alphay.boot.common.core.utils.DateUtils;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.helper.DataPermissionHelper;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.system.api.RemoteUserService;
import com.alphay.boot.system.api.domain.bo.RemoteUserBo;
import com.alphay.boot.system.api.domain.vo.RemoteUserVo;
import com.alphay.boot.system.api.model.LoginUser;
import com.alphay.boot.system.api.model.PostDTO;
import com.alphay.boot.system.api.model.RoleDTO;
import com.alphay.boot.system.api.model.XcxLoginUser;
import com.alphay.boot.system.domain.*;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.vo.SysDeptVo;
import com.alphay.boot.system.domain.vo.SysPostVo;
import com.alphay.boot.system.domain.vo.SysRoleVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.mapper.*;
import com.alphay.boot.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteUserServiceImpl implements RemoteUserService {

  private final ISysUserService userService;
  private final ISysPermissionService permissionService;
  private final ISysConfigService configService;
  private final ISysRoleService roleService;
  private final ISysDeptService deptService;
  private final ISysPostService postService;
  private SysUserPostMapper userPostMapper;
  private SysUserRoleMapper userRoleMapper;

  /**
   * 通过用户名查询用户信息
   *
   * @param username 用户名
   * @param tenantId 租户id
   * @return 结果
   */
  @Override
  public LoginUser getUserInfo(String username, String tenantId) throws UserException {
    return TenantHelper.dynamic(
        tenantId,
        () -> {
          SysUserVo sysUser = userService.selectUserByUserName(username);
          if (ObjectUtil.isNull(sysUser)) {
            throw new UserException("user.not.exists", username);
          }
          if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
            throw new UserException("user.blocked", username);
          }
          // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
          // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
          return buildLoginUser(sysUser);
        });
  }

  /**
   * 通过用户id查询用户信息
   *
   * @param userId 用户id
   * @param tenantId 租户id
   * @return 结果
   */
  @Override
  public LoginUser getUserInfo(Long userId, String tenantId) throws UserException {
    return TenantHelper.dynamic(
        tenantId,
        () -> {
          SysUserVo sysUser = userService.getVoById(userId);
          if (ObjectUtil.isNull(sysUser)) {
            throw new UserException("user.not.exists", "");
          }
          if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
            throw new UserException("user.blocked", sysUser.getUserName());
          }
          // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
          // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
          return buildLoginUser(sysUser);
        });
  }

  /**
   * 通过手机号查询用户信息
   *
   * @param phonenumber 手机号
   * @param tenantId 租户id
   * @return 结果
   */
  @Override
  public LoginUser getUserInfoByPhonenumber(String phonenumber, String tenantId)
      throws UserException {
    return TenantHelper.dynamic(
        tenantId,
        () -> {
          SysUserVo sysUser = userService.selectUserByPhonenumber(phonenumber);
          if (ObjectUtil.isNull(sysUser)) {
            throw new UserException("user.not.exists", phonenumber);
          }
          if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
            throw new UserException("user.blocked", phonenumber);
          }
          // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
          // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
          return buildLoginUser(sysUser);
        });
  }

  /**
   * 通过邮箱查询用户信息
   *
   * @param email 邮箱
   * @param tenantId 租户id
   * @return 结果
   */
  @Override
  public LoginUser getUserInfoByEmail(String email, String tenantId) throws UserException {
    return TenantHelper.dynamic(
        tenantId,
        () -> {
          SysUserVo user = userService.selectUserByEmail(email);
          if (ObjectUtil.isNull(user)) {
            throw new UserException("user.not.exists", email);
          }
          if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            throw new UserException("user.blocked", email);
          }
          // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
          // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
          return buildLoginUser(user);
        });
  }

  /**
   * 通过openid查询用户信息
   *
   * @param openid openid
   * @return 结果
   */
  @Override
  public XcxLoginUser getUserInfoByOpenid(String openid) throws UserException {
    // todo 自行实现 userService.selectUserByOpenid(openid);
    SysUser sysUser = new SysUser();
    if (ObjectUtil.isNull(sysUser)) {
      // todo 用户不存在 业务逻辑自行实现
    }
    if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
      // todo 用户已被停用 业务逻辑自行实现
    }
    // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
    // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
    XcxLoginUser loginUser = new XcxLoginUser();
    loginUser.setUserId(sysUser.getUserId());
    loginUser.setUsername(sysUser.getUserName());
    loginUser.setNickname(sysUser.getNickName());
    loginUser.setUserType(sysUser.getUserType());
    loginUser.setOpenid(openid);
    return loginUser;
  }

  /**
   * 注册用户信息
   *
   * @param remoteUserBo 用户信息
   * @return 结果
   */
  @Override
  public Boolean registerUserInfo(RemoteUserBo remoteUserBo)
      throws UserException, ServiceException {
    SysUserBo sysUserBo = MapstructUtils.convert(remoteUserBo, SysUserBo.class);
    String username = sysUserBo.getUserName();
    boolean exist =
        TenantHelper.dynamic(
            remoteUserBo.getTenantId(),
            () -> {
              if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
                throw new ServiceException("当前系统没有开启注册功能");
              }
              return userService.exists(
                  new LambdaQueryWrapper<SysUser>()
                      .eq(SysUser::getUserName, sysUserBo.getUserName()));
            });
    if (exist) {
      throw new UserException("user.register.save.error", username);
    }
    return userService.registerUser(sysUserBo, remoteUserBo.getTenantId());
  }

  /**
   * 通过用户ID查询用户账户
   *
   * @param userId 用户ID
   * @return 用户账户
   */
  @Override
  public String selectUserNameById(Long userId) {
    return userService.selectUserNameById(userId);
  }

  /**
   * 通过用户ID查询用户昵称
   *
   * @param userId 用户ID
   * @return 用户昵称
   */
  @Override
  public String selectNicknameById(Long userId) {
    return userService.selectNicknameById(userId);
  }

  /**
   * 通过用户ID查询用户账户
   *
   * @param userIds 用户ID 多个用逗号隔开
   * @return 用户账户
   */
  @Override
  public String selectNicknameByIds(String userIds) {
    return userService.selectNicknameByIds(userIds);
  }

  /**
   * 通过用户ID查询用户手机号
   *
   * @param userId 用户id
   * @return 用户手机号
   */
  @Override
  public String selectPhonenumberById(Long userId) {
    return userService.selectPhonenumberById(userId);
  }

  /**
   * 通过用户ID查询用户邮箱
   *
   * @param userId 用户id
   * @return 用户邮箱
   */
  @Override
  public String selectEmailById(Long userId) {
    return userService.selectEmailById(userId);
  }

  /** 构建登录用户 */
  private LoginUser buildLoginUser(SysUserVo userVo) {
    LoginUser loginUser = new LoginUser();
    Long userId = userVo.getUserId();
    loginUser.setTenantId(userVo.getTenantId());
    loginUser.setUserId(userId);
    loginUser.setDeptId(userVo.getDeptId());
    loginUser.setUsername(userVo.getUserName());
    loginUser.setNickname(userVo.getNickName());
    loginUser.setPassword(userVo.getPassword());
    loginUser.setUserType(userVo.getUserType());
    loginUser.setMenuPermission(permissionService.getMenuPermission(userId));
    loginUser.setRolePermission(permissionService.getRolePermission(userId));
    if (ObjectUtil.isNotNull(userVo.getDeptId())) {
      Opt<SysDeptVo> deptOpt = Opt.of(userVo.getDeptId()).map(deptService::getVoById);
      loginUser.setDeptName(deptOpt.map(SysDeptVo::getDeptName).orElse(StringUtils.EMPTY));
      loginUser.setDeptCategory(deptOpt.map(SysDeptVo::getDeptCategory).orElse(StringUtils.EMPTY));
    }
    List<SysRoleVo> roles = roleService.queryListByUserId(userId);
    List<SysPostVo> posts = postService.queryListByUserId(userId);
    loginUser.setRoles(BeanUtil.copyToList(roles, RoleDTO.class));
    loginUser.setPosts(BeanUtil.copyToList(posts, PostDTO.class));
    return loginUser;
  }

  /**
   * 更新用户信息
   *
   * @param userId 用户ID
   * @param ip IP地址
   */
  @Override
  public void recordLoginInfo(Long userId, String ip) {
    SysUser sysUser = new SysUser();
    sysUser.setUserId(userId);
    sysUser.setLoginIp(ip);
    sysUser.setLoginDate(DateUtils.getNowDate());
    sysUser.setUpdateBy(userId);
    DataPermissionHelper.ignore(() -> userService.updateById(sysUser));
  }

  /**
   * 通过用户ID查询用户列表
   *
   * @param userIds 用户ids
   * @return 用户列表
   */
  @Override
  public List<RemoteUserVo> selectListByIds(List<Long> userIds) {
    if (CollUtil.isEmpty(userIds)) {
      return new ArrayList<>();
    }
    List<SysUserVo> list =
        userService.listVo(
            new LambdaQueryWrapper<SysUser>()
                .select(
                    SysUser::getUserId,
                    SysUser::getDeptId,
                    SysUser::getUserName,
                    SysUser::getNickName,
                    SysUser::getUserType,
                    SysUser::getEmail,
                    SysUser::getPhonenumber,
                    SysUser::getSex,
                    SysUser::getStatus,
                    SysUser::getCreateTime)
                .eq(SysUser::getStatus, SystemConstants.NORMAL)
                .in(SysUser::getUserId, userIds));
    return MapstructUtils.convert(list, RemoteUserVo.class);
  }

  /**
   * 通过角色ID查询用户ID
   *
   * @param roleIds 角色ids
   * @return 用户ids
   */
  @Override
  public List<Long> selectUserIdsByRoleIds(List<Long> roleIds) {
    if (CollUtil.isEmpty(roleIds)) {
      return new ArrayList<>();
    }
    return userService.selectUserIdsByRoleIds(roleIds);
  }

  /**
   * 通过角色ID查询用户
   *
   * @param roleIds 角色ids
   * @return 用户
   */
  @Override
  public List<RemoteUserVo> selectUsersByRoleIds(List<Long> roleIds) {
    if (CollUtil.isEmpty(roleIds)) {
      return List.of();
    }

    // 通过角色ID获取用户角色信息
    List<SysUserRole> userRoles =
        userRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, roleIds));

    // 获取用户ID列表
    Set<Long> userIds = StreamUtils.toSet(userRoles, SysUserRole::getUserId);

    return selectListByIds(new ArrayList<>(userIds));
  }

  /**
   * 通过部门ID查询用户
   *
   * @param deptIds 部门ids
   * @return 用户
   */
  @Override
  public List<RemoteUserVo> selectUsersByDeptIds(List<Long> deptIds) {
    if (CollUtil.isEmpty(deptIds)) {
      return List.of();
    }
    List<SysUserVo> list =
        userService.listVo(
            new LambdaQueryWrapper<SysUser>()
                .select(
                    SysUser::getUserId,
                    SysUser::getUserName,
                    SysUser::getNickName,
                    SysUser::getEmail,
                    SysUser::getPhonenumber)
                .eq(SysUser::getStatus, SystemConstants.NORMAL)
                .in(SysUser::getDeptId, deptIds));
    return BeanUtil.copyToList(list, RemoteUserVo.class);
  }

  /**
   * 通过岗位ID查询用户
   *
   * @param postIds 岗位ids
   * @return 用户
   */
  @Override
  public List<RemoteUserVo> selectUsersByPostIds(List<Long> postIds) {
    if (CollUtil.isEmpty(postIds)) {
      return List.of();
    }

    // 通过岗位ID获取用户岗位信息
    List<SysUserPost> userPosts =
        userPostMapper.selectList(
            new LambdaQueryWrapper<SysUserPost>().in(SysUserPost::getPostId, postIds));

    // 获取用户ID列表
    Set<Long> userIds = StreamUtils.toSet(userPosts, SysUserPost::getUserId);

    return selectListByIds(new ArrayList<>(userIds));
  }

  /**
   * 根据用户 ID 列表查询用户名称映射关系
   *
   * @param userIds 用户 ID 列表
   * @return Map，其中 key 为用户 ID，value 为对应的用户名称
   */
  @Override
  public Map<Long, String> selectUserNamesByIds(List<Long> userIds) {
    if (CollUtil.isEmpty(userIds)) {
      return Collections.emptyMap();
    }
    return userService
        .list(
            new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getUserId, SysUser::getNickName)
                .in(SysUser::getUserId, userIds))
        .stream()
        .collect(Collectors.toMap(SysUser::getUserId, SysUser::getNickName));
  }

  /**
   * 根据角色 ID 列表查询角色名称映射关系
   *
   * @param roleIds 角色 ID 列表
   * @return Map，其中 key 为角色 ID，value 为对应的角色名称
   */
  @Override
  public Map<Long, String> selectRoleNamesByIds(List<Long> roleIds) {
    if (CollUtil.isEmpty(roleIds)) {
      return Collections.emptyMap();
    }
    return roleService
        .list(
            new LambdaQueryWrapper<SysRole>()
                .select(SysRole::getRoleId, SysRole::getRoleName)
                .in(SysRole::getRoleId, roleIds))
        .stream()
        .collect(Collectors.toMap(SysRole::getRoleId, SysRole::getRoleName));
  }

  /**
   * 根据部门 ID 列表查询部门名称映射关系
   *
   * @param deptIds 部门 ID 列表
   * @return Map，其中 key 为部门 ID，value 为对应的部门名称
   */
  @Override
  public Map<Long, String> selectDeptNamesByIds(List<Long> deptIds) {
    if (CollUtil.isEmpty(deptIds)) {
      return Collections.emptyMap();
    }
    return deptService
        .list(
            new LambdaQueryWrapper<SysDept>()
                .select(SysDept::getDeptId, SysDept::getDeptName)
                .in(SysDept::getDeptId, deptIds))
        .stream()
        .collect(Collectors.toMap(SysDept::getDeptId, SysDept::getDeptName));
  }

  /**
   * 根据岗位 ID 列表查询岗位名称映射关系
   *
   * @param postIds 岗位 ID 列表
   * @return Map，其中 key 为岗位 ID，value 为对应的岗位名称
   */
  @Override
  public Map<Long, String> selectPostNamesByIds(List<Long> postIds) {
    if (CollUtil.isEmpty(postIds)) {
      return Collections.emptyMap();
    }
    return postService
        .list(
            new LambdaQueryWrapper<SysPost>()
                .select(SysPost::getPostId, SysPost::getPostName)
                .in(SysPost::getPostId, postIds))
        .stream()
        .collect(Collectors.toMap(SysPost::getPostId, SysPost::getPostName));
  }
}
