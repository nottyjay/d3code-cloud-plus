package com.alphay.boot.common.dubbo.config;

import com.alphay.boot.common.core.factory.YmlPropertySourceFactory;
import com.alphay.boot.common.dubbo.handler.DubboExceptionHandler;
import com.alphay.boot.common.dubbo.properties.DubboCustomProperties;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/** dubbo 配置类 */
@AutoConfiguration
@EnableConfigurationProperties(DubboCustomProperties.class)
@PropertySource(value = "classpath:common-dubbo.yml", factory = YmlPropertySourceFactory.class)
public class DubboConfiguration {

  /** dubbo自定义IP注入(避免IP不正确问题) */
  @Bean
  public BeanFactoryPostProcessor customBeanFactoryPostProcessor() {
    return new CustomBeanFactoryPostProcessor();
  }

  /** 异常处理器 */
  @Bean
  public DubboExceptionHandler dubboExceptionHandler() {
    return new DubboExceptionHandler();
  }
}
