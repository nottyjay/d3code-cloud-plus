package com.alphay.boot.common.web.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.web.servlet.LocaleResolver;

/**
 * 获取请求头国际化信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class I18nLocaleResolver implements LocaleResolver {

  @Override
  public Locale resolveLocale(HttpServletRequest httpServletRequest) {
    String language = httpServletRequest.getHeader("content-language");
    Locale locale = Locale.getDefault();
    if (language != null && language.length() > 0) {
      String[] split = language.split("_");
      locale = new Locale(split[0], split[1]);
    }
    return locale;
  }

  @Override
  public void setLocale(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Locale locale) {}
}
