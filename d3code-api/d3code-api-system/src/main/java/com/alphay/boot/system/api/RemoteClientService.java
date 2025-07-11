package com.alphay.boot.system.api;

import com.alphay.boot.system.api.domain.vo.RemoteClientVo;

/**
 * 客户端服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteClientService {

  /**
   * 根据客户端id获取客户端详情
   *
   * @param clientId 客户端id
   * @return 客户端对象
   */
  RemoteClientVo queryByClientId(String clientId);
}
