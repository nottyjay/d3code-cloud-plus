package com.alphay.boot.common.translation.core.impl;

import com.alphay.boot.common.translation.annotation.TranslationType;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.common.translation.core.TranslationInterface;
import com.alphay.boot.system.api.RemoteDeptService;
import lombok.AllArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 部门翻译实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
@TranslationType(type = TransConstant.DEPT_ID_TO_NAME)
public class DeptNameTranslationImpl implements TranslationInterface<String> {

  @DubboReference private RemoteDeptService remoteDeptService;

  @Override
  public String translation(Object key, String other) {
    return remoteDeptService.selectDeptNameByIds(key.toString());
  }
}
