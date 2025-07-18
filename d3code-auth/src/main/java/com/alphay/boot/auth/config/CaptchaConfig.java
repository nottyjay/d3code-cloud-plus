package com.alphay.boot.auth.config;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import java.awt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 验证码配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Configuration
public class CaptchaConfig {

  private static final int WIDTH = 160;
  private static final int HEIGHT = 60;
  private static final Color BACKGROUND = Color.LIGHT_GRAY;
  private static final Font FONT = new Font("Arial", Font.BOLD, 48);

  /** 圆圈干扰验证码 */
  @Lazy
  @Bean
  public CircleCaptcha circleCaptcha() {
    CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }

  /** 线段干扰的验证码 */
  @Lazy
  @Bean
  public LineCaptcha lineCaptcha() {
    LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }

  /** 扭曲干扰验证码 */
  @Lazy
  @Bean
  public ShearCaptcha shearCaptcha() {
    ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }
}
