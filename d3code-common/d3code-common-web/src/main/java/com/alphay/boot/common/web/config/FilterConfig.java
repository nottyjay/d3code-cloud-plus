package com.alphay.boot.common.web.config;

import com.alphay.boot.common.web.config.properties.XssProperties;
import com.alphay.boot.common.web.filter.XssFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Filter配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(XssProperties.class)
public class FilterConfig {

  @Bean
  @ConditionalOnProperty(value = "xss.enabled", havingValue = "true")
  public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
    FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
    registration.setDispatcherTypes(DispatcherType.REQUEST);
    registration.setFilter(new XssFilter());
    registration.addUrlPatterns("/*");
    registration.setName("xssFilter");
    registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE + 1);
    return registration;
  }
}
