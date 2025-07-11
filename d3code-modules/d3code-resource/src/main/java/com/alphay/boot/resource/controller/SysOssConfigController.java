package com.alphay.boot.resource.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.core.validate.QueryGroup;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.resource.api.domain.param.SysOssConfigQueryParam;
import com.alphay.boot.resource.domain.bo.SysOssConfigBo;
import com.alphay.boot.resource.domain.vo.SysOssConfigVo;
import com.alphay.boot.resource.service.ISysOssConfigService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 对象存储配置Controller
 *
 * @author Nottyjay
 * @since 1.0.0
 * @author Nottyjay
 * @since 1.0.0
 * @date 2021-08-13
 */
@Validated
@RestController
@RequestMapping("/oss/config")
public class SysOssConfigController extends BaseController {

  @Resource private ISysOssConfigService iSysOssConfigService;

  /** 查询对象存储配置列表 */
  @SaCheckPermission("system:ossConfig:list")
  @GetMapping("/list")
  public PageResult<SysOssConfigVo> list(
      @Validated(QueryGroup.class) SysOssConfigQueryParam param) {
    return iSysOssConfigService.queryPageList(param);
  }

  /**
   * 获取对象存储配置详细信息
   *
   * @param ossConfigId OSS配置ID
   */
  @SaCheckPermission("system:ossConfig:list")
  @GetMapping("/{ossConfigId}")
  public R<SysOssConfigVo> getInfo(
      @NotNull(message = "主键不能为空") @PathVariable("ossConfigId") Long ossConfigId) {
    return R.ok(iSysOssConfigService.getVoById(ossConfigId));
  }

  /** 新增对象存储配置 */
  @SaCheckPermission("system:ossConfig:add")
  @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
  @PostMapping()
  public R<Void> add(@Validated(AddGroup.class) @RequestBody SysOssConfigBo bo) {
    return toAjax(iSysOssConfigService.insertByBo(bo));
  }

  /** 修改对象存储配置 */
  @SaCheckPermission("system:ossConfig:edit")
  @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
  @PutMapping()
  public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOssConfigBo bo) {
    return toAjax(iSysOssConfigService.updateByBo(bo));
  }

  /**
   * 删除对象存储配置
   *
   * @param ossConfigIds OSS配置ID串
   */
  @SaCheckPermission("system:ossConfig:remove")
  @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ossConfigIds}")
  public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossConfigIds) {
    return toAjax(iSysOssConfigService.deleteWithValidByIds(Arrays.asList(ossConfigIds), true));
  }

  /** 状态修改 */
  @SaCheckPermission("system:ossConfig:edit")
  @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysOssConfigBo bo) {
    return toAjax(iSysOssConfigService.updateOssConfigStatus(bo));
  }
}
