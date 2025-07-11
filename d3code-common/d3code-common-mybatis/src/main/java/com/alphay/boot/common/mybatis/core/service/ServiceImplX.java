package com.alphay.boot.common.mybatis.core.service;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.wrapper.LambdaQueryWrapperX;
import com.alphay.boot.common.mybatis.core.wrapper.QueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.Serializable;
import java.util.List;

/**
 * Mybatis Service 增强类
 *
 * @auther nottyjay
 * @date 2025/6/29
 */
public abstract class ServiceImplX<M extends BaseMapperX<T>, T, VO> extends ServiceImpl<M, T>
    implements IServiceX<T, VO> {

  public LambdaQueryWrapperX<T> lambdaQueryWrapper() {
    return new LambdaQueryWrapperX<T>();
  }

  public QueryWrapperX<T> queryWrapper() {
    return new QueryWrapperX<T>();
  }

  public VO getVoById(Serializable id) {
    return getById(id, getVoClass());
  }

  public VO getOneVo(SFunction<T, ?> column, Object val) {
    return getOne(column, val, getVoClass());
  }

  public VO getOneVo(Wrapper<T> queryWrapper) {
    return getOne(queryWrapper, getVoClass());
  }

  public <V> V getById(Serializable id, Class<V> voClass) {
    return MapstructUtils.convert(getBaseMapper().selectById(id), voClass);
  }

  public <V> V getOne(SFunction<T, ?> column, Object val, Class<V> voClass) {
    T entity = getBaseMapper().selectOne(lambdaQueryWrapper().eq(column, val));
    return MapstructUtils.convert(entity, voClass);
  }

  public <V> V getOne(Wrapper<T> queryWrapper, Class<V> voClass) {
    return getBaseMapper().selectOne(queryWrapper, voClass);
  }

  public <V> List<V> list(Wrapper<T> queryWrapper, Class<V> voClass) {
    List<T> list = getBaseMapper().selectList(queryWrapper);
    return MapstructUtils.convert(list, voClass);
  }

  public Long count(SFunction<T, ?> column, Object val) {
    return getBaseMapper().selectCount(lambdaQueryWrapper().eq(column, val));
  }

  public PageResult<VO> listPageVo(PageReqParam param, Wrapper<T> queryWrapper) {
    return baseMapper.selectPage(param, queryWrapper, getVoClass());
  }

  public List<VO> listVo(Wrapper<T> queryWrapper) {
    return baseMapper.selectList(queryWrapper, getVoClass());
  }

  public Class<VO> getVoClass() {
    return (Class<VO>)
        GenericTypeUtils.resolveTypeArguments(this.getClass(), ServiceImplX.class)[2];
  }

  public T getOne(SFunction<T, ?> column, Object val) {
    return getBaseMapper().selectOne(lambdaQueryWrapper().eq(column, val));
  }
}
