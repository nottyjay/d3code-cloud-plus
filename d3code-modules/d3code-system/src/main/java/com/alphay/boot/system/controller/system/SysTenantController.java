package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.encrypt.annotation.ApiEncrypt;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.idempotent.annotation.RepeatSubmit;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysTenantQueryParam;
import com.alphay.boot.system.domain.bo.SysTenantBo;
import com.alphay.boot.system.domain.vo.SysTenantVo;
import com.alphay.boot.system.service.ISysTenantService;
import com.baomidou.lock.annotation.Lock4j;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/tenant")
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class SysTenantController extends BaseController {

  @Resource private ISysTenantService tenantService;

  /** 查询租户列表 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:list")
  @GetMapping("/list")
  public PageResult<SysTenantVo> list(SysTenantQueryParam param) {
    return tenantService.queryPageList(param);
  }

  /** 导出租户列表 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:export")
  @Log(title = "租户管理", businessType = BusinessType.EXPORT)
  @PostMapping("/export")
  public void export(SysTenantQueryParam param, HttpServletResponse response) {
    List<SysTenantVo> list = tenantService.queryList(param);
    ExcelUtil.exportExcel(list, "租户", SysTenantVo.class, response);
  }

  /**
   * 获取租户详细信息
   *
   * @param id 主键
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:query")
  @GetMapping("/{id}")
  public R<SysTenantVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
    return R.ok(tenantService.getVoById(id));
  }

  /** 新增租户 */
  @ApiEncrypt
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:add")
  @Log(title = "租户管理", businessType = BusinessType.INSERT)
  @Lock4j
  @RepeatSubmit()
  @PostMapping()
  public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantBo bo) {
    if (!tenantService.checkCompanyNameUnique(bo)) {
      return R.fail("新增租户'" + bo.getCompanyName() + "'失败，企业名称已存在");
    }
    return toAjax(TenantHelper.ignore(() -> tenantService.insertByBo(bo)));
  }

  /** 修改租户 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:edit")
  @Log(title = "租户管理", businessType = BusinessType.UPDATE)
  @RepeatSubmit()
  @PutMapping()
  public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantBo bo) {
    tenantService.checkTenantAllowed(bo.getTenantId());
    if (!tenantService.checkCompanyNameUnique(bo)) {
      return R.fail("修改租户'" + bo.getCompanyName() + "'失败，公司名称已存在");
    }
    return toAjax(tenantService.updateByBo(bo));
  }

  /** 状态修改 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:edit")
  @Log(title = "租户管理", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysTenantBo bo) {
    tenantService.checkTenantAllowed(bo.getTenantId());
    return toAjax(tenantService.updateTenantStatus(bo));
  }

  /**
   * 删除租户
   *
   * @param ids 主键串
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:remove")
  @Log(title = "租户管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ids}")
  public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
    return toAjax(tenantService.deleteWithValidByIds(Arrays.asList(ids), true));
  }

  /**
   * 动态切换租户
   *
   * @param tenantId 租户ID
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @GetMapping("/dynamic/{tenantId}")
  public R<Void> dynamicTenant(@NotBlank(message = "租户ID不能为空") @PathVariable String tenantId) {
    TenantHelper.setDynamic(tenantId, true);
    return R.ok();
  }

  /** 清除动态租户 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @GetMapping("/dynamic/clear")
  public R<Void> dynamicClear() {
    TenantHelper.clearDynamic();
    return R.ok();
  }

  /**
   * 同步租户套餐
   *
   * @param tenantId 租户id
   * @param packageId 套餐id
   */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @SaCheckPermission("system:tenant:edit")
  @Log(title = "租户管理", businessType = BusinessType.UPDATE)
  @GetMapping("/syncTenantPackage")
  public R<Void> syncTenantPackage(
      @NotBlank(message = "租户ID不能为空") String tenantId,
      @NotNull(message = "套餐ID不能为空") Long packageId) {
    return toAjax(TenantHelper.ignore(() -> tenantService.syncTenantPackage(tenantId, packageId)));
  }

  /** 同步租户字典 */
  @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
  @Log(title = "租户管理", businessType = BusinessType.INSERT)
  @GetMapping("/syncTenantDict")
  public R<Void> syncTenantDict() {
    if (!TenantHelper.isEnable()) {
      return R.fail("当前未开启租户模式");
    }
    tenantService.syncTenantDict();
    return R.ok("同步租户字典成功");
  }
}
