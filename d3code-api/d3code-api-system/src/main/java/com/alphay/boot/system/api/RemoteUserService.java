package com.alphay.boot.system.api;

import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.exception.user.UserException;
import com.alphay.boot.system.api.domain.bo.RemoteUserBo;
import com.alphay.boot.system.api.domain.vo.RemoteUserVo;
import com.alphay.boot.system.api.model.LoginUser;
import com.alphay.boot.system.api.model.XcxLoginUser;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteUserService {

  /**
   * 通过用户名查询用户信息
   *
   * @param username 用户名
   * @param tenantId 租户id
   * @return 结果
   */
  LoginUser getUserInfo(String username, String tenantId) throws UserException;

  /**
   * 通过用户id查询用户信息
   *
   * @param userId 用户id
   * @param tenantId 租户id
   * @return 结果
   */
  LoginUser getUserInfo(Long userId, String tenantId) throws UserException;

  /**
   * 通过手机号查询用户信息
   *
   * @param phonenumber 手机号
   * @param tenantId 租户id
   * @return 结果
   */
  LoginUser getUserInfoByPhonenumber(String phonenumber, String tenantId) throws UserException;

  /**
   * 通过邮箱查询用户信息
   *
   * @param email 邮箱
   * @param tenantId 租户id
   * @return 结果
   */
  LoginUser getUserInfoByEmail(String email, String tenantId) throws UserException;

  /**
   * 通过openid查询用户信息
   *
   * @param openid openid
   * @return 结果
   */
  XcxLoginUser getUserInfoByOpenid(String openid) throws UserException;

  /**
   * 注册用户信息
   *
   * @param remoteUserBo 用户信息
   * @return 结果
   */
  Boolean registerUserInfo(RemoteUserBo remoteUserBo) throws UserException, ServiceException;

  /**
   * 通过userId查询用户账户
   *
   * @param userId 用户id
   * @return 结果
   */
  String selectUserNameById(Long userId);

  /**
   * 通过用户ID查询用户昵称
   *
   * @param userId 用户id
   * @return 结果
   */
  String selectNicknameById(Long userId);

  /**
   * 通过用户ID查询用户账户
   *
   * @param userIds 用户ID 多个用逗号隔开
   * @return 用户名称
   */
  String selectNicknameByIds(String userIds);

  /**
   * 通过用户ID查询用户手机号
   *
   * @param userId 用户id
   * @return 用户手机号
   */
  String selectPhonenumberById(Long userId);

  /**
   * 通过用户ID查询用户邮箱
   *
   * @param userId 用户id
   * @return 用户邮箱
   */
  String selectEmailById(Long userId);

  /**
   * 更新用户信息
   *
   * @param userId 用户ID
   * @param ip IP地址
   */
  void recordLoginInfo(Long userId, String ip);

  /**
   * 通过用户ID查询用户列表
   *
   * @param userIds 用户ids
   * @return 用户列表
   */
  List<RemoteUserVo> selectListByIds(List<Long> userIds);

  /**
   * 通过角色ID查询用户ID
   *
   * @param roleIds 角色ids
   * @return 用户ids
   */
  List<Long> selectUserIdsByRoleIds(List<Long> roleIds);

  /**
   * 通过角色ID查询用户
   *
   * @param roleIds 角色ids
   * @return 用户
   */
  List<RemoteUserVo> selectUsersByRoleIds(List<Long> roleIds);

  /**
   * 通过部门ID查询用户
   *
   * @param deptIds 部门ids
   * @return 用户
   */
  List<RemoteUserVo> selectUsersByDeptIds(List<Long> deptIds);

  /**
   * 通过岗位ID查询用户
   *
   * @param postIds 岗位ids
   * @return 用户
   */
  List<RemoteUserVo> selectUsersByPostIds(List<Long> postIds);

  /**
   * 根据用户 ID 列表查询用户名称映射关系
   *
   * @param userIds 用户 ID 列表
   * @return Map，其中 key 为用户 ID，value 为对应的用户名称
   */
  Map<Long, String> selectUserNamesByIds(List<Long> userIds);

  /**
   * 根据角色 ID 列表查询角色名称映射关系
   *
   * @param roleIds 角色 ID 列表
   * @return Map，其中 key 为角色 ID，value 为对应的角色名称
   */
  Map<Long, String> selectRoleNamesByIds(List<Long> roleIds);

  /**
   * 根据部门 ID 列表查询部门名称映射关系
   *
   * @param deptIds 部门 ID 列表
   * @return Map，其中 key 为部门 ID，value 为对应的部门名称
   */
  Map<Long, String> selectDeptNamesByIds(List<Long> deptIds);

  /**
   * 根据岗位 ID 列表查询岗位名称映射关系
   *
   * @param postIds 岗位 ID 列表
   * @return Map，其中 key 为岗位 ID，value 为对应的岗位名称
   */
  Map<Long, String> selectPostNamesByIds(List<Long> postIds);
}
