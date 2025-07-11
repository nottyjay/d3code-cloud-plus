package com.alphay.boot.auth.service;

import com.alphay.boot.auth.domain.vo.LoginVo;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;

/**
 * 授权策略
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface IAuthStrategy {

  String BASE_NAME = "AuthStrategy";

  /**
   * 登录
   *
   * @param body 登录对象
   * @param client 授权管理视图对象
   * @param grantType 授权类型
   * @return 登录验证信息
   */
  static LoginVo login(String body, RemoteClientVo client, String grantType) {
    // 授权类型和客户端id
    String beanName = grantType + BASE_NAME;
    if (!SpringUtils.containsBean(beanName)) {
      throw new ServiceException("授权类型不正确!");
    }
    IAuthStrategy instance = SpringUtils.getBean(beanName);
    return instance.login(body, client);
  }

  /**
   * 登录
   *
   * @param body 登录对象
   * @param client 授权管理视图对象
   * @return 登录验证信息
   */
  LoginVo login(String body, RemoteClientVo client);
}
