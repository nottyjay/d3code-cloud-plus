package com.alphay.boot.modules.monitor.config;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import java.util.concurrent.Executor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * springboot-admin server配置类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Configuration
@EnableAdminServer
public class AdminServerConfig {

  @Lazy
  @Bean(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
  @ConditionalOnMissingBean(Executor.class)
  public ThreadPoolTaskExecutor applicationTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
    return builder.build();
  }
}
