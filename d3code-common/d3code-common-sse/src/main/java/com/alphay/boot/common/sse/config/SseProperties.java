package com.alphay.boot.common.sse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSE 配置项
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ConfigurationProperties("sse")
public class SseProperties {

  private Boolean enabled;

  /** 路径 */
  private String path;
}
