package com.alphay.boot.resource.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.resource.api.domain.param.SysOssQueryParam;
import com.alphay.boot.resource.domain.SysOss;
import com.alphay.boot.resource.domain.bo.SysOssBo;
import com.alphay.boot.resource.domain.vo.SysOssVo;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysOssService extends IServiceX<SysOss, SysOssVo> {

  /**
   * 查询OSS对象存储列表
   *
   * @param param OSS对象存储分页查询对象
   * @return 结果
   */
  PageResult<SysOssVo> queryPageList(SysOssQueryParam param);

  /**
   * 根据一组 ossIds 获取对应的 SysOssVo 列表
   *
   * @param ossIds 一组文件在数据库中的唯一标识集合
   * @return 包含 SysOssVo 对象的列表
   */
  List<SysOssVo> queryByIds(Collection<Long> ossIds);

  /**
   * 根据一组 ossIds 获取对应文件的 URL 列表
   *
   * @param ossIds 以逗号分隔的 ossId 字符串
   * @return 以逗号分隔的文件 URL 字符串
   */
  String selectUrlByIds(String ossIds);

  /**
   * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
   *
   * @param file 要上传的 MultipartFile 对象
   * @return 上传成功后的 SysOssVo 对象，包含文件信息
   */
  SysOssVo upload(MultipartFile file);

  /**
   * 上传文件到对象存储服务，并保存文件信息到数据库
   *
   * @param file 要上传的文件对象
   * @return 上传成功后的 SysOssVo 对象，包含文件信息
   */
  SysOssVo upload(File file);

  /**
   * 新增OSS对象存储
   *
   * @param bo SysOssBo 对象，包含待插入的数据
   * @return 插入操作是否成功的布尔值
   */
  Boolean insertByBo(SysOssBo bo);

  /**
   * 文件下载方法，支持一次性下载完整文件
   *
   * @param ossId OSS对象ID
   * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
   */
  void download(Long ossId, HttpServletResponse response) throws IOException;

  /**
   * 删除OSS对象存储
   *
   * @param ids OSS对象ID串
   * @param isValid 判断是否需要校验
   * @return 结果
   */
  Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
