package com.alphay.boot.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.system.api.domain.param.SysDictDataQueryParam;
import com.alphay.boot.system.domain.SysDictData;
import com.alphay.boot.system.domain.bo.SysDictDataBo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.alphay.boot.system.mapper.SysDictDataMapper;
import com.alphay.boot.system.service.ISysDictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

/**
 * 字典 业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysDictDataServiceImpl
    extends ServiceImplX<SysDictDataMapper, SysDictData, SysDictDataVo>
    implements ISysDictDataService {

  @Override
  public PageResult<SysDictDataVo> queryPageDictDataList(SysDictDataQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 根据条件分页查询字典数据
   *
   * @param param 字典数据信息
   * @return 字典数据集合信息
   */
  @Override
  public List<SysDictDataVo> queryList(SysDictDataQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysDictData> buildQueryWrapper(SysDictDataQueryParam param) {
    LambdaQueryWrapper<SysDictData> lqw =
        this.lambdaQueryWrapper()
            .eqIfPresent(SysDictData::getDictSort, param.getDictSort())
            .likeIfPresent(SysDictData::getDictLabel, param.getDictLabel())
            .eqIfPresent(SysDictData::getDictType, param.getDictType());
    lqw.orderByAsc(SysDictData::getDictSort, SysDictData::getDictCode);
    return lqw;
  }

  /**
   * 根据字典类型和字典键值查询字典数据信息
   *
   * @param dictType 字典类型
   * @param dictValue 字典键值
   * @return 字典标签
   */
  @Override
  public String selectDictLabel(String dictType, String dictValue) {
    return getOne(
            new LambdaQueryWrapper<SysDictData>()
                .select(SysDictData::getDictLabel)
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue))
        .getDictLabel();
  }

  /**
   * 批量删除字典数据信息
   *
   * @param dictCodes 需要删除的字典数据ID
   */
  @Override
  public void deleteDictDataByIds(List<Long> dictCodes) {
    List<SysDictData> list = baseMapper.selectByIds(dictCodes);
    baseMapper.deleteByIds(dictCodes);
    list.forEach(x -> CacheUtils.evict(CacheNames.SYS_DICT, x.getDictType()));
  }

  /**
   * 新增保存字典数据信息
   *
   * @param bo 字典数据信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
  @Override
  public List<SysDictDataVo> insertDictData(SysDictDataBo bo) {
    SysDictData data = MapstructUtils.convert(bo, SysDictData.class);
    int row = baseMapper.insert(data);
    if (row > 0) {
      return baseMapper.selectDictDataByType(data.getDictType());
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 修改保存字典数据信息
   *
   * @param bo 字典数据信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
  @Override
  public List<SysDictDataVo> updateDictData(SysDictDataBo bo) {
    SysDictData data = MapstructUtils.convert(bo, SysDictData.class);
    int row = baseMapper.updateById(data);
    if (row > 0) {
      return baseMapper.selectDictDataByType(data.getDictType());
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 校验字典键值是否唯一
   *
   * @param dict 字典数据
   * @return 结果
   */
  @Override
  public boolean checkDictDataUnique(SysDictDataBo dict) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dict.getDictType())
                .eq(SysDictData::getDictValue, dict.getDictValue())
                .ne(
                    ObjectUtil.isNotNull(dict.getDictCode()),
                    SysDictData::getDictCode,
                    dict.getDictCode()));
    return !exist;
  }
}
