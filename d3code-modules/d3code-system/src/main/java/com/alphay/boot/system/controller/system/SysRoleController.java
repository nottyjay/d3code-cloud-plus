package com.alphay.boot.system.controller.system;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.role;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysDeptQueryParam;
import com.alphay.boot.system.api.domain.param.SysRoleQueryParam;
import com.alphay.boot.system.api.domain.param.SysUserQueryParam;
import com.alphay.boot.system.domain.SysUserRole;
import com.alphay.boot.system.domain.bo.SysRoleBo;
import com.alphay.boot.system.domain.vo.SysRoleVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.service.ISysDeptService;
import com.alphay.boot.system.service.ISysRoleService;
import com.alphay.boot.system.service.ISysUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController {

  @Resource private ISysRoleService roleService;
  @Resource private ISysUserService userService;
  @Resource private ISysDeptService deptService;

  /** 获取角色信息列表 */
  @SaCheckPermission("system:role:list")
  @GetMapping("/list")
  public PageResult<SysRoleVo> list(SysRoleQueryParam param) {
    return roleService.queryPageList(param);
  }

  /** 导出角色信息列表 */
  @Log(title = "角色管理", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:role:export")
  @PostMapping("/export")
  public void export(SysRoleQueryParam param, HttpServletResponse response) {
    List<SysRoleVo> list = roleService.queryList(param);
    ExcelUtil.exportExcel(list, "角色数据", SysRoleVo.class, response);
  }

  /**
   * 根据角色编号获取详细信息
   *
   * @param roleId 角色ID
   */
  @SaCheckPermission("system:role:query")
  @GetMapping(value = "/{roleId}")
  public R<SysRoleVo> getInfo(@PathVariable Long roleId) {
    roleService.checkRoleDataScope(roleId);
    return R.ok(roleService.getVoById(roleId));
  }

  /** 新增角色 */
  @SaCheckPermission("system:role:add")
  @Log(title = "角色管理", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysRoleBo role) {
    SysRoleQueryParam param = BeanUtil.toBean(role, SysRoleQueryParam.class);
    roleService.checkRoleAllowed(param);
    if (!roleService.checkRoleNameUnique(param)) {
      return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
    } else if (!roleService.checkRoleKeyUnique(param)) {
      return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
    }
    return toAjax(roleService.insertRole(role));
  }

  /** 修改保存角色 */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysRoleBo role) {
    SysRoleQueryParam param = BeanUtil.toBean(role, SysRoleQueryParam.class);
    roleService.checkRoleAllowed(param);
    roleService.checkRoleDataScope(role.getRoleId());
    if (!roleService.checkRoleNameUnique(param)) {
      return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
    } else if (!roleService.checkRoleKeyUnique(param)) {
      return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
    }

    if (roleService.updateRole(role) > 0) {
      roleService.cleanOnlineUserByRole(role.getRoleId());
      return R.ok();
    }
    return R.fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
  }

  /** 修改保存数据权限 */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @PutMapping("/dataScope")
  public R<Void> dataScope(@RequestBody SysRoleBo role) {
    SysRoleQueryParam param = BeanUtil.toBean(role, SysRoleQueryParam.class);
    roleService.checkRoleAllowed(param);
    roleService.checkRoleDataScope(role.getRoleId());
    return toAjax(roleService.authDataScope(role));
  }

  /** 状态修改 */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysRoleBo role) {
    SysRoleQueryParam param = BeanUtil.toBean(role, SysRoleQueryParam.class);

    roleService.checkRoleAllowed(param);
    roleService.checkRoleDataScope(role.getRoleId());
    return toAjax(roleService.updateRoleStatus(role.getRoleId(), role.getStatus()));
  }

  /**
   * 删除角色
   *
   * @param roleIds 角色ID串
   */
  @SaCheckPermission("system:role:remove")
  @Log(title = "角色管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{roleIds}")
  public R<Void> remove(@PathVariable Long[] roleIds) {
    return toAjax(roleService.deleteRoleByIds(roleIds));
  }

  /**
   * 获取角色选择框列表
   *
   * @param roleIds 角色ID串
   */
  @SaCheckPermission("system:role:query")
  @GetMapping("/optionselect")
  public R<List<SysRoleVo>> optionselect(@RequestParam(required = false) Long[] roleIds) {
    return R.ok(roleService.queryListByIds(roleIds == null ? null : List.of(roleIds)));
  }

  /** 查询已分配用户角色列表 */
  @SaCheckPermission("system:role:list")
  @GetMapping("/authUser/allocatedList")
  public PageResult<SysUserVo> allocatedList(SysUserQueryParam param) {
    return userService.queryPageAllocatedList(param);
  }

  /** 查询未分配用户角色列表 */
  @SaCheckPermission("system:role:list")
  @GetMapping("/authUser/unallocatedList")
  public PageResult<SysUserVo> unallocatedList(SysUserQueryParam param) {
    return userService.queryPageUnallocatedList(param);
  }

  /** 取消授权用户 */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @PutMapping("/authUser/cancel")
  public R<Void> cancelAuthUser(@RequestBody SysUserRole userRole) {
    return toAjax(roleService.deleteAuthUser(userRole));
  }

  /**
   * 批量取消授权用户
   *
   * @param roleId 角色ID
   * @param userIds 用户ID串
   */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @PutMapping("/authUser/cancelAll")
  public R<Void> cancelAuthUserAll(Long roleId, Long[] userIds) {
    return toAjax(roleService.deleteAuthUsers(roleId, userIds));
  }

  /**
   * 批量选择用户授权
   *
   * @param roleId 角色ID
   * @param userIds 用户ID串
   */
  @SaCheckPermission("system:role:edit")
  @Log(title = "角色管理", businessType = BusinessType.GRANT)
  @PutMapping("/authUser/selectAll")
  public R<Void> selectAuthUserAll(Long roleId, Long[] userIds) {
    roleService.checkRoleDataScope(roleId);
    return toAjax(roleService.insertAuthUsers(roleId, userIds));
  }

  /**
   * 获取对应角色部门树列表
   *
   * @param roleId 角色ID
   */
  @SaCheckPermission("system:role:list")
  @GetMapping(value = "/deptTree/{roleId}")
  public R<DeptTreeSelectVo> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
    DeptTreeSelectVo selectVo =
        new DeptTreeSelectVo(
            deptService.selectDeptListByRoleId(roleId),
            deptService.selectDeptTreeList(new SysDeptQueryParam()));
    return R.ok(selectVo);
  }

  public record DeptTreeSelectVo(List<Long> checkedKeys, List<Tree<Long>> depts) {}
}
