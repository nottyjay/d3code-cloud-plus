package com.alphay.boot.auth.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alphay.boot.common.core.constant.CacheConstants;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.utils.MessageUtils;
import com.alphay.boot.common.core.utils.ServletUtils;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.core.utils.ip.AddressUtils;
import com.alphay.boot.common.log.event.LogininforEvent;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.resource.api.RemoteMessageService;
import com.alphay.boot.system.api.RemoteUserService;
import com.alphay.boot.system.api.domain.SysUserOnline;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * 用户行为 侦听器的实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Component
@Slf4j
public class UserActionListener implements SaTokenListener {

  @DubboReference private RemoteUserService remoteUserService;
  @DubboReference private RemoteMessageService remoteMessageService;

  /** 每次登录时触发 */
  @Override
  public void doLogin(
      String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
    UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
    String ip = ServletUtils.getClientIP();
    SysUserOnline userOnline = new SysUserOnline();
    userOnline.setIpaddr(ip);
    userOnline.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
    userOnline.setBrowser(userAgent.getBrowser().getName());
    userOnline.setOs(userAgent.getOs().getName());
    userOnline.setLoginTime(System.currentTimeMillis());
    userOnline.setTokenId(tokenValue);
    String username = (String) loginParameter.getExtra(LoginHelper.USER_NAME_KEY);
    String tenantId = (String) loginParameter.getExtra(LoginHelper.TENANT_KEY);
    userOnline.setUserName(username);
    userOnline.setClientKey((String) loginParameter.getExtra(LoginHelper.CLIENT_KEY));
    userOnline.setDeviceType(loginParameter.getDeviceType());
    userOnline.setDeptName((String) loginParameter.getExtra(LoginHelper.DEPT_NAME_KEY));
    TenantHelper.dynamic(
        tenantId,
        () -> {
          if (loginParameter.getTimeout() == -1) {
            RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, userOnline);
          } else {
            RedisUtils.setCacheObject(
                CacheConstants.ONLINE_TOKEN_KEY + tokenValue,
                userOnline,
                Duration.ofSeconds(loginParameter.getTimeout()));
          }
        });
    // 记录登录日志
    LogininforEvent logininforEvent = new LogininforEvent();
    logininforEvent.setTenantId(tenantId);
    logininforEvent.setUsername(username);
    logininforEvent.setStatus(Constants.LOGIN_SUCCESS);
    logininforEvent.setMessage(MessageUtils.message("user.login.success"));
    SpringUtils.context().publishEvent(logininforEvent);
    // 更新登录信息
    remoteUserService.recordLoginInfo((Long) loginParameter.getExtra(LoginHelper.USER_KEY), ip);
    log.info("user doLogin, useId:{}, token:{}", loginId, tokenValue);
  }

  /** 每次注销时触发 */
  @Override
  public void doLogout(String loginType, Object loginId, String tokenValue) {
    String tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY));
    TenantHelper.dynamic(
        tenantId,
        () -> {
          RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        });
    log.info("user doLogout, useId:{}, token:{}", loginId, tokenValue);
  }

  /** 每次被踢下线时触发 */
  @Override
  public void doKickout(String loginType, Object loginId, String tokenValue) {
    String tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY));
    TenantHelper.dynamic(
        tenantId,
        () -> {
          RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        });
    log.info("user doLogoutByLoginId, useId:{}, token:{}", loginId, tokenValue);
  }

  /** 每次被顶下线时触发 */
  @Override
  public void doReplaced(String loginType, Object loginId, String tokenValue) {
    String tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY));
    TenantHelper.dynamic(
        tenantId,
        () -> {
          RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        });
    log.info("user doReplaced, useId:{}, token:{}", loginId, tokenValue);
  }

  /** 每次被封禁时触发 */
  @Override
  public void doDisable(
      String loginType, Object loginId, String service, int level, long disableTime) {}

  /** 每次被解封时触发 */
  @Override
  public void doUntieDisable(String loginType, Object loginId, String service) {}

  /** 每次打开二级认证时触发 */
  @Override
  public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {}

  /** 每次创建Session时触发 */
  @Override
  public void doCloseSafe(String loginType, String tokenValue, String service) {}

  /** 每次创建Session时触发 */
  @Override
  public void doCreateSession(String id) {}

  /** 每次注销Session时触发 */
  @Override
  public void doLogoutSession(String id) {}

  /** 每次Token续期时触发 */
  @Override
  public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {}
}
