package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysDictTypeQueryParam;
import com.alphay.boot.system.domain.bo.SysDictTypeBo;
import com.alphay.boot.system.domain.vo.SysDictTypeVo;
import com.alphay.boot.system.service.ISysDictTypeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数据字典信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/dict/type")
public class SysDictTypeController extends BaseController {

  @Resource private ISysDictTypeService dictTypeService;

  /** 查询字典类型列表 */
  @SaCheckPermission("system:dict:list")
  @GetMapping("/list")
  public PageResult<SysDictTypeVo> list(SysDictTypeQueryParam param) {
    return dictTypeService.queryPageList(param);
  }

  /** 导出字典类型列表 */
  @Log(title = "字典类型", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:dict:export")
  @PostMapping("/export")
  public void export(SysDictTypeQueryParam param, HttpServletResponse response) {
    List<SysDictTypeVo> list = dictTypeService.queryList(param);
    ExcelUtil.exportExcel(list, "字典类型", SysDictTypeVo.class, response);
  }

  /**
   * 查询字典类型详细
   *
   * @param dictId 字典ID
   */
  @SaCheckPermission("system:dict:query")
  @GetMapping(value = "/{dictId}")
  public R<SysDictTypeVo> getInfo(@PathVariable Long dictId) {
    return R.ok(dictTypeService.getVoById(dictId));
  }

  /** 新增字典类型 */
  @SaCheckPermission("system:dict:add")
  @Log(title = "字典类型", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysDictTypeBo dict) {
    if (!dictTypeService.checkDictTypeUnique(dict)) {
      return R.fail("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
    }
    dictTypeService.insertDictType(dict);
    return R.ok();
  }

  /** 修改字典类型 */
  @SaCheckPermission("system:dict:edit")
  @Log(title = "字典类型", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysDictTypeBo dict) {
    if (!dictTypeService.checkDictTypeUnique(dict)) {
      return R.fail("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
    }
    dictTypeService.updateDictType(dict);
    return R.ok();
  }

  /**
   * 删除字典类型
   *
   * @param dictIds 字典ID串
   */
  @SaCheckPermission("system:dict:remove")
  @Log(title = "字典类型", businessType = BusinessType.DELETE)
  @DeleteMapping("/{dictIds}")
  public R<Void> remove(@PathVariable Long[] dictIds) {
    dictTypeService.deleteDictTypeByIds(Arrays.asList(dictIds));
    return R.ok();
  }

  /** 刷新字典缓存 */
  @SaCheckPermission("system:dict:remove")
  @Log(title = "字典类型", businessType = BusinessType.CLEAN)
  @DeleteMapping("/refreshCache")
  public R<Void> refreshCache() {
    dictTypeService.resetDictCache();
    return R.ok();
  }

  /** 获取字典选择框列表 */
  @GetMapping("/optionselect")
  public R<List<SysDictTypeVo>> optionselect() {
    List<SysDictTypeVo> dictTypes = dictTypeService.selectDictTypeAll();
    return R.ok(dictTypes);
  }
}
