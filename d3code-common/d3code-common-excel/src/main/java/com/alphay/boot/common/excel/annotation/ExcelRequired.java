package com.alphay.boot.common.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 是否必填
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelRequired {

  /** 字体颜色 */
  IndexedColors fontColor() default IndexedColors.RED;
}
