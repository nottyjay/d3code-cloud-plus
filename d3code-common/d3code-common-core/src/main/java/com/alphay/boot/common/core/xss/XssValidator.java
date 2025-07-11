package com.alphay.boot.common.core.xss;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义xss校验注解实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class XssValidator implements ConstraintValidator<Xss, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    return !ReUtil.contains(HtmlUtil.RE_HTML_MARK, value);
  }
}
