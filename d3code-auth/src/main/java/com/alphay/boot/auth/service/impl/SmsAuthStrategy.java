package com.alphay.boot.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.auth.form.SmsLoginBody;
import com.alphay.boot.auth.service.IAuthStrategy;
import com.alphay.boot.auth.service.SysLoginService;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.GlobalConstants;
import com.alphay.boot.common.core.enums.LoginType;
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
 * 短信认证策略
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service("sms" + IAuthStrategy.BASE_NAME)
public class SmsAuthStrategy implements IAuthStrategy {

  @Resource private SysLoginService loginService;

  @DubboReference private RemoteUserService remoteUserService;

  @Override
  public LoginVo login(String body, RemoteClientVo client) {
    SmsLoginBody loginBody = JsonUtils.parseObject(body, SmsLoginBody.class);
    ValidatorUtils.validate(loginBody);
    String tenantId = loginBody.getTenantId();
    String phonenumber = loginBody.getPhonenumber();
    String smsCode = loginBody.getSmsCode();
    LoginUser loginUser =
        TenantHelper.dynamic(
            tenantId,
            () -> {
              LoginUser user = remoteUserService.getUserInfoByPhonenumber(phonenumber, tenantId);
              loginService.checkLogin(
                  LoginType.SMS,
                  tenantId,
                  user.getUsername(),
                  () -> !validateSmsCode(tenantId, phonenumber, smsCode));
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

  /** 校验短信验证码 */
  private boolean validateSmsCode(String tenantId, String phonenumber, String smsCode) {
    String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);
    if (StringUtils.isBlank(code)) {
      loginService.recordLogininfor(
          tenantId,
          phonenumber,
          Constants.LOGIN_FAIL,
          MessageUtils.message("user.jcaptcha.expire"));
      throw new CaptchaExpireException();
    }
    return code.equals(smsCode);
  }
}
