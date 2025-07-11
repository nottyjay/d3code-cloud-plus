package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysDictDataQueryParam;
import com.alphay.boot.system.domain.SysDictData;
import com.alphay.boot.system.domain.bo.SysDictDataBo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import java.util.List;

/**
 * 字典 业务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysDictDataService extends IServiceX<SysDictData, SysDictDataVo> {

  PageResult<SysDictDataVo> queryPageDictDataList(SysDictDataQueryParam param);

  /**
   * 根据条件分页查询字典数据
   *
   * @param param 字典数据信息
   * @return 字典数据集合信息
   */
  List<SysDictDataVo> queryList(SysDictDataQueryParam param);

  /**
   * 根据字典类型和字典键值查询字典数据信息
   *
   * @param dictType 字典类型
   * @param dictValue 字典键值
   * @return 字典标签
   */
  String selectDictLabel(String dictType, String dictValue);

  /**
   * 批量删除字典数据信息
   *
   * @param dictCodes 需要删除的字典数据ID
   */
  void deleteDictDataByIds(List<Long> dictCodes);

  /**
   * 新增保存字典数据信息
   *
   * @param bo 字典数据信息
   * @return 结果
   */
  List<SysDictDataVo> insertDictData(SysDictDataBo bo);

  /**
   * 修改保存字典数据信息
   *
   * @param bo 字典数据信息
   * @return 结果
   */
  List<SysDictDataVo> updateDictData(SysDictDataBo bo);

  /**
   * 校验字典键值是否唯一
   *
   * @param dict 字典数据
   * @return 结果
   */
  boolean checkDictDataUnique(SysDictDataBo dict);
}
