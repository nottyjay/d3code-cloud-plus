package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.system.api.domain.param.SysDictTypeQueryParam;
import com.alphay.boot.system.domain.SysDictData;
import com.alphay.boot.system.domain.SysDictType;
import com.alphay.boot.system.domain.bo.SysDictTypeBo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.alphay.boot.system.domain.vo.SysDictTypeVo;
import com.alphay.boot.system.mapper.SysDictDataMapper;
import com.alphay.boot.system.mapper.SysDictTypeMapper;
import com.alphay.boot.system.service.ISysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典 业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysDictTypeServiceImpl
    extends ServiceImplX<SysDictTypeMapper, SysDictType, SysDictTypeVo>
    implements ISysDictTypeService {

  @Resource private SysDictDataMapper dictDataMapper;

  @Override
  public PageResult<SysDictTypeVo> queryPageList(SysDictTypeQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 根据条件分页查询字典类型
   *
   * @param param 字典类型信息
   * @return 字典类型集合信息
   */
  @Override
  public List<SysDictTypeVo> queryList(SysDictTypeQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysDictType> buildQueryWrapper(SysDictTypeQueryParam param) {
    LambdaQueryWrapper<SysDictType> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysDictType::getDictName, param.getDictName())
            .likeIfPresent(SysDictType::getDictType, param.getDictType())
            .betweenIfPresent(SysDictType::getCreateTime, param.getCreateTime());
    lqw.orderByAsc(SysDictType::getDictId);
    return lqw;
  }

  /**
   * 根据所有字典类型
   *
   * @return 字典类型集合信息
   */
  @Override
  public List<SysDictTypeVo> selectDictTypeAll() {
    return listVo(new QueryWrapper<>());
  }

  /**
   * 根据字典类型查询字典数据
   *
   * @param dictType 字典类型
   * @return 字典数据集合信息
   */
  @Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
  @Override
  public List<SysDictDataVo> selectDictDataByType(String dictType) {
    List<SysDictDataVo> dictDatas = dictDataMapper.selectDictDataByType(dictType);
    return CollUtil.isNotEmpty(dictDatas) ? dictDatas : null;
  }

  /**
   * 根据字典类型查询信息
   *
   * @param dictType 字典类型
   * @return 字典类型
   */
  @Cacheable(cacheNames = CacheNames.SYS_DICT_TYPE, key = "#dictType")
  @Override
  public SysDictTypeVo selectDictTypeByType(String dictType) {
    return getOneVo(SysDictType::getDictType, dictType);
  }

  /**
   * 批量删除字典类型信息
   *
   * @param dictIds 需要删除的字典ID
   */
  @Override
  public void deleteDictTypeByIds(List<Long> dictIds) {
    List<SysDictType> list = baseMapper.selectByIds(dictIds);
    list.forEach(
        x -> {
          boolean assigned =
              dictDataMapper.exists(
                  new LambdaQueryWrapper<SysDictData>()
                      .eq(SysDictData::getDictType, x.getDictType()));
          if (assigned) {
            throw new ServiceException(String.format("%1$s已分配,不能删除", x.getDictName()));
          }
        });
    baseMapper.deleteByIds(dictIds);
    list.forEach(
        x -> {
          CacheUtils.evict(CacheNames.SYS_DICT, x.getDictType());
          CacheUtils.evict(CacheNames.SYS_DICT_TYPE, x.getDictType());
        });
  }

  /** 重置字典缓存数据 */
  @Override
  public void resetDictCache() {
    CacheUtils.clear(CacheNames.SYS_DICT);
    CacheUtils.clear(CacheNames.SYS_DICT_TYPE);
  }

  /**
   * 新增保存字典类型信息
   *
   * @param bo 字典类型信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
  @Override
  public List<SysDictDataVo> insertDictType(SysDictTypeBo bo) {
    SysDictType dict = MapstructUtils.convert(bo, SysDictType.class);
    int row = baseMapper.insert(dict);
    if (row > 0) {
      // 新增 type 下无 data 数据 返回空防止缓存穿透
      return new ArrayList<>();
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 修改保存字典类型信息
   *
   * @param bo 字典类型信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<SysDictDataVo> updateDictType(SysDictTypeBo bo) {
    SysDictType dict = MapstructUtils.convert(bo, SysDictType.class);
    SysDictType oldDict = baseMapper.selectById(dict.getDictId());
    dictDataMapper.update(
        null,
        new LambdaUpdateWrapper<SysDictData>()
            .set(SysDictData::getDictType, dict.getDictType())
            .eq(SysDictData::getDictType, oldDict.getDictType()));
    int row = baseMapper.updateById(dict);
    if (row > 0) {
      CacheUtils.evict(CacheNames.SYS_DICT, oldDict.getDictType());
      return dictDataMapper.selectDictDataByType(dict.getDictType());
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 校验字典类型称是否唯一
   *
   * @param dictType 字典类型
   * @return 结果
   */
  @Override
  public boolean checkDictTypeUnique(SysDictTypeBo dictType) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType.getDictType())
                .ne(
                    ObjectUtil.isNotNull(dictType.getDictId()),
                    SysDictType::getDictId,
                    dictType.getDictId()));
    return !exist;
  }
}
