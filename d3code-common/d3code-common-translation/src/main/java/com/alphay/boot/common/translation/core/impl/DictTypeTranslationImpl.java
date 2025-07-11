package com.alphay.boot.common.translation.core.impl;

import com.alphay.boot.common.core.service.DictService;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.translation.annotation.TranslationType;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.common.translation.core.TranslationInterface;
import lombok.AllArgsConstructor;

/**
 * 字典翻译实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.DICT_TYPE_TO_LABEL)
public class DictTypeTranslationImpl implements TranslationInterface<String> {

  private final DictService dictService;

  @Override
  public String translation(Object key, String other) {
    if (key instanceof String && StringUtils.isNotBlank(other)) {
      return dictService.getDictLabel(other, key.toString());
    }
    return null;
  }
}
