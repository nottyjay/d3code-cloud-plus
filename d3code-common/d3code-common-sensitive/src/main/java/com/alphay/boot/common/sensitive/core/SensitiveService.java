package com.alphay.boot.common.sensitive.core;

/**
 * 脱敏服务 默认管理员不过滤 需自行根据业务重写实现
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 3.6.0
 */
public interface SensitiveService {

  /** 是否脱敏 */
  boolean isSensitive(String[] roleKey, String[] perms);
}
