package com.alphay.boot.resource.controller;

import cn.hutool.core.util.RandomUtil;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.ratelimiter.annotation.RateLimiter;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.web.core.BaseController;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信功能
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sms")
public class SysSmsController extends BaseController {

  /**
   * 短信验证码
   *
   * @param phonenumber 用户手机号
   */
  @RateLimiter(key = "#phonenumber", time = 60, count = 1)
  @GetMapping("/code")
  public R<Void> smsCaptcha(
      @NotBlank(message = "{user.phonenumber.not.blank}") String phonenumber) {
    String key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber;
    String code = RandomUtil.randomNumbers(4);
    RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
    // 验证码模板id 自行处理 (查数据库或写死均可)
    String templateId = "";
    LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
    map.put("code", code);
    SmsBlend smsBlend = SmsFactory.getSmsBlend("tx1");
    SmsResponse smsResponse = smsBlend.sendMessage(phonenumber, templateId, map);
    if (!smsResponse.isSuccess()) {
      log.error("验证码短信发送异常 => {}", smsResponse);
      return R.fail(smsResponse.getData().toString());
    }
    return R.ok();
  }
}
