package com.alphay.boot.common.encrypt.annotation;

import com.alphay.boot.common.encrypt.enumd.AlgorithmType;
import com.alphay.boot.common.encrypt.enumd.EncodeType;
import java.lang.annotation.*;

/**
 * 字段加密注解
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

  /** 加密算法 */
  AlgorithmType algorithm() default AlgorithmType.DEFAULT;

  /** 秘钥。AES、SM4需要 */
  String password() default "";

  /** 公钥。RSA、SM2需要 */
  String publicKey() default "";

  /** 私钥。RSA、SM2需要 */
  String privateKey() default "";

  /** 编码方式。对加密算法为BASE64的不起作用 */
  EncodeType encode() default EncodeType.DEFAULT;
}
