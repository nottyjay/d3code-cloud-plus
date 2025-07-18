package com.alphay.boot.common.core.validate.enumd;

import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.reflect.ReflectUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义枚举校验注解实现
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2024-12-09
 */
public class EnumPatternValidator implements ConstraintValidator<EnumPattern, String> {

  private EnumPattern annotation;

  @Override
  public void initialize(EnumPattern annotation) {
    ConstraintValidator.super.initialize(annotation);
    this.annotation = annotation;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isNotBlank(value)) {
      String fieldName = annotation.fieldName();
      for (Object e : annotation.type().getEnumConstants()) {
        if (value.equals(ReflectUtils.invokeGetter(e, fieldName))) {
          return true;
        }
      }
    }
    return false;
  }
}
