package com.alphay.boot.common.redis.config;

import com.alphay.boot.common.redis.manager.PlusSpringCacheManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * 缓存配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@EnableCaching
public class CacheConfiguration {

  /** caffeine 本地缓存处理器 */
  @Bean
  public Cache<Object, Object> caffeine() {
    return Caffeine.newBuilder()
        // 设置最后一次写入或访问后经过固定时间过期
        .expireAfterWrite(30, TimeUnit.SECONDS)
        // 初始的缓存空间大小
        .initialCapacity(100)
        // 缓存的最大条数
        .maximumSize(1000)
        .build();
  }

  /** 自定义缓存管理器 整合spring-cache */
  @Bean
  public CacheManager cacheManager() {
    return new PlusSpringCacheManager();
  }
}
