package com.alphay.boot.resource.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 上传对象信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysOssUploadVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** URL地址 */
  private String url;

  /** 文件名 */
  private String fileName;

  /** 对象存储主键 */
  private String ossId;
}
