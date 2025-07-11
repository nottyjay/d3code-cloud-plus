package com.alphay.boot.common.encrypt.core.encryptor;

import com.alphay.boot.common.encrypt.core.EncryptContext;
import com.alphay.boot.common.encrypt.core.IEncryptor;

/**
 * 所有加密执行者的基类
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
public abstract class AbstractEncryptor implements IEncryptor {

  public AbstractEncryptor(EncryptContext context) {
    // 用户配置校验与配置注入
  }
}
