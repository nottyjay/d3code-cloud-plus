package com.alphay.boot.common.mybatis.core.mapper;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.utils.MybatisUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.util.function.Function;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * 自定义 Mapper 接口, 实现 自定义扩展
 *
 * @param <T> table 泛型
 * @author Nottyjay
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public interface BaseMapperX<T> extends BaseMapper<T> {

  Log log = LogFactory.getLog(BaseMapperX.class);

  default PageResult<T> selectPage(
      PageReqParam pageReqParam, @Param("ew") Wrapper<T> queryWrapper) {
    // 特殊：不分页，直接查询全部
    if (PageReqParam.PAGE_SIZE_NONE.equals(pageReqParam.getPageSize())) {
      List<T> list = selectList(queryWrapper);
      return new PageResult<>(list, (long) list.size());
    }

    // 使用Mybatis-Plus查询
    IPage<T> page = MybatisUtil.buildPage(pageReqParam, pageReqParam.getSortOrder());
    selectPage(page, queryWrapper);
    return new PageResult<>(page.getRecords(), page.getTotal());
  }

  default <V> PageResult<V> selectPage(
      PageReqParam pageReqParam, @Param("ew") Wrapper<T> queryWrapper, Class<V> voClass) {
    PageResult<T> pageResult = selectPage(pageReqParam, queryWrapper);
    return new PageResult<V>(
        MapstructUtils.convert(pageResult.getRows(), voClass), pageResult.getTotal());
  }

  default <V> List<V> selectList(@Param("ew") Wrapper<T> queryWrapper, Class<V> voClass) {
    List<T> list = selectList(queryWrapper);
    return MapstructUtils.convert(list, voClass);
  }

  default <V> V selectOne(@Param("ew") Wrapper<T> queryWrapper, Class<V> voClass) {
    T t = selectOne(queryWrapper);
    return MapstructUtils.convert(t, voClass);
  }

  default <C> List<C> selectObjs(Wrapper<T> queryWrapper, Function<? super Object, C> function) {
    return StreamUtils.toList(this.selectObjs(queryWrapper), function);
  }

  default boolean insertBatch(List<T> entityList) {
    for (T entity : entityList) {
      this.insert(entity);
    }
    return true;
  }
}
