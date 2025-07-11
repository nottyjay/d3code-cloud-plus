package com.alphay.boot.common.social.config.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Social 配置属性
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "justauth")
public class SocialProperties {

  /** 授权类型 */
  private Map<String, SocialLoginConfigProperties> type;
}
