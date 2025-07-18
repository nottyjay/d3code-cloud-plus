package com.alphay.boot.gateway.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alphay.boot.common.core.constant.HttpStatus;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.gateway.config.properties.IgnoreWhiteProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * [Sa-Token 权限认证] 拦截器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Configuration
public class AuthFilter {

  /** 注册 Sa-Token 全局过滤器 */
  @Bean
  public SaReactorFilter getSaReactorFilter(IgnoreWhiteProperties ignoreWhite) {
    return new SaReactorFilter()
        // 拦截地址
        .addInclude("/**")
        .addExclude("/favicon.ico", "/actuator", "/actuator/**", "/resource/sse")
        // 鉴权方法：每次访问进入
        .setAuth(
            obj -> {
              // 登录校验 -- 拦截所有路由
              SaRouter.match("/**")
                  .notMatch(ignoreWhite.getWhites())
                  .check(
                      r -> {
                        ServerHttpRequest request = SaReactorSyncHolder.getExchange().getRequest();
                        // 检查是否登录 是否有token
                        StpUtil.checkLogin();

                        // 检查 header 与 param 里的 clientid 与 token 里的是否一致
                        String headerCid = request.getHeaders().getFirst(LoginHelper.CLIENT_KEY);
                        String paramCid = request.getQueryParams().getFirst(LoginHelper.CLIENT_KEY);
                        String clientId = StpUtil.getExtra(LoginHelper.CLIENT_KEY).toString();
                        if (!StringUtils.equalsAny(clientId, headerCid, paramCid)) {
                          // token 无效
                          throw NotLoginException.newInstance(
                              StpUtil.getLoginType(),
                              "-100",
                              "客户端ID与Token不匹配",
                              StpUtil.getTokenValue());
                        }

                        // 有效率影响 用于临时测试
                        // if (log.isDebugEnabled()) {
                        //     log.debug("剩余有效时间: {}", StpUtil.getTokenTimeout());
                        //     log.debug("临时有效时间: {}", StpUtil.getTokenActivityTimeout());
                        // }
                      });
            })
        .setError(
            e -> {
              if (e instanceof NotLoginException) {
                return SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED);
              }
              return SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED);
            });
  }

  /** 对 actuator 健康检查接口 做账号密码鉴权 */
  @Bean
  public SaReactorFilter actuatorFilter() {
    String username = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.username");
    String password = SpringUtils.getProperty("spring.cloud.nacos.discovery.metadata.userpassword");
    return new SaReactorFilter()
        .addInclude("/actuator", "/actuator/**")
        .setAuth(
            obj -> {
              SaHttpBasicUtil.check(username + ":" + password);
            })
        .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED));
  }
}
