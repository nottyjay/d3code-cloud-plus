package com.alphay.boot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 网关启动程序
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class D3codeGatewayApplication {
  public static void main(String[] args) {
    // 标记 sentinel 类型为 网关
    System.setProperty("csp.sentinel.app.type", "1");
    SpringApplication application = new SpringApplication(D3codeGatewayApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  网关启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
