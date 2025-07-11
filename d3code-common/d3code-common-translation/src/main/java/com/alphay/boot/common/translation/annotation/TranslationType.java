package com.alphay.boot.common.translation.annotation;

import com.alphay.boot.common.translation.core.TranslationInterface;
import java.lang.annotation.*;

/**
 * 翻译类型注解 (标注到{@link TranslationInterface} 的实现类)
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface TranslationType {

  /** 类型 */
  String type();
}
