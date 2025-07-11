package com.alphay.boot.common.sms.config;

import com.alphay.boot.common.redis.config.RedisConfiguration;
import com.alphay.boot.common.sms.core.dao.PlusSmsDao;
import com.alphay.boot.common.sms.handler.SmsExceptionHandler;
import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 短信配置类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration(after = {RedisConfiguration.class})
public class SmsAutoConfiguration {

  @Primary
  @Bean
  public SmsDao smsDao() {
    return new PlusSmsDao();
  }

  /** 异常处理器 */
  @Bean
  public SmsExceptionHandler smsExceptionHandler() {
    return new SmsExceptionHandler();
  }
}
