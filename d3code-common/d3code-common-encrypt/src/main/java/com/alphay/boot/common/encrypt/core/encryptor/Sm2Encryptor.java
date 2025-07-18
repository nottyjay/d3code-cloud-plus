package com.alphay.boot.common.encrypt.core.encryptor;

import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.encrypt.core.EncryptContext;
import com.alphay.boot.common.encrypt.enumd.AlgorithmType;
import com.alphay.boot.common.encrypt.enumd.EncodeType;
import com.alphay.boot.common.encrypt.utils.EncryptUtils;

/**
 * sm2算法实现
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
public class Sm2Encryptor extends AbstractEncryptor {

  private final EncryptContext context;

  public Sm2Encryptor(EncryptContext context) {
    super(context);
    String privateKey = context.getPrivateKey();
    String publicKey = context.getPublicKey();
    if (StringUtils.isAnyEmpty(privateKey, publicKey)) {
      throw new IllegalArgumentException("SM2公私钥均需要提供，公钥加密，私钥解密。");
    }
    this.context = context;
  }

  /** 获得当前算法 */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.SM2;
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
      return EncryptUtils.encryptBySm2Hex(value, context.getPublicKey());
    } else {
      return EncryptUtils.encryptBySm2(value, context.getPublicKey());
    }
  }

  /**
   * 解密
   *
   * @param value 待加密字符串
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptBySm2(value, context.getPrivateKey());
  }
}
