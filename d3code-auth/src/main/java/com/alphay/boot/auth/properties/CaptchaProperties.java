package com.alphay.boot.auth.properties;

import com.alphay.boot.auth.enums.CaptchaCategory;
import com.alphay.boot.auth.enums.CaptchaType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 验证码配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.captcha")
public class CaptchaProperties {

  /** 验证码类型 */
  private CaptchaType type;

  /** 验证码类别 */
  private CaptchaCategory category;

  /** 数字验证码位数 */
  private Integer numberLength;

  /** 字符验证码长度 */
  private Integer charLength;

  /** 验证码开关 */
  private Boolean enabled;
}
