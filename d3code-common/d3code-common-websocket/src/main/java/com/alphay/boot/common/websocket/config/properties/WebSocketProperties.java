package com.alphay.boot.common.websocket.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket 配置项
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@ConfigurationProperties("websocket")
@Data
public class WebSocketProperties {

  private Boolean enabled;

  /** 路径 */
  private String path;

  /** 设置访问源地址 */
  private String allowedOrigins;
}
