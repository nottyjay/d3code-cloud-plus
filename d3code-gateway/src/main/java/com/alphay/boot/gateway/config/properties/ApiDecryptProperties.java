package com.alphay.boot.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * api解密属性配置类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "api-decrypt")
public class ApiDecryptProperties {

  /** 加密开关 */
  private Boolean enabled;

  /** 头部标识 */
  private String headerFlag;
}
