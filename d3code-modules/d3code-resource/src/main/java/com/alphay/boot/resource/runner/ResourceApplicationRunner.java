package com.alphay.boot.resource.runner;

import com.alphay.boot.resource.service.ISysOssConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化 system 模块对应业务数据
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Component
public class ResourceApplicationRunner implements ApplicationRunner {

  @Resource private ISysOssConfigService ossConfigService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    ossConfigService.init();
    log.info("初始化OSS配置成功");
  }
}
