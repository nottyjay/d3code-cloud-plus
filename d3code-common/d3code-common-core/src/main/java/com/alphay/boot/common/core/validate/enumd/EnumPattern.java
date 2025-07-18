package com.alphay.boot.common.core.validate.enumd;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自定义枚举校验
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2024-12-09
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(EnumPattern.List.class) // 允许在同一元素上多次使用该注解
@Constraint(validatedBy = {EnumPatternValidator.class})
public @interface EnumPattern {

  /** 需要校验的枚举类型 */
  Class<? extends Enum<?>> type();

  /** 枚举类型校验值字段名称 需确保该字段实现了 getter 方法 */
  String fieldName();

  String message() default "输入值不在枚举范围内";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Documented
  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RUNTIME)
  @interface List {
    EnumPattern[] value();
  }
}
