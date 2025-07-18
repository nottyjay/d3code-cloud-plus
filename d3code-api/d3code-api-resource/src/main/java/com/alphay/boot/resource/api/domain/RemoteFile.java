package com.alphay.boot.resource.api.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 文件信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class RemoteFile implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** oss主键 */
  private Long ossId;

  /** 文件名称 */
  private String name;

  /** 文件地址 */
  private String url;

  /** 原名 */
  private String originalName;

  /** 文件后缀名 */
  private String fileSuffix;
}
