package com.alphay.boot.system;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 系统模块
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@EnableDubbo
@SpringBootApplication
public class D3codeSystemApplication {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(D3codeSystemApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
