package com.alphay.boot.auth.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.auth.domain.vo.LoginTenantVo;
import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.auth.domain.vo.TenantListVo;
import com.alphay.boot.auth.form.RegisterBody;
import com.alphay.boot.auth.form.SocialLoginBody;
import com.alphay.boot.auth.service.IAuthStrategy;
import com.alphay.boot.auth.service.SysLoginService;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.domain.model.LoginBody;
import com.alphay.boot.common.core.utils.*;
import com.alphay.boot.common.encrypt.annotation.ApiEncrypt;
import com.alphay.boot.common.json.utils.JsonUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.social.config.properties.SocialLoginConfigProperties;
import com.alphay.boot.common.social.config.properties.SocialProperties;
import com.alphay.boot.common.social.utils.SocialUtils;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.resource.api.RemoteMessageService;
import com.alphay.boot.system.api.RemoteClientService;
import com.alphay.boot.system.api.RemoteConfigService;
import com.alphay.boot.system.api.RemoteSocialService;
import com.alphay.boot.system.api.RemoteTenantService;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;
import com.alphay.boot.system.api.domain.vo.RemoteTenantVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * token 控制
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@RestController
public class TokenController {

  @Resource private SocialProperties socialProperties;
  @Resource private SysLoginService sysLoginService;
  @Resource private ScheduledExecutorService scheduledExecutorService;

  @DubboReference private RemoteConfigService remoteConfigService;
  @DubboReference private RemoteTenantService remoteTenantService;
  @DubboReference private RemoteClientService remoteClientService;
  @DubboReference private RemoteSocialService remoteSocialService;

  @DubboReference(stub = "true")
  private RemoteMessageService remoteMessageService;

  /**
   * 登录方法
   *
   * @param body 登录信息
   * @return 结果
   */
  @ApiEncrypt
  @PostMapping("/login")
  public R<LoginVo> login(@RequestBody String body) {
    LoginBody loginBody = JsonUtils.parseObject(body, LoginBody.class);
    ValidatorUtils.validate(loginBody);
    // 授权类型和客户端id
    String clientId = loginBody.getClientId();
    String grantType = loginBody.getGrantType();
    RemoteClientVo clientVo = remoteClientService.queryByClientId(clientId);

    // 查询不到 client 或 client 内不包含 grantType
    if (ObjectUtil.isNull(clientVo) || !StringUtils.contains(clientVo.getGrantType(), grantType)) {
      log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType);
      return R.fail(MessageUtils.message("auth.grant.type.error"));
    } else if (!SystemConstants.NORMAL.equals(clientVo.getStatus())) {
      return R.fail(MessageUtils.message("auth.grant.type.blocked"));
    }
    // 校验租户
    sysLoginService.checkTenant(loginBody.getTenantId());
    // 登录
    LoginVo loginVo = IAuthStrategy.login(body, clientVo, grantType);

    Long userId = LoginHelper.getUserId();
    scheduledExecutorService.schedule(
        () -> {
          remoteMessageService.publishMessage(List.of(userId), "欢迎登录D3code-Cloud-Plus微服务管理系统");
        },
        5,
        TimeUnit.SECONDS);
    return R.ok(loginVo);
  }

  /**
   * 第三方登录请求
   *
   * @param source 登录来源
   * @return 结果
   */
  @GetMapping("/binding/{source}")
  public R<String> authBinding(
      @PathVariable("source") String source,
      @RequestParam String tenantId,
      @RequestParam String domain) {
    SocialLoginConfigProperties obj = socialProperties.getType().get(source);
    if (ObjectUtil.isNull(obj)) {
      return R.fail(source + "平台账号暂不支持");
    }
    AuthRequest authRequest = SocialUtils.getAuthRequest(source, socialProperties);
    Map<String, String> map = new HashMap<>();
    map.put("tenantId", tenantId);
    map.put("domain", domain);
    map.put("state", AuthStateUtils.createState());
    String authorizeUrl =
        authRequest.authorize(Base64.encode(JsonUtils.toJsonString(map), StandardCharsets.UTF_8));
    return R.ok("操作成功", authorizeUrl);
  }

  /**
   * 第三方登录回调业务处理 绑定授权
   *
   * @param loginBody 请求体
   * @return 结果
   */
  @PostMapping("/social/callback")
  public R<Void> socialCallback(@RequestBody SocialLoginBody loginBody) {
    // 获取第三方登录信息
    AuthResponse<AuthUser> response =
        SocialUtils.loginAuth(
            loginBody.getSource(),
            loginBody.getSocialCode(),
            loginBody.getSocialState(),
            socialProperties);
    AuthUser authUserData = response.getData();
    // 判断授权响应是否成功
    if (!response.ok()) {
      return R.fail(response.getMsg());
    }
    sysLoginService.socialRegister(authUserData);
    return R.ok();
  }

  /**
   * 取消授权
   *
   * @param socialId socialId
   */
  @DeleteMapping(value = "/unlock/{socialId}")
  public R<Void> unlockSocial(@PathVariable Long socialId) {
    Boolean rows = remoteSocialService.deleteWithValidById(socialId);
    return rows ? R.ok() : R.fail("取消授权失败");
  }

  /** 登出方法 */
  @PostMapping("logout")
  public R<Void> logout() {
    sysLoginService.logout();
    return R.ok();
  }

  /** 用户注册 */
  @ApiEncrypt
  @PostMapping("register")
  public R<Void> register(@RequestBody RegisterBody registerBody) {
    if (!remoteConfigService.selectRegisterEnabled(registerBody.getTenantId())) {
      return R.fail("当前系统没有开启注册功能！");
    }
    // 用户注册
    sysLoginService.register(registerBody);
    return R.ok();
  }

  /**
   * 登录页面租户下拉框
   *
   * @return 租户列表
   */
  @GetMapping("/tenant/list")
  public R<LoginTenantVo> tenantList(HttpServletRequest request) throws Exception {
    // 返回对象
    LoginTenantVo result = new LoginTenantVo();
    boolean enable = TenantHelper.isEnable();
    result.setTenantEnabled(enable);
    // 如果未开启租户这直接返回
    if (!enable) {
      return R.ok(result);
    }

    List<RemoteTenantVo> tenantList = remoteTenantService.queryList();
    List<TenantListVo> voList = MapstructUtils.convert(tenantList, TenantListVo.class);
    try {
      // 如果只超管返回所有租户
      if (LoginHelper.isSuperAdmin()) {
        result.setVoList(voList);
        return R.ok(result);
      }
    } catch (NotLoginException ignored) {
    }

    // 获取域名
    String host;
    String referer = request.getHeader("referer");
    if (StringUtils.isNotBlank(referer)) {
      // 这里从referer中取值是为了本地使用hosts添加虚拟域名，方便本地环境调试
      host = referer.split("//")[1].split("/")[0];
    } else {
      host = new URL(request.getRequestURL().toString()).getHost();
    }
    // 根据域名进行筛选
    List<TenantListVo> list =
        StreamUtils.filter(voList, vo -> StringUtils.equalsIgnoreCase(vo.getDomain(), host));
    result.setVoList(CollUtil.isNotEmpty(list) ? list : voList);
    return R.ok(result);
  }
}
