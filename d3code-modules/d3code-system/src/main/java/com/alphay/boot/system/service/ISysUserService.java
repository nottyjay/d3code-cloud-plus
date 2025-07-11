package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysUserQueryParam;
import com.alphay.boot.system.domain.SysUser;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.vo.SysUserExportVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import java.util.List;

/**
 * 用户 业务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysUserService extends IServiceX<SysUser, SysUserVo> {

  /**
   * 根据条件分页查询用户列表
   *
   * @param param 用户信息
   * @return 用户信息
   */
  PageResult<SysUserVo> queryPageList(SysUserQueryParam param);

  /**
   * 导出用户列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  List<SysUserExportVo> queryExportList(SysUserQueryParam param);

  /**
   * 根据条件分页查询已分配用户角色列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  PageResult<SysUserVo> queryPageAllocatedList(SysUserQueryParam param);

  /**
   * 根据条件分页查询未分配用户角色列表
   *
   * @param param 用户信息
   * @return 用户信息集合信息
   */
  PageResult<SysUserVo> queryPageUnallocatedList(SysUserQueryParam param);

  /**
   * 通过用户名查询用户
   *
   * @param userName 用户名
   * @return 用户对象信息
   */
  SysUserVo selectUserByUserName(String userName);

  /**
   * 通过手机号查询用户
   *
   * @param phonenumber 手机号
   * @return 用户对象信息
   */
  SysUserVo selectUserByPhonenumber(String phonenumber);

  /**
   * 通过用户ID串查询用户
   *
   * @param userIds 用户ID串
   * @param deptId 部门id
   * @return 用户列表信息
   */
  List<SysUserVo> queryListByIds(List<Long> userIds, Long deptId);

  /**
   * 根据用户ID查询用户所属角色组
   *
   * @param userId 用户ID
   * @return 结果
   */
  String selectUserRoleGroup(Long userId);

  /**
   * 根据用户ID查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 结果
   */
  String selectUserPostGroup(Long userId);

  /**
   * 校验用户名称是否唯一
   *
   * @param user 用户信息
   * @return 结果
   */
  boolean checkUserNameUnique(SysUserQueryParam user);

  /**
   * 校验手机号码是否唯一
   *
   * @param user 用户信息
   * @return 结果
   */
  boolean checkPhoneUnique(SysUserQueryParam user);

  /**
   * 校验email是否唯一
   *
   * @param user 用户信息
   * @return 结果
   */
  boolean checkEmailUnique(SysUserQueryParam user);

  /**
   * 校验用户是否允许操作
   *
   * @param userId 用户ID
   */
  void checkUserAllowed(Long userId);

  /**
   * 校验用户是否有数据权限
   *
   * @param userId 用户id
   */
  void checkUserDataScope(Long userId);

  /**
   * 新增用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  int insertUser(SysUserBo user);

  /**
   * 注册用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  boolean registerUser(SysUserBo user, String tenantId);

  /**
   * 修改用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  int updateUser(SysUserBo user);

  /**
   * 用户授权角色
   *
   * @param userId 用户ID
   * @param roleIds 角色组
   */
  void insertUserAuth(Long userId, Long[] roleIds);

  /**
   * 修改用户状态
   *
   * @param userId 用户ID
   * @param status 帐号状态
   * @return 结果
   */
  int updateUserStatus(Long userId, String status);

  /**
   * 修改用户基本信息
   *
   * @param user 用户信息
   * @return 结果
   */
  int updateUserProfile(SysUserBo user);

  /**
   * 修改用户头像
   *
   * @param userId 用户ID
   * @param avatar 头像地址
   * @return 结果
   */
  boolean updateUserAvatar(Long userId, Long avatar);

  /**
   * 重置用户密码
   *
   * @param userId 用户ID
   * @param password 密码
   * @return 结果
   */
  int resetUserPwd(Long userId, String password);

  /**
   * 通过用户ID删除用户
   *
   * @param userId 用户ID
   * @return 结果
   */
  int deleteUserById(Long userId);

  /**
   * 批量删除用户信息
   *
   * @param userIds 需要删除的用户ID
   * @return 结果
   */
  int deleteUserByIds(Long[] userIds);

  /**
   * 通过用户ID查询用户账户
   *
   * @param userId 用户ID
   * @return 用户账户
   */
  String selectUserNameById(Long userId);

  /**
   * 通过用户ID查询用户账户
   *
   * @param userId 用户ID
   * @return 用户账户
   */
  String selectNicknameById(Long userId);

  /**
   * 通过用户ID查询用户账户
   *
   * @param userIds 用户ID
   * @return 用户账户
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
   * 通过部门id查询当前部门所有用户
   *
   * @param deptId 部门id
   * @return 结果
   */
  List<SysUserVo> selectUserListByDept(Long deptId);

  /**
   * 通过角色ID查询用户ID
   *
   * @param roleIds 角色ids
   * @return 用户ids
   */
  List<Long> selectUserIdsByRoleIds(List<Long> roleIds);

  SysUserVo selectUserByEmail(String email);
}
