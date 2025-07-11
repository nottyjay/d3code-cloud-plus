package com.alphay.boot.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.file.FileUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.oss.core.OssClient;
import com.alphay.boot.common.oss.entity.UploadResult;
import com.alphay.boot.common.oss.enums.AccessPolicyType;
import com.alphay.boot.common.oss.factory.OssFactory;
import com.alphay.boot.resource.api.domain.param.SysOssQueryParam;
import com.alphay.boot.resource.domain.SysOss;
import com.alphay.boot.resource.domain.bo.SysOssBo;
import com.alphay.boot.resource.domain.vo.SysOssVo;
import com.alphay.boot.resource.mapper.SysOssMapper;
import com.alphay.boot.resource.service.ISysOssService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传 服务层实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysOssServiceImpl extends ServiceImplX<SysOssMapper, SysOss, SysOssVo>
    implements ISysOssService {

  /**
   * 查询OSS对象存储列表
   *
   * @param param OSS对象存储分页查询对象
   * @return 结果
   */
  @Override
  public PageResult<SysOssVo> queryPageList(SysOssQueryParam param) {
    PageResult<SysOssVo> result = listPageVo(param, buildQueryWrapper(param));
    List<SysOssVo> filterResult =
        result.getRows().stream().map(this::matchingUrl).collect(Collectors.toList());
    result.setRows(filterResult);
    return result;
  }

  /**
   * 根据一组 ossIds 获取对应的 SysOssVo 列表
   *
   * @param ossIds 一组文件在数据库中的唯一标识集合
   * @return 包含 SysOssVo 对象的列表
   */
  @Override
  public List<SysOssVo> queryByIds(Collection<Long> ossIds) {
    List<SysOssVo> list = new ArrayList<>();
    ISysOssService ossService = SpringUtils.getAopProxy(this);
    for (Long id : ossIds) {
      SysOssVo vo = ossService.getVoById(id);
      if (ObjectUtil.isNotNull(vo)) {
        try {
          list.add(this.matchingUrl(vo));
        } catch (Exception ignored) {
          // 如果oss异常无法连接则将数据直接返回
          list.add(vo);
        }
      }
    }
    return list;
  }

  /**
   * 根据一组 ossIds 获取对应文件的 URL 列表
   *
   * @param ossIds 以逗号分隔的 ossId 字符串
   * @return 以逗号分隔的文件 URL 字符串
   */
  @Override
  public String selectUrlByIds(String ossIds) {
    List<String> list = new ArrayList<>();
    ISysOssService ossService = SpringUtils.getAopProxy(this);
    for (Long id : StringUtils.splitTo(ossIds, Convert::toLong)) {
      SysOssVo vo = ossService.getVoById(id);
      if (ObjectUtil.isNotNull(vo)) {
        try {
          list.add(this.matchingUrl(vo).getUrl());
        } catch (Exception ignored) {
          // 如果oss异常无法连接则将数据直接返回
          list.add(vo.getUrl());
        }
      }
    }
    return String.join(StringUtils.SEPARATOR, list);
  }

  private LambdaQueryWrapper<SysOss> buildQueryWrapper(SysOssQueryParam param) {
    LambdaQueryWrapper<SysOss> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysOss::getFileName, param.getFileName())
            .likeIfPresent(SysOss::getOriginalName, param.getOriginalName())
            .eqIfPresent(SysOss::getFileSuffix, param.getFileSuffix())
            .eqIfPresent(SysOss::getUrl, param.getUrl())
            .betweenIfPresent(SysOss::getCreateTime, param.getCreateTime())
            .eqIfPresent(SysOss::getCreateBy, param.getCreateBy())
            .eqIfPresent(SysOss::getService, param.getService());
    lqw.orderByAsc(SysOss::getOssId);
    return lqw;
  }

  /**
   * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
   *
   * @param ossId 文件在数据库中的唯一标识
   * @return SysOssVo 对象，包含文件信息
   */
  @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
  @Override
  public SysOssVo getVoById(Serializable ossId) {
    return super.getVoById(ossId);
  }

  /**
   * 文件下载方法，支持一次性下载完整文件
   *
   * @param ossId OSS对象ID
   * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
   */
  @Override
  public void download(Long ossId, HttpServletResponse response) throws IOException {
    SysOssVo sysOss = SpringUtils.getAopProxy(this).getVoById(ossId);
    if (ObjectUtil.isNull(sysOss)) {
      throw new ServiceException("文件数据不存在!");
    }
    FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
    OssClient storage = OssFactory.instance(sysOss.getService());
    storage.download(
        sysOss.getFileName(), response.getOutputStream(), response::setContentLengthLong);
  }

  /**
   * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
   *
   * @param file 要上传的 MultipartFile 对象
   * @return 上传成功后的 SysOssVo 对象，包含文件信息
   * @throws ServiceException 如果上传过程中发生异常，则抛出 ServiceException 异常
   */
  @Override
  public SysOssVo upload(MultipartFile file) {
    String originalfileName = file.getOriginalFilename();
    String suffix =
        StringUtils.substring(
            originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
    OssClient storage = OssFactory.instance();
    UploadResult uploadResult;
    try {
      uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
    } catch (IOException e) {
      throw new ServiceException(e.getMessage());
    }
    // 保存文件信息
    return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult);
  }

  /**
   * 上传文件到对象存储服务，并保存文件信息到数据库
   *
   * @param file 要上传的文件对象
   * @return 上传成功后的 SysOssVo 对象，包含文件信息
   */
  @Override
  public SysOssVo upload(File file) {
    String originalfileName = file.getName();
    String suffix =
        StringUtils.substring(
            originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
    OssClient storage = OssFactory.instance();
    UploadResult uploadResult = storage.uploadSuffix(file, suffix);
    // 保存文件信息
    return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult);
  }

  private SysOssVo buildResultEntity(
      String originalfileName, String suffix, String configKey, UploadResult uploadResult) {
    SysOss oss = new SysOss();
    oss.setUrl(uploadResult.getUrl());
    oss.setFileSuffix(suffix);
    oss.setFileName(uploadResult.getFilename());
    oss.setOriginalName(originalfileName);
    oss.setService(configKey);
    baseMapper.insert(oss);
    SysOssVo sysOssVo = MapstructUtils.convert(oss, SysOssVo.class);
    return this.matchingUrl(sysOssVo);
  }

  /**
   * 新增OSS对象存储
   *
   * @param bo SysOssBo 对象，包含待插入的数据
   * @return 插入操作是否成功的布尔值
   */
  @Override
  public Boolean insertByBo(SysOssBo bo) {
    SysOss oss = BeanUtil.toBean(bo, SysOss.class);
    boolean flag = baseMapper.insert(oss) > 0;
    if (flag) {
      bo.setOssId(oss.getOssId());
    }
    return flag;
  }

  /**
   * 删除OSS对象存储
   *
   * @param ids OSS对象ID串
   * @param isValid 判断是否需要校验
   * @return 结果
   */
  @Override
  public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
    if (isValid) {
      // 做一些业务上的校验,判断是否需要校验
    }
    List<SysOss> list = baseMapper.selectByIds(ids);
    for (SysOss sysOss : list) {
      OssClient storage = OssFactory.instance(sysOss.getService());
      storage.delete(sysOss.getUrl());
    }
    return baseMapper.deleteByIds(ids) > 0;
  }

  /**
   * 桶类型为 private 的URL 修改为临时URL时长为120s
   *
   * @param oss OSS对象
   * @return oss 匹配Url的OSS对象
   */
  private SysOssVo matchingUrl(SysOssVo oss) {
    OssClient storage = OssFactory.instance(oss.getService());
    // 仅修改桶类型为 private 的URL，临时URL时长为120s
    if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
      oss.setUrl(storage.getPrivateUrl(oss.getFileName(), Duration.ofSeconds(120)));
    }
    return oss;
  }
}
