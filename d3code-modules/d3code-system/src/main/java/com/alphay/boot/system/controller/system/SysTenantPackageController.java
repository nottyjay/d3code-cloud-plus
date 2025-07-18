package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.bean.BeanUtil;
import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.idempotent.annotation.RepeatSubmit;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysTenantPackageQueryParam;
import com.alphay.boot.system.domain.bo.SysTenantPackageBo;
import com.alphay.boot.system.domain.vo.SysTenantPackageVo;
import com.alphay.boot.system.service.ISysTenantPackageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 租户套餐管理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/tenant/package")
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class SysTenantPackageController extends BaseController {

  @Resource private ISysTenantPackageService tenantPackageService;

  /** 查询租户套餐列表 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:list")
  @GetMapping("/list")
  public PageResult<SysTenantPackageVo> list(SysTenantPackageQueryParam param) {
    return tenantPackageService.queryPageList(param);
  }

  /** 查询租户套餐下拉选列表 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:list")
  @GetMapping("/selectList")
  public R<List<SysTenantPackageVo>> selectList() {
    return R.ok(tenantPackageService.queryList());
  }

  /** 导出租户套餐列表 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:export")
  @Log(title = "租户套餐", businessType = BusinessType.EXPORT)
  @PostMapping("/export")
  public void export(SysTenantPackageQueryParam param, HttpServletResponse response) {
    List<SysTenantPackageVo> list = tenantPackageService.queryList(param);
    ExcelUtil.exportExcel(list, "租户套餐", SysTenantPackageVo.class, response);
  }

  /**
   * 获取租户套餐详细信息
   *
   * @param packageId 主键
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:query")
  @GetMapping("/{packageId}")
  public R<SysTenantPackageVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long packageId) {
    return R.ok(tenantPackageService.getVoById(packageId));
  }

  /** 新增租户套餐 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:add")
  @Log(title = "租户套餐", businessType = BusinessType.INSERT)
  @RepeatSubmit()
  @PostMapping()
  public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantPackageBo bo) {
    SysTenantPackageQueryParam param = BeanUtil.toBean(bo, SysTenantPackageQueryParam.class);
    if (!tenantPackageService.checkPackageNameUnique(param)) {
      return R.fail("新增套餐'" + bo.getPackageName() + "'失败，套餐名称已存在");
    }
    return toAjax(tenantPackageService.insertByBo(bo));
  }

  /** 修改租户套餐 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:edit")
  @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
  @RepeatSubmit()
  @PutMapping()
  public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantPackageBo bo) {
    SysTenantPackageQueryParam param = BeanUtil.toBean(bo, SysTenantPackageQueryParam.class);
    if (!tenantPackageService.checkPackageNameUnique(param)) {
      return R.fail("修改套餐'" + bo.getPackageName() + "'失败，套餐名称已存在");
    }
    return toAjax(tenantPackageService.updateByBo(bo));
  }

  /** 状态修改 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:edit")
  @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysTenantPackageBo bo) {
    return toAjax(tenantPackageService.updatePackageStatus(bo));
  }

  /**
   * 删除租户套餐
   *
   * @param packageIds 主键串
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenantPackage:remove")
  @Log(title = "租户套餐", businessType = BusinessType.DELETE)
  @DeleteMapping("/{packageIds}")
  public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] packageIds) {
    return toAjax(tenantPackageService.deleteWithValidByIds(Arrays.asList(packageIds), true));
  }
}
