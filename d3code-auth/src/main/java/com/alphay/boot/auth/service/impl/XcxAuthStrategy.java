package com.alphay.boot.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.auth.form.XcxLoginBody;
import com.alphay.boot.auth.service.IAuthStrategy;
import com.alphay.boot.auth.service.SysLoginService;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.ValidatorUtils;
import com.alphay.boot.common.json.utils.JsonUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.api.RemoteUserService;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;
import com.alphay.boot.system.api.model.XcxLoginUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 邮件认证策略
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service("xcx" + IAuthStrategy.BASE_NAME)
public class XcxAuthStrategy implements IAuthStrategy {

  @Resource private SysLoginService loginService;

  @DubboReference private RemoteUserService remoteUserService;

  @Override
  public LoginVo login(String body, RemoteClientVo client) {
    XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
    ValidatorUtils.validate(loginBody);
    // xcxCode 为 小程序调用 wx.login 授权后获取
    String xcxCode = loginBody.getXcxCode();
    // 多个小程序识别使用
    String appid = loginBody.getAppid();

    // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
    AuthRequest authRequest =
        new AuthWechatMiniProgramRequest(
            AuthConfig.builder()
                .clientId(appid)
                .clientSecret("自行填写密钥 可根据不同appid填入不同密钥")
                .ignoreCheckRedirectUri(true)
                .ignoreCheckState(true)
                .build());
    AuthCallback authCallback = new AuthCallback();
    authCallback.setCode(xcxCode);
    AuthResponse<AuthUser> resp = authRequest.login(authCallback);
    String openid, unionId;
    if (resp.ok()) {
      AuthToken token = resp.getData().getToken();
      openid = token.getOpenId();
      // 微信小程序只有关联到微信开放平台下之后才能获取到 unionId，因此unionId不一定能返回。
      unionId = token.getUnionId();
    } else {
      throw new ServiceException(resp.getMsg());
    }
    // todo getUserInfoByOpenid 方法内部查询逻辑需要自行根据业务实现
    XcxLoginUser loginUser = remoteUserService.getUserInfoByOpenid(openid);
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
    loginVo.setOpenid(openid);
    return loginVo;
  }
}
