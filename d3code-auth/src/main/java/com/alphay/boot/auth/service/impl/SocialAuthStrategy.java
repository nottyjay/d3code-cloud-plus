package com.alphay.boot.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.collection.CollUtil;
import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.auth.form.SocialLoginBody;
import com.alphay.boot.auth.service.IAuthStrategy;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.ValidatorUtils;
import com.alphay.boot.common.json.utils.JsonUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.social.config.properties.SocialProperties;
import com.alphay.boot.common.social.utils.SocialUtils;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.system.api.RemoteSocialService;
import com.alphay.boot.system.api.RemoteUserService;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;
import com.alphay.boot.system.api.domain.vo.RemoteSocialVo;
import com.alphay.boot.system.api.model.LoginUser;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 第三方授权策略
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service("social" + IAuthStrategy.BASE_NAME)
public class SocialAuthStrategy implements IAuthStrategy {

  @Resource private SocialProperties socialProperties;

  @DubboReference private RemoteSocialService remoteSocialService;
  @DubboReference private RemoteUserService remoteUserService;

  /**
   * 登录-第三方授权登录
   *
   * @param body 登录信息
   * @param client 客户端信息
   */
  @Override
  public LoginVo login(String body, RemoteClientVo client) {
    SocialLoginBody loginBody = JsonUtils.parseObject(body, SocialLoginBody.class);
    ValidatorUtils.validate(loginBody);
    AuthResponse<AuthUser> response =
        SocialUtils.loginAuth(
            loginBody.getSource(),
            loginBody.getSocialCode(),
            loginBody.getSocialState(),
            socialProperties);
    if (!response.ok()) {
      throw new ServiceException(response.getMsg());
    }
    AuthUser authUserData = response.getData();

    List<RemoteSocialVo> list =
        remoteSocialService.selectByAuthId(authUserData.getSource() + authUserData.getUuid());
    if (CollUtil.isEmpty(list)) {
      throw new ServiceException("你还没有绑定第三方账号，绑定后才可以登录！");
    }
    RemoteSocialVo socialVo;
    if (TenantHelper.isEnable()) {
      Optional<RemoteSocialVo> opt =
          StreamUtils.findAny(list, x -> x.getTenantId().equals(loginBody.getTenantId()));
      if (opt.isEmpty()) {
        throw new ServiceException("对不起，你没有权限登录当前租户！");
      }
      socialVo = opt.get();
    } else {
      socialVo = list.get(0);
    }

    LoginUser loginUser =
        remoteUserService.getUserInfo(socialVo.getUserId(), socialVo.getTenantId());
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
}
