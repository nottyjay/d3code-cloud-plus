package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysDictTypeQueryParam;
import com.alphay.boot.system.domain.SysDictType;
import com.alphay.boot.system.domain.bo.SysDictTypeBo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.alphay.boot.system.domain.vo.SysDictTypeVo;
import java.util.List;

/**
 * 字典 业务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysDictTypeService extends IServiceX<SysDictType, SysDictTypeVo> {

  PageResult<SysDictTypeVo> queryPageList(SysDictTypeQueryParam param);

  /**
   * 根据条件分页查询字典类型
   *
   * @param param 字典类型信息
   * @return 字典类型集合信息
   */
  List<SysDictTypeVo> queryList(SysDictTypeQueryParam param);

  /**
   * 根据所有字典类型
   *
   * @return 字典类型集合信息
   */
  List<SysDictTypeVo> selectDictTypeAll();

  /**
   * 根据字典类型查询字典数据
   *
   * @param dictType 字典类型
   * @return 字典数据集合信息
   */
  List<SysDictDataVo> selectDictDataByType(String dictType);

  /**
   * 根据字典类型查询信息
   *
   * @param dictType 字典类型
   * @return 字典类型
   */
  SysDictTypeVo selectDictTypeByType(String dictType);

  /**
   * 批量删除字典信息
   *
   * @param dictIds 需要删除的字典ID
   */
  void deleteDictTypeByIds(List<Long> dictIds);

  /** 重置字典缓存数据 */
  void resetDictCache();

  /**
   * 新增保存字典类型信息
   *
   * @param bo 字典类型信息
   * @return 结果
   */
  List<SysDictDataVo> insertDictType(SysDictTypeBo bo);

  /**
   * 修改保存字典类型信息
   *
   * @param bo 字典类型信息
   * @return 结果
   */
  List<SysDictDataVo> updateDictType(SysDictTypeBo bo);

  /**
   * 校验字典类型称是否唯一
   *
   * @param dictType 字典类型
   * @return 结果
   */
  boolean checkDictTypeUnique(SysDictTypeBo dictType);
}
