package com.alphay.boot.common.encrypt.core.encryptor;

import com.alphay.boot.common.encrypt.core.EncryptContext;
import com.alphay.boot.common.encrypt.enumd.AlgorithmType;
import com.alphay.boot.common.encrypt.enumd.EncodeType;
import com.alphay.boot.common.encrypt.utils.EncryptUtils;

/**
 * sm4算法实现
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
public class Sm4Encryptor extends AbstractEncryptor {

  private final EncryptContext context;

  public Sm4Encryptor(EncryptContext context) {
    super(context);
    this.context = context;
  }

  /** 获得当前算法 */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.SM4;
  }

  /**
   * 加密
   *
   * @param value 待加密字符串
   * @param encodeType 加密后的编码格式
   */
  @Override
  public String encrypt(String value, EncodeType encodeType) {
    if (encodeType == EncodeType.HEX) {
      return EncryptUtils.encryptBySm4Hex(value, context.getPassword());
    } else {
      return EncryptUtils.encryptBySm4(value, context.getPassword());
    }
  }

  /**
   * 解密
   *
   * @param value 待加密字符串
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptBySm4(value, context.getPassword());
  }
}
