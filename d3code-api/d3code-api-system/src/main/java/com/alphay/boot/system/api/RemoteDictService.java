package com.alphay.boot.system.api;

import com.alphay.boot.system.api.domain.vo.RemoteDictDataVo;
import com.alphay.boot.system.api.domain.vo.RemoteDictTypeVo;
import java.util.List;

/**
 * 字典服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteDictService {

  /**
   * 根据字典类型查询信息
   *
   * @param dictType 字典类型
   * @return 字典类型
   */
  RemoteDictTypeVo selectDictTypeByType(String dictType);

  /**
   * 根据字典类型查询字典数据
   *
   * @param dictType 字典类型
   * @return 字典数据集合信息
   */
  List<RemoteDictDataVo> selectDictDataByType(String dictType);
}
