package com.alphay.boot.gen.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.helper.DataBaseHelper;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.gen.domain.GenTable;
import com.alphay.boot.gen.domain.GenTableColumn;
import com.alphay.boot.gen.domain.param.GenTableQueryParam;
import com.alphay.boot.gen.service.IGenTableService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代码生成 操作处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/gen")
public class GenController extends BaseController {

  @Resource private IGenTableService genTableService;

  /** 查询代码生成列表 */
  @SaCheckPermission("tool:gen:list")
  @GetMapping("/list")
  public PageResult<GenTable> genList(GenTableQueryParam queryParam) {
    return genTableService.queryPageList(queryParam);
  }

  /**
   * 修改代码生成业务
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:query")
  @GetMapping(value = "/{tableId}")
  public R<Map<String, Object>> getInfo(@PathVariable Long tableId) {
    GenTable table = genTableService.selectGenTableById(tableId);
    List<GenTable> tables = genTableService.selectGenTableAll();
    List<GenTableColumn> list = genTableService.selectGenTableColumnListByTableId(tableId);
    Map<String, Object> map = new HashMap<>(3);
    map.put("info", table);
    map.put("rows", list);
    map.put("tables", tables);
    return R.ok(map);
  }

  /** 查询数据库列表 */
  @SaCheckPermission("tool:gen:list")
  @GetMapping("/db/list")
  public PageResult<GenTable> dataList(GenTableQueryParam param) {
    return genTableService.queryPageDbTableList(param);
  }

  /**
   * 查询数据表字段列表
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:list")
  @GetMapping(value = "/column/{tableId}")
  public PageResult<GenTableColumn> columnList(@PathVariable("tableId") Long tableId) {
    List<GenTableColumn> list = genTableService.selectGenTableColumnListByTableId(tableId);
    return PageResult.build(list);
  }

  /**
   * 导入表结构（保存）
   *
   * @param tables 表名串
   */
  @SaCheckPermission("tool:gen:import")
  @Log(title = "代码生成", businessType = BusinessType.IMPORT)
  @PostMapping("/importTable")
  public R<Void> importTableSave(String tables, String dataName) {
    String[] tableNames = Convert.toStrArray(tables);
    // 查询表信息
    List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames, dataName);
    genTableService.importGenTable(tableList, dataName);
    return R.ok();
  }

  /** 修改保存代码生成业务 */
  @SaCheckPermission("tool:gen:edit")
  @Log(title = "代码生成", businessType = BusinessType.UPDATE)
  @PutMapping
  public R<Void> editSave(@Validated @RequestBody GenTable genTable) {
    genTableService.validateEdit(genTable);
    genTableService.updateGenTable(genTable);
    return R.ok();
  }

  /**
   * 删除代码生成
   *
   * @param tableIds 表ID串
   */
  @SaCheckPermission("tool:gen:remove")
  @Log(title = "代码生成", businessType = BusinessType.DELETE)
  @DeleteMapping("/{tableIds}")
  public R<Void> remove(@PathVariable Long[] tableIds) {
    genTableService.deleteGenTableByIds(tableIds);
    return R.ok();
  }

  /**
   * 预览代码
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:preview")
  @GetMapping("/preview/{tableId}")
  public R<Map<String, String>> preview(@PathVariable("tableId") Long tableId) throws IOException {
    Map<String, String> dataMap = genTableService.previewCode(tableId);
    return R.ok(dataMap);
  }

  /**
   * 生成代码（下载方式）
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:code")
  @Log(title = "代码生成", businessType = BusinessType.GENCODE)
  @GetMapping("/download/{tableId}")
  public void download(HttpServletResponse response, @PathVariable("tableId") Long tableId)
      throws IOException {
    byte[] data = genTableService.downloadCode(tableId);
    genCode(response, data);
  }

  /**
   * 生成代码（自定义路径）
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:code")
  @Log(title = "代码生成", businessType = BusinessType.GENCODE)
  @GetMapping("/genCode/{tableId}")
  public R<Void> genCode(@PathVariable("tableId") Long tableId) {
    genTableService.generatorCode(tableId);
    return R.ok();
  }

  /**
   * 同步数据库
   *
   * @param tableId 表ID
   */
  @SaCheckPermission("tool:gen:edit")
  @Log(title = "代码生成", businessType = BusinessType.UPDATE)
  @GetMapping("/synchDb/{tableId}")
  public R<Void> synchDb(@PathVariable("tableId") Long tableId) {
    genTableService.synchDb(tableId);
    return R.ok();
  }

  /**
   * 批量生成代码
   *
   * @param tableIdStr 表ID串
   */
  @SaCheckPermission("tool:gen:code")
  @Log(title = "代码生成", businessType = BusinessType.GENCODE)
  @GetMapping("/batchGenCode")
  public void batchGenCode(HttpServletResponse response, String tableIdStr) throws IOException {
    String[] tableIds = Convert.toStrArray(tableIdStr);
    byte[] data = genTableService.downloadCode(tableIds);
    genCode(response, data);
  }

  /** 生成zip文件 */
  private void genCode(HttpServletResponse response, byte[] data) throws IOException {
    response.reset();
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
    response.setHeader("Content-Disposition", "attachment; filename=\"d3code.zip\"");
    response.addHeader("Content-Length", "" + data.length);
    response.setContentType("application/octet-stream; charset=UTF-8");
    IoUtil.write(response.getOutputStream(), false, data);
  }

  /** 查询数据源名称列表 */
  @SaCheckPermission("tool:gen:list")
  @GetMapping(value = "/getDataNames")
  public R<Object> getCurrentDataSourceNameList() {
    return R.ok(DataBaseHelper.getDataSourceNameList());
  }
}
