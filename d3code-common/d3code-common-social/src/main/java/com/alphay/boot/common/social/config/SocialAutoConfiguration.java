package com.alphay.boot.common.social.config;

import com.alphay.boot.common.social.config.properties.SocialProperties;
import com.alphay.boot.common.social.utils.AuthRedisStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Social 配置属性
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SocialProperties.class)
public class SocialAutoConfiguration {

  @Bean
  public AuthStateCache authStateCache() {
    return new AuthRedisStateCache();
  }
}
