package com.alphay.boot.common.idempotent.config;

import com.alphay.boot.common.idempotent.aspectj.RepeatSubmitAspect;
import com.alphay.boot.common.redis.config.RedisConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 幂等功能配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration(after = RedisConfiguration.class)
public class IdempotentAutoConfiguration {

  @Bean
  public RepeatSubmitAspect repeatSubmitAspect() {
    return new RepeatSubmitAspect();
  }
}
