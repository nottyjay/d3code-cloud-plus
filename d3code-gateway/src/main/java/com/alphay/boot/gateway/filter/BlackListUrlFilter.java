package com.alphay.boot.gateway.filter;

import com.alphay.boot.gateway.utils.WebFluxUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 黑名单过滤器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Component
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config> {
  public BlackListUrlFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String url = exchange.getRequest().getURI().getPath();
      if (config.matchBlacklist(url)) {
        return WebFluxUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
      }

      return chain.filter(exchange);
    };
  }

  public static class Config {
    private List<String> blacklistUrl;

    private List<Pattern> blacklistUrlPattern = new ArrayList<>();

    public boolean matchBlacklist(String url) {
      return !blacklistUrlPattern.isEmpty()
          && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
    }

    public List<String> getBlacklistUrl() {
      return blacklistUrl;
    }

    public void setBlacklistUrl(List<String> blacklistUrl) {
      this.blacklistUrl = blacklistUrl;
      this.blacklistUrlPattern.clear();
      this.blacklistUrl.forEach(
          url -> {
            this.blacklistUrlPattern.add(
                Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
          });
    }
  }
}
