package com.alphay.boot.common.tenant.manager;

import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.redis.manager.PlusSpringCacheManager;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
public class TenantSpringCacheManager extends PlusSpringCacheManager {

  public TenantSpringCacheManager() {}

  @Override
  public Cache getCache(String name) {
    if (InterceptorIgnoreHelper.willIgnoreTenantLine("")) {
      return super.getCache(name);
    }
    if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
      return super.getCache(name);
    }
    String tenantId = TenantHelper.getTenantId();
    if (StringUtils.isBlank(tenantId)) {
      log.error("无法获取有效的租户id -> Null");
    }
    if (StringUtils.startsWith(name, tenantId)) {
      // 如果存在则直接返回
      return super.getCache(name);
    }
    return super.getCache(tenantId + ":" + name);
  }
}
