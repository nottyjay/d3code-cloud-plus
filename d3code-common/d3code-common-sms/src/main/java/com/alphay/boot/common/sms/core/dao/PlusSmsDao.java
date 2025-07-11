package com.alphay.boot.common.sms.core.dao;

import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.redis.utils.RedisUtils;
import java.time.Duration;
import org.dromara.sms4j.api.dao.SmsDao;

/**
 * SmsDao缓存配置 (使用框架自带RedisUtils实现 协议统一)
 *
 * <p>主要用于短信重试和拦截的缓存
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class PlusSmsDao implements SmsDao {

  /**
   * 存储
   *
   * @param key 键
   * @param value 值
   * @param cacheTime 缓存时间（单位：秒)
   */
  @Override
  public void set(String key, Object value, long cacheTime) {
    RedisUtils.setCacheObject(
        GlobalConstants.GLOBAL_REDIS_KEY + key, value, Duration.ofSeconds(cacheTime));
  }

  /**
   * 存储
   *
   * @param key 键
   * @param value 值
   */
  @Override
  public void set(String key, Object value) {
    RedisUtils.setCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, true);
  }

  /**
   * 读取
   *
   * @param key 键
   * @return 值
   */
  @Override
  public Object get(String key) {
    return RedisUtils.getCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key);
  }

  /**
   * remove
   *
   * <p>根据key移除缓存
   *
   * @param key 缓存键
   * @return 被删除的value
   * @author Nottyjay
   * @since 1.0.0
   */
  @Override
  public Object remove(String key) {
    return RedisUtils.deleteObject(GlobalConstants.GLOBAL_REDIS_KEY + key);
  }

  /** 清空 */
  @Override
  public void clean() {
    RedisUtils.deleteKeys(GlobalConstants.GLOBAL_REDIS_KEY + "sms:*");
  }
}
