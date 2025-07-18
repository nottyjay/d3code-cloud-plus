package com.alphay.boot.common.redis.handler;

import com.alphay.boot.common.core.utils.StringUtils;
import org.redisson.api.NameMapper;

/**
 * redis缓存key前缀处理
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2022/7/14 17:44
 * @since 1.0.0
 */
public class KeyPrefixHandler implements NameMapper {

  private final String keyPrefix;

  public KeyPrefixHandler(String keyPrefix) {
    // 前缀为空 则返回空前缀
    this.keyPrefix = StringUtils.isBlank(keyPrefix) ? "" : keyPrefix + ":";
  }

  /** 增加前缀 */
  @Override
  public String map(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    if (StringUtils.isNotBlank(keyPrefix) && !name.startsWith(keyPrefix)) {
      return keyPrefix + name;
    }
    return name;
  }

  /** 去除前缀 */
  @Override
  public String unmap(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    if (StringUtils.isNotBlank(keyPrefix) && name.startsWith(keyPrefix)) {
      return name.substring(keyPrefix.length());
    }
    return name;
  }
}
