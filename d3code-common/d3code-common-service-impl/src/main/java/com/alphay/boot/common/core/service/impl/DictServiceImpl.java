package com.alphay.boot.common.core.service.impl;

import com.alphay.boot.common.core.constant.CacheConstants;
import com.alphay.boot.common.core.service.DictService;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.system.api.RemoteDictService;
import com.alphay.boot.system.api.domain.vo.RemoteDictDataVo;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 字典服务服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class DictServiceImpl implements DictService {

  @Autowired private Cache<Object, Object> ceffeine;

  @DubboReference private RemoteDictService remoteDictService;

  /**
   * 根据字典类型和字典值获取字典标签
   *
   * @param dictType 字典类型
   * @param dictValue 字典值
   * @param separator 分隔符
   * @return 字典标签
   */
  @SuppressWarnings("unchecked")
  @Override
  public String getDictLabel(String dictType, String dictValue, String separator) {
    // 优先从本地缓存获取
    List<RemoteDictDataVo> datas =
        (List<RemoteDictDataVo>)
            ceffeine.get(
                CacheConstants.SYS_DICT_KEY + "remote:" + dictType,
                k -> {
                  return remoteDictService.selectDictDataByType(dictType);
                });
    Map<String, String> map =
        StreamUtils.toMap(datas, RemoteDictDataVo::getDictValue, RemoteDictDataVo::getDictLabel);
    if (StringUtils.containsAny(dictValue, separator)) {
      return Arrays.stream(dictValue.split(separator))
          .map(v -> map.getOrDefault(v, StringUtils.EMPTY))
          .collect(Collectors.joining(separator));
    } else {
      return map.getOrDefault(dictValue, StringUtils.EMPTY);
    }
  }

  /**
   * 根据字典类型和字典标签获取字典值
   *
   * @param dictType 字典类型
   * @param dictLabel 字典标签
   * @param separator 分隔符
   * @return 字典值
   */
  @SuppressWarnings("unchecked")
  @Override
  public String getDictValue(String dictType, String dictLabel, String separator) {
    // 优先从本地缓存获取
    List<RemoteDictDataVo> datas =
        (List<RemoteDictDataVo>)
            ceffeine.get(
                CacheConstants.SYS_DICT_KEY + "remote:" + dictType,
                k -> {
                  return remoteDictService.selectDictDataByType(dictType);
                });
    Map<String, String> map =
        StreamUtils.toMap(datas, RemoteDictDataVo::getDictLabel, RemoteDictDataVo::getDictValue);
    if (StringUtils.containsAny(dictLabel, separator)) {
      return Arrays.stream(dictLabel.split(separator))
          .map(l -> map.getOrDefault(l, StringUtils.EMPTY))
          .collect(Collectors.joining(separator));
    } else {
      return map.getOrDefault(dictLabel, StringUtils.EMPTY);
    }
  }

  /**
   * 获取字典下所有的字典值与标签
   *
   * @param dictType 字典类型
   * @return dictValue为key，dictLabel为值组成的Map
   */
  @Override
  public Map<String, String> getAllDictByDictType(String dictType) {
    List<RemoteDictDataVo> list = remoteDictService.selectDictDataByType(dictType);
    // 保证顺序
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    for (RemoteDictDataVo vo : list) {
      map.put(vo.getDictValue(), vo.getDictLabel());
    }
    return map;
  }
}
