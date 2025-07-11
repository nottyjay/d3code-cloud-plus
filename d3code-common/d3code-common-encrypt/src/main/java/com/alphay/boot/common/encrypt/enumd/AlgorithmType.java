package com.alphay.boot.common.encrypt.enumd;

import com.alphay.boot.common.encrypt.core.encryptor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 算法名称
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

  /** 默认走yml配置 */
  DEFAULT(null),

  /** base64 */
  BASE64(Base64Encryptor.class),

  /** aes */
  AES(AesEncryptor.class),

  /** rsa */
  RSA(RsaEncryptor.class),

  /** sm2 */
  SM2(Sm2Encryptor.class),

  /** sm4 */
  SM4(Sm4Encryptor.class);

  private final Class<? extends AbstractEncryptor> clazz;
}
