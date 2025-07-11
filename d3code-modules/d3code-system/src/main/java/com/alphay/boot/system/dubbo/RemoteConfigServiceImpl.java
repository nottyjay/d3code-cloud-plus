package com.alphay.boot.system.dubbo;

import com.alphay.boot.system.api.RemoteConfigService;
import com.alphay.boot.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 配置服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteConfigServiceImpl implements RemoteConfigService {

  private final ISysConfigService configService;

  /** 获取注册开关 */
  @Override
  public boolean selectRegisterEnabled(String tenantId) {
    return configService.selectRegisterEnabled(tenantId);
  }
}
