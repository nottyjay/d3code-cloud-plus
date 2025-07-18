package com.alphay.boot.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.excel.utils.ExcelUtil;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.system.api.domain.param.SysDictDataQueryParam;
import com.alphay.boot.system.domain.bo.SysDictDataBo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.alphay.boot.system.service.ISysDictDataService;
import com.alphay.boot.system.service.ISysDictTypeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
@RequestMapping("/dict/data")
public class SysDictDataController extends BaseController {

  @Resource private ISysDictDataService dictDataService;
  @Resource private ISysDictTypeService dictTypeService;

  /** 查询字典数据列表 */
  @SaCheckPermission("system:dict:list")
  @GetMapping("/list")
  public PageResult<SysDictDataVo> list(SysDictDataQueryParam param) {
    return dictDataService.queryPageDictDataList(param);
  }

  /** 导出字典数据列表 */
  @Log(title = "字典数据", businessType = BusinessType.EXPORT)
  @SaCheckPermission("system:dict:export")
  @PostMapping("/export")
  public void export(SysDictDataQueryParam param, HttpServletResponse response) {
    List<SysDictDataVo> list = dictDataService.queryList(param);
    ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
  }

  /**
   * 查询字典数据详细
   *
   * @param dictCode 字典code
   */
  @SaCheckPermission("system:dict:query")
  @GetMapping(value = "/{dictCode}")
  public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
    return R.ok(dictDataService.getVoById(dictCode));
  }

  /**
   * 根据字典类型查询字典数据信息
   *
   * @param dictType 字典类型
   */
  @GetMapping(value = "/type/{dictType}")
  public R<List<SysDictDataVo>> dictType(@PathVariable String dictType) {
    List<SysDictDataVo> data = dictTypeService.selectDictDataByType(dictType);
    if (ObjectUtil.isNull(data)) {
      data = new ArrayList<>();
    }
    return R.ok(data);
  }

  /** 新增字典类型 */
  @SaCheckPermission("system:dict:add")
  @Log(title = "字典数据", businessType = BusinessType.INSERT)
  @PostMapping
  public R<Void> add(@Validated @RequestBody SysDictDataBo dict) {
    if (!dictDataService.checkDictDataUnique(dict)) {
      return R.fail("新增字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
    }
    dictDataService.insertDictData(dict);
    return R.ok();
  }

  /** 修改保存字典类型 */
  @SaCheckPermission("system:dict:edit")
  @Log(title = "字典数据", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> edit(@Validated @RequestBody SysDictDataBo dict) {
    if (!dictDataService.checkDictDataUnique(dict)) {
      return R.fail("修改字典数据'" + dict.getDictValue() + "'失败，字典键值已存在");
    }
    dictDataService.updateDictData(dict);
    return R.ok();
  }

  /**
   * 删除字典类型
   *
   * @param dictCodes 字典code串
   */
  @SaCheckPermission("system:dict:remove")
  @Log(title = "字典类型", businessType = BusinessType.DELETE)
  @DeleteMapping("/{dictCodes}")
  public R<Void> remove(@PathVariable Long[] dictCodes) {
    dictDataService.deleteDictDataByIds(Arrays.asList(dictCodes));
    return R.ok();
  }
}
