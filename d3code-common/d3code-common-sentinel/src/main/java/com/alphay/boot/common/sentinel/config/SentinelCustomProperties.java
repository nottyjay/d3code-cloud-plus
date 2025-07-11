package com.alphay.boot.common.sentinel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sentinel自定义配置类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.sentinel.transport")
public class SentinelCustomProperties {

  private String serverName;
}
