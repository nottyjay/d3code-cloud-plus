package com.alphay.boot.common.translation.core.impl;

import com.alphay.boot.common.translation.annotation.TranslationType;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.common.translation.core.TranslationInterface;
import com.alphay.boot.system.api.RemoteUserService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 用户昵称翻译实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.USER_ID_TO_NICKNAME)
public class NicknameTranslationImpl implements TranslationInterface<String> {

  @DubboReference private RemoteUserService remoteUserService;

  @Override
  public String translation(Object key, String other) {
    if (key instanceof Long id) {
      return remoteUserService.selectNicknameByIds(id.toString());
    } else if (key instanceof String ids) {
      return remoteUserService.selectNicknameByIds(ids);
    }
    return null;
  }
}
