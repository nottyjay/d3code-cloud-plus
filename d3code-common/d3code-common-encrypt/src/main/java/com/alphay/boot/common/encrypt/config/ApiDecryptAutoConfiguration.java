package com.alphay.boot.common.encrypt.config;

import com.alphay.boot.common.encrypt.filter.CryptoFilter;
import com.alphay.boot.common.encrypt.properties.ApiDecryptProperties;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * api 解密自动配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiDecryptProperties.class)
@ConditionalOnProperty(value = "api-decrypt.enabled", havingValue = "true")
public class ApiDecryptAutoConfiguration {

  @Bean
  public FilterRegistrationBean<CryptoFilter> cryptoFilterRegistration(
      ApiDecryptProperties properties) {
    FilterRegistrationBean<CryptoFilter> registration = new FilterRegistrationBean<>();
    registration.setDispatcherTypes(DispatcherType.REQUEST);
    registration.setFilter(new CryptoFilter(properties));
    registration.addUrlPatterns("/*");
    registration.setName("cryptoFilter");
    registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
    return registration;
  }
}
