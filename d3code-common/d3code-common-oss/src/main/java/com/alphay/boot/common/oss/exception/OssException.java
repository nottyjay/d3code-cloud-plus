package com.alphay.boot.common.oss.exception;

import java.io.Serial;

/**
 * OSS异常类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class OssException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public OssException(String msg) {
    super(msg);
  }
}
