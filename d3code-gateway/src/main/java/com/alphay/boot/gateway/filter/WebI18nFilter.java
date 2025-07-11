package com.alphay.boot.gateway.filter;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 全局国际化处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebI18nFilter implements WebFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String language = exchange.getRequest().getHeaders().getFirst("content-language");
    Locale locale = Locale.getDefault();
    if (language != null && language.length() > 0) {
      String[] split = language.split("_");
      locale = new Locale(split[0], split[1]);
    }
    LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(locale), true);
    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
