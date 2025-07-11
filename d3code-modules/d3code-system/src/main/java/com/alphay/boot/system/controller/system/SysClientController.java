package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.idempotent.annotation.RepeatSubmit;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysClientQueryParam;
import com.alphay.boot.system.domain.bo.SysClientBo;
import com.alphay.boot.system.domain.vo.SysClientVo;
import com.alphay.boot.system.service.ISysClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 客户端管理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
public class SysClientController extends BaseController {

  @Resource private ISysClientService sysClientService;

  /** 查询客户端管理列表 */
  @SaCheckPermission("system:client:list")
  @GetMapping("/list")
  public PageResult<SysClientVo> list(SysClientQueryParam param) {
    return sysClientService.queryPageList(param);
  }

  /** 导出客户端管理列表 */
  @SaCheckPermission("system:client:export")
  @Log(title = "客户端管理", businessType = BusinessType.EXPORT)
  @PostMapping("/export")
  public void export(SysClientQueryParam param, HttpServletResponse response) {
    List<SysClientVo> list = sysClientService.queryList(param);
    ExcelUtil.exportExcel(list, "客户端管理", SysClientVo.class, response);
  }

  /**
   * 获取客户端管理详细信息
   *
   * @param id 主键
   */
  @SaCheckPermission("system:client:query")
  @GetMapping("/{id}")
  public R<SysClientVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
    return R.ok(sysClientService.getVoById(id));
  }

  /** 新增客户端管理 */
  @SaCheckPermission("system:client:add")
  @Log(title = "客户端管理", businessType = BusinessType.INSERT)
  @RepeatSubmit()
  @PostMapping()
  public R<Void> add(@Validated(AddGroup.class) @RequestBody SysClientBo bo) {
    return toAjax(sysClientService.insertByBo(bo));
  }

  /** 修改客户端管理 */
  @SaCheckPermission("system:client:edit")
  @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
  @RepeatSubmit()
  @PutMapping()
  public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysClientBo bo) {
    return toAjax(sysClientService.updateByBo(bo));
  }

  /** 状态修改 */
  @SaCheckPermission("system:client:edit")
  @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
  @PutMapping("/changeStatus")
  public R<Void> changeStatus(@RequestBody SysClientBo bo) {
    return toAjax(sysClientService.updateClientStatus(bo.getClientId(), bo.getStatus()));
  }

  /**
   * 删除客户端管理
   *
   * @param ids 主键串
   */
  @SaCheckPermission("system:client:remove")
  @Log(title = "客户端管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ids}")
  public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
    return toAjax(sysClientService.deleteWithValidByIds(List.of(ids), true));
  }
}
