package com.alphay.boot.system.api;

import com.alphay.boot.system.api.domain.vo.RemoteTenantVo;
import java.util.List;

/**
 * 租户服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteTenantService {

  /**
   * 根据租户id获取租户详情
   *
   * @param tenantId 租户id
   * @return 结果
   */
  RemoteTenantVo queryByTenantId(String tenantId);

  /**
   * 获取租户列表
   *
   * @return 结果
   */
  List<RemoteTenantVo> queryList();
}
