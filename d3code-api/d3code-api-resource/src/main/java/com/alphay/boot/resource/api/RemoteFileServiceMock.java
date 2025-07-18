package com.alphay.boot.resource.api;

import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.resource.api.domain.RemoteFile;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件服务(降级处理)
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
public class RemoteFileServiceMock implements RemoteFileService {

  /**
   * 上传文件
   *
   * @param file 文件信息
   * @return 结果
   */
  @Override
  public RemoteFile upload(String name, String originalFilename, String contentType, byte[] file) {
    log.warn("服务调用异常 -> 降级处理");
    return null;
  }

  /**
   * 通过ossId查询对应的url
   *
   * @param ossIds ossId串逗号分隔
   * @return url串逗号分隔
   */
  @Override
  public String selectUrlByIds(String ossIds) {
    log.warn("服务调用异常 -> 降级处理");
    return StringUtils.EMPTY;
  }

  /**
   * 通过ossId查询列表
   *
   * @param ossIds ossId串逗号分隔
   * @return 列表
   */
  @Override
  public List<RemoteFile> selectByIds(String ossIds) {
    log.warn("服务调用异常 -> 降级处理");
    return List.of();
  }
}
