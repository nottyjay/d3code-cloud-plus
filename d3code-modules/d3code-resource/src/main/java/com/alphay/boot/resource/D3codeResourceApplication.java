package com.alphay.boot.resource;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 资源服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@EnableDubbo
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class D3codeResourceApplication {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(D3codeResourceApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  资源服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
