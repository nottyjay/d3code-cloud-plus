package com.alphay.boot.common.translation.core.impl;

import com.alphay.boot.common.translation.annotation.TranslationType;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.common.translation.core.TranslationInterface;
import com.alphay.boot.system.api.RemoteUserService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 用户名翻译实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NAME)
public class UserNameTranslationImpl implements TranslationInterface<String> {

  @DubboReference private RemoteUserService remoteUserService;

  @Override
  public String translation(Object key, String other) {
    return remoteUserService.selectUserNameById((Long) key);
  }
}
