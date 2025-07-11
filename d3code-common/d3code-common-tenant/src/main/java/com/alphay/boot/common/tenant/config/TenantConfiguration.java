package com.alphay.boot.common.tenant.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.utils.reflect.ReflectUtils;
import com.alphay.boot.common.redis.config.RedisConfiguration;
import com.alphay.boot.common.redis.config.properties.RedissonProperties;
import com.alphay.boot.common.tenant.core.TenantSaTokenDao;
import com.alphay.boot.common.tenant.handle.PlusTenantLineHandler;
import com.alphay.boot.common.tenant.handle.TenantKeyPrefixHandler;
import com.alphay.boot.common.tenant.manager.TenantSpringCacheManager;
import com.alphay.boot.common.tenant.properties.TenantProperties;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 租户配置类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@EnableConfigurationProperties(TenantProperties.class)
@AutoConfiguration(after = {RedisConfiguration.class})
@ConditionalOnProperty(value = "tenant.enable", havingValue = "true")
public class TenantConfiguration {

  @Bean
  public RedissonAutoConfigurationCustomizer tenantRedissonCustomizer(
      RedissonProperties redissonProperties) {
    return config -> {
      TenantKeyPrefixHandler nameMapper =
          new TenantKeyPrefixHandler(redissonProperties.getKeyPrefix());
      SingleServerConfig singleServerConfig =
          ReflectUtils.invokeGetter(config, "singleServerConfig");
      if (ObjectUtil.isNotNull(singleServerConfig)) {
        // 使用单机模式
        // 设置多租户 redis key前缀
        singleServerConfig.setNameMapper(nameMapper);
      }
      ClusterServersConfig clusterServersConfig =
          ReflectUtils.invokeGetter(config, "clusterServersConfig");
      // 集群配置方式 参考下方注释
      if (ObjectUtil.isNotNull(clusterServersConfig)) {
        // 设置多租户 redis key前缀
        clusterServersConfig.setNameMapper(nameMapper);
      }
    };
  }

  /** 多租户缓存管理器 */
  @Primary
  @Bean
  public CacheManager tenantCacheManager() {
    return new TenantSpringCacheManager();
  }

  /** 多租户鉴权dao实现 */
  @Primary
  @Bean
  public SaTokenDao tenantSaTokenDao() {
    return new TenantSaTokenDao();
  }

  @ConditionalOnClass(TenantLineInnerInterceptor.class)
  @AutoConfiguration
  static class MybatisPlusConfig {

    /** 多租户插件 */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(
        TenantProperties tenantProperties) {
      return new TenantLineInnerInterceptor(new PlusTenantLineHandler(tenantProperties));
    }
  }
}
