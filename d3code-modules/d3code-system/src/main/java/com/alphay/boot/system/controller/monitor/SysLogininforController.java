package com.alphay.boot.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.constant.CacheConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysLogininforQueryParam;
import com.alphay.boot.system.domain.vo.SysLogininforVo;
import com.alphay.boot.system.service.ISysLogininforService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统访问记录
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController extends BaseController {

  @Resource private ISysLogininforService logininforService;

  /** 获取系统访问记录列表 */
  @SaCheckPermission("monitor:logininfor:list")
  @GetMapping("/list")
  public PageResult<SysLogininforVo> list(SysLogininforQueryParam param) {
    return logininforService.queryPageList(param);
  }

  /** 导出系统访问记录列表 */
  @Log(title = "登录日志", businessType = BusinessType.EXPORT)
  @SaCheckPermission("monitor:logininfor:export")
  @PostMapping("/export")
  public void export(SysLogininforQueryParam param, HttpServletResponse response) {
    List<SysLogininforVo> list = logininforService.queryList(param);
    ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
  }

  /**
   * 批量删除登录日志
   *
   * @param infoIds 日志ids
   */
  @SaCheckPermission("monitor:logininfor:remove")
  @Log(title = "登录日志", businessType = BusinessType.DELETE)
  @DeleteMapping("/{infoIds}")
  public R<Void> remove(@PathVariable Long[] infoIds) {
    return toAjax(logininforService.deleteLogininforByIds(infoIds));
  }

  /** 清理系统访问记录 */
  @SaCheckPermission("monitor:logininfor:remove")
  @Log(title = "登录日志", businessType = BusinessType.CLEAN)
  @DeleteMapping("/clean")
  public R<Void> clean() {
    logininforService.cleanLogininfor();
    return R.ok();
  }

  @SaCheckPermission("monitor:logininfor:unlock")
  @Log(title = "账户解锁", businessType = BusinessType.OTHER)
  @GetMapping("/unlock/{userName}")
  public R<Void> unlock(@PathVariable("userName") String userName) {
    String loginName = CacheConstants.PWD_ERR_CNT_KEY + userName;
    if (RedisUtils.hasKey(loginName)) {
      RedisUtils.deleteObject(loginName);
    }
    return R.ok();
  }
}
