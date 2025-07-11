package com.alphay.boot.common.translation.core.impl;

import com.alphay.boot.common.translation.annotation.TranslationType;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.common.translation.core.TranslationInterface;
import com.alphay.boot.resource.api.RemoteFileService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * OSS翻译实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.OSS_ID_TO_URL)
public class OssUrlTranslationImpl implements TranslationInterface<String> {

  @DubboReference(mock = "true")
  private RemoteFileService remoteFileService;

  @Override
  public String translation(Object key, String other) {
    return remoteFileService.selectUrlByIds(key.toString());
  }
}
