package com.alphay.boot.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义gateway参数配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "spring.cloud.gateway")
public class CustomGatewayProperties {

  /** 请求日志 */
  private Boolean requestLog;
}
