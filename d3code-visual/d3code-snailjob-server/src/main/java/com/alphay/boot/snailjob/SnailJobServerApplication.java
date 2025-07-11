package com.alphay.boot.snailjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SnailJob Server 启动程序
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2024-05-17
 */
@SpringBootApplication
public class SnailJobServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(com.aizuda.snailjob.server.SnailJobServerApplication.class, args);
  }
}
