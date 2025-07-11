package com.alphay.boot.common.prometheus.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * prometheus 配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
public class PrometheusConfiguration {

  @Bean
  public MeterRegistryCustomizer<MeterRegistry> configurer(
      @Value("${spring.application.name}") String applicationName) {
    return (registry) -> registry.config().commonTags("application", applicationName);
  }
}
