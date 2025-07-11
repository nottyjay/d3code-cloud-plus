package com.alphay.boot.resource.api;

import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.resource.api.domain.RemoteFile;
import java.util.List;

/**
 * 文件服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteFileService {

  /**
   * 上传文件
   *
   * @param file 文件信息
   * @return 结果
   */
  RemoteFile upload(String name, String originalFilename, String contentType, byte[] file)
      throws ServiceException;

  /**
   * 通过ossId查询对应的url
   *
   * @param ossIds ossId串逗号分隔
   * @return url串逗号分隔
   */
  String selectUrlByIds(String ossIds);

  /**
   * 通过ossId查询列表
   *
   * @param ossIds ossId串逗号分隔
   * @return 列表
   */
  List<RemoteFile> selectByIds(String ossIds);
}
