package com.alphay.boot.resource.controller;

import cn.hutool.core.util.RandomUtil;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.mail.config.properties.MailProperties;
import com.alphay.boot.common.mail.utils.MailUtils;
import com.alphay.boot.common.ratelimiter.annotation.RateLimiter;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.web.core.BaseController;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件功能
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/email")
public class SysEmailController extends BaseController {

  @Resource private MailProperties mailProperties;

  /**
   * 邮箱验证码
   *
   * @param email 邮箱
   */
  @GetMapping("/code")
  public R<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") String email) {
    if (!mailProperties.getEnabled()) {
      return R.fail("当前系统没有开启邮箱功能！");
    }
    SpringUtils.getAopProxy(this).emailCodeImpl(email);
    return R.ok();
  }

  /** 邮箱验证码 独立方法避免验证码关闭之后仍然走限流 */
  @RateLimiter(key = "#email", time = 60, count = 1)
  public void emailCodeImpl(String email) {
    String key = GlobalConstants.CAPTCHA_CODE_KEY + email;
    String code = RandomUtil.randomNumbers(4);
    RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
    try {
      MailUtils.sendText(
          email, "登录验证码", "您本次验证码为：" + code + "，有效性为" + Constants.CAPTCHA_EXPIRATION + "分钟，请尽快填写。");
    } catch (Exception e) {
      log.error("验证码短信发送异常 => {}", e.getMessage());
      throw new ServiceException(e.getMessage());
    }
  }
}
