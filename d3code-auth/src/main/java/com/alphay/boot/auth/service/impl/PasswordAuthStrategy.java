package com.alphay.boot.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.auth.form.PasswordLoginBody;
import com.alphay.boot.auth.properties.CaptchaProperties;
import com.alphay.boot.auth.service.IAuthStrategy;
import com.alphay.boot.auth.service.SysLoginService;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.core.enums.LoginType;
import com.alphay.boot.common.core.exception.user.CaptchaException;
import com.alphay.boot.common.core.exception.user.CaptchaExpireException;
import com.alphay.boot.common.core.utils.MessageUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.ValidatorUtils;
import com.alphay.boot.common.json.utils.JsonUtils;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.system.api.RemoteUserService;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;
import com.alphay.boot.system.api.model.LoginUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 密码认证策略
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
public class PasswordAuthStrategy implements IAuthStrategy {

  @Resource private CaptchaProperties captchaProperties;

  @Resource private SysLoginService loginService;

  @DubboReference private RemoteUserService remoteUserService;

  @Override
  public LoginVo login(String body, RemoteClientVo client) {
    PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
    ValidatorUtils.validate(loginBody);
    String tenantId = loginBody.getTenantId();
    String username = loginBody.getUsername();
    String password = loginBody.getPassword();
    String code = loginBody.getCode();
    String uuid = loginBody.getUuid();

    // 验证码开关
    if (captchaProperties.getEnabled()) {
      validateCaptcha(tenantId, username, code, uuid);
    }
    LoginUser loginUser =
        TenantHelper.dynamic(
            tenantId,
            () -> {
              LoginUser user = remoteUserService.getUserInfo(username, tenantId);
              loginService.checkLogin(
                  LoginType.PASSWORD,
                  tenantId,
                  username,
                  () -> !BCrypt.checkpw(password, user.getPassword()));
              return user;
            });
    loginUser.setClientKey(client.getClientKey());
    loginUser.setDeviceType(client.getDeviceType());
    SaLoginParameter model = new SaLoginParameter();
    model.setDeviceType(client.getDeviceType());
    // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
    // 例如: 后台用户30分钟过期 app用户1天过期
    model.setTimeout(client.getTimeout());
    model.setActiveTimeout(client.getActiveTimeout());
    model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
    // 生成token
    LoginHelper.login(loginUser, model);

    LoginVo loginVo = new LoginVo();
    loginVo.setAccessToken(StpUtil.getTokenValue());
    loginVo.setExpireIn(StpUtil.getTokenTimeout());
    loginVo.setClientId(client.getClientId());
    return loginVo;
  }

  /**
   * 校验验证码
   *
   * @param username 用户名
   * @param code 验证码
   * @param uuid 唯一标识
   */
  private void validateCaptcha(String tenantId, String username, String code, String uuid) {
    String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
    String captcha = RedisUtils.getCacheObject(verifyKey);
    RedisUtils.deleteObject(verifyKey);
    if (captcha == null) {
      loginService.recordLogininfor(
          tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
      throw new CaptchaExpireException();
    }
    if (!code.equalsIgnoreCase(captcha)) {
      loginService.recordLogininfor(
          tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
      throw new CaptchaException();
    }
  }
}
