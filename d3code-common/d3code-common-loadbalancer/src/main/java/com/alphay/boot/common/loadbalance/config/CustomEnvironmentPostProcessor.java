package com.alphay.boot.common.loadbalance.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * dubbo自定义负载均衡配置注入
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    System.setProperty("dubbo.consumer.loadbalance", "customDubboLoadBalancer");
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
