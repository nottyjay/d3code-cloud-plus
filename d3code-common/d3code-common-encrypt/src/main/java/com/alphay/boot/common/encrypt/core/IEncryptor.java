package com.alphay.boot.common.encrypt.core;

import com.alphay.boot.common.encrypt.enumd.AlgorithmType;
import com.alphay.boot.common.encrypt.enumd.EncodeType;

/**
 * 加解者
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
public interface IEncryptor {

  /** 获得当前算法 */
  AlgorithmType algorithm();

  /**
   * 加密
   *
   * @param value 待加密字符串
   * @param encodeType 加密后的编码格式
   * @return 加密后的字符串
   */
  String encrypt(String value, EncodeType encodeType);

  /**
   * 解密
   *
   * @param value 待加密字符串
   * @return 解密后的字符串
   */
  String decrypt(String value);
}
