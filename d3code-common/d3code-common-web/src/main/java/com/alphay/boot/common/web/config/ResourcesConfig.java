package com.alphay.boot.common.web.config;

import cn.hutool.core.date.DateUtil;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.web.handler.GlobalExceptionHandler;
import java.util.Date;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
public class ResourcesConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {}

  @Override
  public void addFormatters(FormatterRegistry registry) {
    // 全局日期格式转换配置
    registry.addConverter(
        String.class,
        Date.class,
        source -> {
          if (StringUtils.isBlank(source)) {
            return null;
          }
          return DateUtil.parse(source);
        });
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {}

  /** 全局异常处理器 */
  @Bean
  public GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }
}
