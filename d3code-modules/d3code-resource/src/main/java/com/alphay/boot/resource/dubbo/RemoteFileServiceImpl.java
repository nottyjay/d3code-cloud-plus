package com.alphay.boot.resource.dubbo;

import cn.hutool.core.convert.Convert;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.oss.core.OssClient;
import com.alphay.boot.common.oss.entity.UploadResult;
import com.alphay.boot.common.oss.factory.OssFactory;
import com.alphay.boot.resource.api.RemoteFileService;
import com.alphay.boot.resource.api.domain.RemoteFile;
import com.alphay.boot.resource.domain.bo.SysOssBo;
import com.alphay.boot.resource.domain.vo.SysOssVo;
import com.alphay.boot.resource.service.ISysOssService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件请求处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DubboService
public class RemoteFileServiceImpl implements RemoteFileService {

  private final ISysOssService sysOssService;

  /** 文件上传请求 */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public RemoteFile upload(String name, String originalFilename, String contentType, byte[] file)
      throws ServiceException {
    try {
      String suffix =
          StringUtils.substring(
              originalFilename, originalFilename.lastIndexOf("."), originalFilename.length());
      OssClient storage = OssFactory.instance();
      UploadResult uploadResult = storage.uploadSuffix(file, suffix, contentType);
      // 保存文件信息
      SysOssBo oss = new SysOssBo();
      oss.setUrl(uploadResult.getUrl());
      oss.setFileSuffix(suffix);
      oss.setFileName(uploadResult.getFilename());
      oss.setOriginalName(originalFilename);
      oss.setService(storage.getConfigKey());
      sysOssService.insertByBo(oss);
      RemoteFile sysFile = new RemoteFile();
      sysFile.setOssId(oss.getOssId());
      sysFile.setName(uploadResult.getFilename());
      sysFile.setUrl(uploadResult.getUrl());
      sysFile.setOriginalName(originalFilename);
      sysFile.setFileSuffix(suffix);
      return sysFile;
    } catch (Exception e) {
      log.error("上传文件失败", e);
      throw new ServiceException("上传文件失败");
    }
  }

  /**
   * 通过ossId查询对应的url
   *
   * @param ossIds ossId串逗号分隔
   * @return url串逗号分隔
   */
  @Override
  public String selectUrlByIds(String ossIds) {
    return sysOssService.selectUrlByIds(ossIds);
  }

  /**
   * 通过ossId查询列表
   *
   * @param ossIds ossId串逗号分隔
   * @return 列表
   */
  @Override
  public List<RemoteFile> selectByIds(String ossIds) {
    List<SysOssVo> sysOssVos =
        sysOssService.queryByIds(StringUtils.splitTo(ossIds, Convert::toLong));
    return MapstructUtils.convert(sysOssVos, RemoteFile.class);
  }
}
