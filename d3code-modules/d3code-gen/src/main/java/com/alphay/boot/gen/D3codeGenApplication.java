package com.alphay.boot.gen;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 代码生成
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@EnableDubbo
@SpringBootApplication
public class D3codeGenApplication {
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(D3codeGenApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  代码生成模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
