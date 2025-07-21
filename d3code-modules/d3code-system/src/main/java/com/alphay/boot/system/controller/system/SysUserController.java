package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.encrypt.annotation.ApiEncrypt;
import com.alphay.boot.common.excel.core.ExcelResult;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysDeptQueryParam;
import com.alphay.boot.system.api.domain.param.SysPostQueryParam;
import com.alphay.boot.system.api.domain.param.SysRoleQueryParam;
import com.alphay.boot.system.api.domain.param.SysUserQueryParam;
import com.alphay.boot.system.api.model.LoginUser;
import com.alphay.boot.system.domain.bo.SysUserBo;
import com.alphay.boot.system.domain.vo.*;
import com.alphay.boot.system.listener.SysUserImportListener;
import com.alphay.boot.system.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {

  @Resource private ISysUserService userService;
  @Resource private ISysRoleService roleService;
  @Resource private ISysPostService postService;
  @Resource private ISysDeptService deptService;
  @Resource private ISysTenantService tenantService;

  /** 获取用户列表 */
  @SaCheckPermission("system:user:list")
  @GetMapping("/list")
  public PageResult<SysUserVo> list(SysUserQueryParam param) {
    return userService.queryPageList(param);
  }

  /** 导出用户列表 */
  @Log(title = "用户管理", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:user:export")
  @PostMapping("/export")
  public void export(SysUserQueryParam param, HttpServletResponse response) {
    List<SysUserExportVo> list = userService.queryExportList(param);
    ExcelUtil.exportExcel(list, "用户数据", SysUserExportVo.class, response);
  }

  /**
   * 导入数据
   *
   * @param file 导入文件
   * @param updateSupport 是否更新已存在数据
   */
  @Log(title = "用户管理", businessType = BusinessType.IMPORT)
  @SaCheckPermission("system:user:import")
  @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport)
      throws Exception {
    ExcelResult<SysUserImportVo> result =
        ExcelUtil.importExcel(
            file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
    return R.ok(result.getAnalysis());
  }

  /** 获取导入模板 */
  @PostMapping("/importTemplate")
  public void importTemplate(HttpServletResponse response) {
    ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
  }

  /**
   * 获取用户信息
   *
   * @return 用户信息
   */
  @GetMapping("/getInfo")
  public R<UserInfoVo> getInfo() {
    UserInfoVo userInfoVo = new UserInfoVo();
    LoginUser loginUser = LoginHelper.getLoginUser();
    if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
      // 超级管理员 如果重新加载用户信息需清除动态租户
      TenantHelper.clearDynamic();
    }
    SysUserVo user = userService.getVoById(loginUser.getUserId());
    if (ObjectUtil.isNull(user)) {
      return R.fail("没有权限访问用户数据!");
    }
    user.setRoles(roleService.queryListByUserId(user.getUserId()));
    userInfoVo.setUser(user);
    userInfoVo.setPermissions(loginUser.getMenuPermission());
    userInfoVo.setRoles(loginUser.getRolePermission());
    return R.ok(userInfoVo);
  }

  /**
   * 根据用户编号获取详细信息
   *
   * @param userId 用户ID
   */
  @SaCheckPermission("system:user:query")
  @GetMapping(value = {"/", "/{userId}"})
  public R<SysUserInfoVo> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
    SysUserInfoVo userInfoVo = new SysUserInfoVo();
    if (ObjectUtil.isNotNull(userId)) {
      userService.checkUserDataScope(userId);
      SysUserVo sysUser = userService.getVoById(userId);
      userInfoVo.setUser(sysUser);
      userInfoVo.setRoleIds(roleService.fetchListByUserId(userId));
      Long deptId = sysUser.getDeptId();
      if (ObjectUtil.isNotNull(deptId)) {
        SysPostQueryParam param = new SysPostQueryParam();
        param.setDeptId(deptId);
        userInfoVo.setPosts(postService.queryList(param));
        userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
      }
    }
    SysRoleQueryParam roleQueryParam = new SysRoleQueryParam();
    roleQueryParam.setStatus(SystemConstants.NORMAL);
    List<SysRoleVo> roles = roleService.queryList(roleQueryParam);
    userInfoVo.setRoles(
        LoginHelper.isSuperAdmin(userId)
            ? roles
            : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
    return R.ok(userInfoVo);
  }

  /** 新增用户 */
  @SaCheckPermission("system:user:add")
  @Log(title = "用户管理", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysUserBo user) {
    deptService.checkDeptDataScope(user.getDeptId());
    SysUserQueryParam queryParam = BeanUtil.toBean(user, SysUserQueryParam.class);
    if (!userService.checkUserNameUnique(queryParam)) {
      return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
    } else if (StringUtils.isNotEmpty(user.getPhonenumber())
        && !userService.checkPhoneUnique(queryParam)) {
      return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
    } else if (StringUtils.isNotEmpty(user.getEmail())
        && !userService.checkEmailUnique(queryParam)) {
      return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
    }
    if (TenantHelper.isEnable()) {
      if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
        return R.fail("当前租户下用户名额不足，请联系管理员");
      }
    }
    user.setPassword(BCrypt.hashpw(user.getPassword()));
    return toAjax(userService.insertUser(user));
  }

  /** 修改用户 */
  @SaCheckPermission("system:user:edit")
  @Log(title = "用户管理", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysUserBo user) {
    userService.checkUserAllowed(user.getUserId());
    userService.checkUserDataScope(user.getUserId());
    deptService.checkDeptDataScope(user.getDeptId());
    SysUserQueryParam queryParam = BeanUtil.toBean(user, SysUserQueryParam.class);

    if (!userService.checkUserNameUnique(queryParam)) {
      return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
    } else if (StringUtils.isNotEmpty(user.getPhonenumber())
        && !userService.checkPhoneUnique(queryParam)) {
      return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
    } else if (StringUtils.isNotEmpty(user.getEmail())
        && !userService.checkEmailUnique(queryParam)) {
      return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
    }
    return toAjax(userService.updateUser(user));
  }

  /**
   * 删除用户
   *
   * @param userIds 角色ID串
   */
  @SaCheckPermission("system:user:remove")
  @Log(title = "用户管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{userIds}")
  public R<Void> remove(@PathVariable Long[] userIds) {
    if (ArrayUtil.contains(userIds, LoginHelper.getUserId())) {
      return R.fail("当前用户不能删除");
    }
    return toAjax(userService.deleteUserByIds(userIds));
  }

  /**
   * 根据用户ID串批量获取用户基础信息
   *
   * @param userIds 用户ID串
   * @param deptId 部门ID
   */
  @SaCheckPermission("system:user:query")
  @GetMapping("/optionselect")
  public R<List<SysUserVo>> optionselect(
      @RequestParam(required = false) Long[] userIds, @RequestParam(required = false) Long deptId) {
    return R.ok(
        userService.queryListByIds(ArrayUtil.isEmpty(userIds) ? null : List.of(userIds), deptId));
  }

  /** 重置密码 */
  @ApiEncrypt
  @SaCheckPermission("system:user:resetPwd")
  @Log(title = "用户管理", businessType = BusinessType.UPDATE)
  @PutMapping("/resetPwd")
  public R<Void> resetPwd(@RequestBody SysUserBo user) {
    userService.checkUserAllowed(user.getUserId());
    userService.checkUserDataScope(user.getUserId());
    user.setPassword(BCrypt.hashpw(user.getPassword()));
    return toAjax(userService.resetUserPwd(user.getUserId(), user.getPassword()));
  }

  /** 状态修改 */
  @SaCheckPermission("system:user:edit")
  @Log(title = "用户管理", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysUserBo user) {
    userService.checkUserAllowed(user.getUserId());
    userService.checkUserDataScope(user.getUserId());
    return toAjax(userService.updateUserStatus(user.getUserId(), user.getStatus()));
  }

  /**
   * 根据用户编号获取授权角色
   *
   * @param userId 用户ID
   */
  @SaCheckPermission("system:user:query")
  @GetMapping("/authRole/{userId}")
  public R<SysUserInfoVo> authRole(@PathVariable Long userId) {
    userService.checkUserDataScope(userId);
    SysUserVo user = userService.getVoById(userId);
    List<SysRoleVo> roles = roleService.queryListByUserId(userId);
    SysUserInfoVo userInfoVo = new SysUserInfoVo();
    userInfoVo.setUser(user);
    userInfoVo.setRoles(
        LoginHelper.isSuperAdmin(userId)
            ? roles
            : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
    return R.ok(userInfoVo);
  }

  /**
   * 用户授权角色
   *
   * @param userId 用户Id
   * @param roleIds 角色ID串
   */
  @SaCheckPermission("system:user:edit")
  @Log(title = "用户管理", businessType = BusinessType.GRANT)
  @PutMapping("/authRole")
  public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
    userService.checkUserDataScope(userId);
    userService.insertUserAuth(userId, roleIds);
    return R.ok();
  }

  /** 获取部门树列表 */
  @SaCheckPermission("system:user:list")
  @GetMapping("/deptTree")
  public R<List<Tree<Long>>> deptTree(SysDeptQueryParam param) {
    return R.ok(deptService.selectDeptTreeList(param));
  }

  /** 获取部门下的所有用户信息 */
  @SaCheckPermission("system:user:list")
  @GetMapping("/list/dept/{deptId}")
  public R<List<SysUserVo>> listByDept(@PathVariable @NotNull Long deptId) {
    return R.ok(userService.selectUserListByDept(deptId));
  }
}
