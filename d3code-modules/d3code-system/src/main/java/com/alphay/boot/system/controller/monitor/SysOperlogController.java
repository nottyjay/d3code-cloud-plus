package com.alphay.boot.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysOperLogQueryParam;
import com.alphay.boot.system.domain.vo.SysOperLogVo;
import com.alphay.boot.system.service.ISysOperLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志记录
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController {

  @Resource private ISysOperLogService operLogService;

  /** 获取操作日志记录列表 */
  @SaCheckPermission("monitor:operlog:list")
  @GetMapping("/list")
  public PageResult<SysOperLogVo> list(SysOperLogQueryParam param) {
    return operLogService.queryPageList(param);
  }

  /** 导出操作日志记录列表 */
  @Log(title = "操作日志", businessType = BusinessType.EXPORT)
  @SaCheckPermission("monitor:operlog:export")
  @PostMapping("/export")
  public void export(SysOperLogQueryParam param, HttpServletResponse response) {
    List<SysOperLogVo> list = operLogService.queryList(param);
    ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo.class, response);
  }

  /**
   * 批量删除操作日志记录
   *
   * @param operIds 日志ids
   */
  @Log(title = "操作日志", businessType = BusinessType.DELETE)
  @SaCheckPermission("monitor:operlog:remove")
  @DeleteMapping("/{operIds}")
  public R<Void> remove(@PathVariable Long[] operIds) {
    return toAjax(operLogService.deleteOperLogByIds(operIds));
  }

  /** 清理操作日志记录 */
  @SaCheckPermission("monitor:operlog:remove")
  @Log(title = "操作日志", businessType = BusinessType.CLEAN)
  @DeleteMapping("/clean")
  public R<Void> clean() {
    operLogService.cleanOperLog();
    return R.ok();
  }
}
