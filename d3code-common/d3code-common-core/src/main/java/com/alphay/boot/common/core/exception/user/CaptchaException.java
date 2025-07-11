package com.alphay.boot.common.core.exception.user;

import java.io.Serial;

/**
 * 验证码错误异常类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class CaptchaException extends UserException {
  @Serial private static final long serialVersionUID = 1L;

  public CaptchaException() {
    super("user.jcaptcha.error");
  }

  public CaptchaException(String msg) {
    super(msg);
  }
}
