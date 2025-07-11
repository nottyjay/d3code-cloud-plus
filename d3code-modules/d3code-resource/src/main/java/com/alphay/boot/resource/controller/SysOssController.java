package com.alphay.boot.resource.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.validate.QueryGroup;
import com.alphay.boot.common.log.annotation.Log;
import com.alphay.boot.common.log.enums.BusinessType;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.web.core.BaseController;
import com.alphay.boot.resource.api.domain.param.SysOssQueryParam;
import com.alphay.boot.resource.domain.vo.SysOssUploadVo;
import com.alphay.boot.resource.domain.vo.SysOssVo;
import com.alphay.boot.resource.service.ISysOssService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传 控制层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/oss")
public class SysOssController extends BaseController {

  @Resource private ISysOssService iSysOssService;

  /** 查询OSS对象存储列表 */
  @SaCheckPermission("system:oss:list")
  @GetMapping("/list")
  public PageResult<SysOssVo> list(@Validated(QueryGroup.class) SysOssQueryParam param) {
    return iSysOssService.queryPageList(param);
  }

  /**
   * 查询OSS对象基于id串
   *
   * @param ossIds OSS对象ID串
   */
  @SaCheckPermission("system:oss:query")
  @GetMapping("/listByIds/{ossIds}")
  public R<List<SysOssVo>> listByIds(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
    List<SysOssVo> list = iSysOssService.queryByIds(Arrays.asList(ossIds));
    return R.ok(list);
  }

  /**
   * 上传OSS对象存储
   *
   * @param file 文件
   */
  @SaCheckPermission("system:oss:upload")
  @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public R<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file) {
    if (ObjectUtil.isNull(file)) {
      return R.fail("上传文件不能为空");
    }
    SysOssVo oss = iSysOssService.upload(file);
    SysOssUploadVo uploadVo = new SysOssUploadVo();
    uploadVo.setUrl(oss.getUrl());
    uploadVo.setFileName(oss.getOriginalName());
    uploadVo.setOssId(oss.getOssId().toString());
    return R.ok(uploadVo);
  }

  /**
   * 下载OSS对象存储
   *
   * @param ossId OSS对象ID
   */
  @SaCheckPermission("system:oss:download")
  @GetMapping("/download/{ossId}")
  public void download(@PathVariable Long ossId, HttpServletResponse response) throws IOException {
    iSysOssService.download(ossId, response);
  }

  /**
   * 删除OSS对象存储
   *
   * @param ossIds OSS对象ID串
   */
  @SaCheckPermission("system:oss:remove")
  @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ossIds}")
  public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
    return toAjax(iSysOssService.deleteWithValidByIds(Arrays.asList(ossIds), true));
  }
}
