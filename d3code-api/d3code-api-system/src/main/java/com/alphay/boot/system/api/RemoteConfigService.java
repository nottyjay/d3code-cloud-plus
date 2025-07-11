package com.alphay.boot.system.api;

/**
 * 配置服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteConfigService {

  /**
   * 获取注册开关
   *
   * @param tenantId 租户id
   * @return true开启，false关闭
   */
  boolean selectRegisterEnabled(String tenantId);
}
