package com.alphay.boot.common.dubbo.properties;

import com.alphay.boot.common.dubbo.enumd.RequestLogEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 自定义配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "dubbo.custom")
public class DubboCustomProperties {

  /** 是否开启请求日志记录 */
  private Boolean requestLog;

  /** 日志级别 */
  private RequestLogEnum logLevel;
}
