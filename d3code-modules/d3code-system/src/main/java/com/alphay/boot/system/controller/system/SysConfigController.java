package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysConfigQueryParam;
import com.alphay.boot.system.domain.bo.SysConfigBo;
import com.alphay.boot.system.domain.vo.SysConfigVo;
import com.alphay.boot.system.service.ISysConfigService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置 信息操作处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController {

  @Resource private ISysConfigService configService;

  /** 获取参数配置列表 */
  @SaCheckPermission("system:config:list")
  @GetMapping("/list")
  public PageResult<SysConfigVo> list(SysConfigQueryParam param) {
    return configService.queryPageList(param);
  }

  /** 导出参数配置列表 */
  @Log(title = "参数管理", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:config:export")
  @PostMapping("/export")
  public void export(SysConfigQueryParam param, HttpServletResponse response) {
    List<SysConfigVo> list = configService.queryList(param);
    ExcelUtil.exportExcel(list, "参数数据", SysConfigVo.class, response);
  }

  /**
   * 根据参数编号获取详细信息
   *
   * @param configId 参数ID
   */
  @SaCheckPermission("system:config:query")
  @GetMapping(value = "/{configId}")
  public R<SysConfigVo> getInfo(@PathVariable Long configId) {
    return R.ok(configService.getVoById(configId));
  }

  /**
   * 根据参数键名查询参数值
   *
   * @param configKey 参数Key
   */
  @GetMapping(value = "/configKey/{configKey}")
  public R<String> getConfigKey(@PathVariable String configKey) {
    return R.ok("操作成功", configService.selectConfigByKey(configKey));
  }

  /** 新增参数配置 */
  @SaCheckPermission("system:config:add")
  @Log(title = "参数管理", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysConfigBo config) {
    if (!configService.checkConfigKeyUnique(config)) {
      return R.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
    }
    configService.insertConfig(config);
    return R.ok();
  }

  /** 修改参数配置 */
  @SaCheckPermission("system:config:edit")
  @Log(title = "参数管理", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysConfigBo config) {
    if (!configService.checkConfigKeyUnique(config)) {
      return R.fail("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
    }
    configService.updateConfig(config);
    return R.ok();
  }

  /** 根据参数键名修改参数配置 */
  @SaCheckPermission("system:config:edit")
  @Log(title = "参数管理", businessType = BusinessType.UPDATE)
  @PutMapping("/updateByKey")
  public R<Void> updateByKey(@RequestBody SysConfigBo config) {
    configService.updateConfig(config);
    return R.ok();
  }

  /**
   * 删除参数配置
   *
   * @param configIds 参数ID串
   */
  @SaCheckPermission("system:config:remove")
  @Log(title = "参数管理", businessType = BusinessType.DELETE)
  @DeleteMapping("/{configIds}")
  public R<Void> remove(@PathVariable Long[] configIds) {
    configService.deleteConfigByIds(Arrays.asList(configIds));
    return R.ok();
  }

  /** 刷新参数缓存 */
  @SaCheckPermission("system:config:remove")
  @Log(title = "参数管理", businessType = BusinessType.CLEAN)
  @DeleteMapping("/refreshCache")
  public R<Void> refreshCache() {
    configService.resetConfigCache();
    return R.ok();
  }
}
