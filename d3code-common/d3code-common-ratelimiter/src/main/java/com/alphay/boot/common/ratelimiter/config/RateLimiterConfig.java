package com.alphay.boot.common.ratelimiter.config;

import com.alphay.boot.common.ratelimiter.aspectj.RateLimiterAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * @author Nottyjay
 * @since 1.0.0
 * @date 2023/1/18
 */
@AutoConfiguration(after = RedisConfiguration.class)
public class RateLimiterConfig {

  @Bean
  public RateLimiterAspect rateLimiterAspect() {
    return new RateLimiterAspect();
  }
}
