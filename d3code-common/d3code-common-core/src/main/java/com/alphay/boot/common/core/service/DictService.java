package com.alphay.boot.common.core.service;

import com.alphay.boot.common.core.utils.StringUtils;
import java.util.Map;

/**
 * 字典服务服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface DictService {

  /**
   * 根据字典类型和字典值获取字典标签
   *
   * @param dictType 字典类型
   * @param dictValue 字典值
   * @return 字典标签
   */
  default String getDictLabel(String dictType, String dictValue) {
    return getDictLabel(dictType, dictValue, StringUtils.SEPARATOR);
  }

  /**
   * 根据字典类型和字典标签获取字典值
   *
   * @param dictType 字典类型
   * @param dictLabel 字典标签
   * @return 字典值
   */
  default String getDictValue(String dictType, String dictLabel) {
    return getDictValue(dictType, dictLabel, StringUtils.SEPARATOR);
  }

  /**
   * 根据字典类型和字典值获取字典标签
   *
   * @param dictType 字典类型
   * @param dictValue 字典值
   * @param separator 分隔符
   * @return 字典标签
   */
  String getDictLabel(String dictType, String dictValue, String separator);

  /**
   * 根据字典类型和字典标签获取字典值
   *
   * @param dictType 字典类型
   * @param dictLabel 字典标签
   * @param separator 分隔符
   * @return 字典值
   */
  String getDictValue(String dictType, String dictLabel, String separator);

  /**
   * 获取字典下所有的字典值与标签
   *
   * @param dictType 字典类型
   * @return dictValue为key，dictLabel为值组成的Map
   */
  Map<String, String> getAllDictByDictType(String dictType);
}
