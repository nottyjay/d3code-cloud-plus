package com.alphay.boot.auth.domain.vo;

import lombok.Data;

/**
 * 验证码信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class CaptchaVo {

  /** 是否开启验证码 */
  private Boolean captchaEnabled = true;

  private String uuid;

  /** 验证码图片 */
  private String img;
}
