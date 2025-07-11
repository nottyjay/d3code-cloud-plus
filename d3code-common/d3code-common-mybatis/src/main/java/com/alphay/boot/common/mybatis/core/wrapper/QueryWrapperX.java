package com.alphay.boot.common.mybatis.core.wrapper;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import java.util.Collection;
import java.util.function.Consumer;
import org.springframework.util.StringUtils;

/**
 * @author Nottyjay
 * @since
 */
public class QueryWrapperX<T> extends QueryWrapper<T> {

  public QueryWrapperX<T> likeIfPresent(String column, String val) {
    if (StringUtils.hasText(val)) {
      return (QueryWrapperX<T>) super.like(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> inIfPresent(String column, Collection<?> values) {
    if (!CollectionUtils.isEmpty(values)) {
      return (QueryWrapperX<T>) super.in(column, values);
    }
    return this;
  }

  public QueryWrapperX<T> inIfPresent(String column, Object... values) {
    if (!ArrayUtil.isEmpty(values)) {
      return (QueryWrapperX<T>) super.in(column, values);
    }
    return this;
  }

  public QueryWrapperX<T> notInIfPresent(String column, Collection<?> values) {
    if (!CollectionUtils.isEmpty(values)) {
      return (QueryWrapperX<T>) super.notIn(column, values);
    }
    return this;
  }

  public QueryWrapperX<T> eqIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.eq(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> neIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.ne(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> gtIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.gt(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> geIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.ge(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> ltIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.lt(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> leIfPresent(String column, Object val) {
    if (val != null) {
      return (QueryWrapperX<T>) super.le(column, val);
    }
    return this;
  }

  public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
    if (val1 != null && val2 != null) {
      return (QueryWrapperX<T>) super.between(column, val1, val2);
    }
    if (val1 != null) {
      return (QueryWrapperX<T>) ge(column, val1);
    }
    if (val2 != null) {
      return (QueryWrapperX<T>) le(column, val2);
    }
    return this;
  }

  public QueryWrapperX<T> betweenIfPresent(String column, Object[] values) {
    Object val1 = ArrayUtil.get(values, 0);
    Object val2 = ArrayUtil.get(values, 1);
    return betweenIfPresent(column, val1, val2);
  }

  // ========== 重写父类方法，方便链式调用 ==========

  @Override
  public QueryWrapperX<T> eq(boolean condition, String column, Object val) {
    super.eq(condition, column, val);
    return this;
  }

  @Override
  public QueryWrapperX<T> eq(String column, Object val) {
    super.eq(column, val);
    return this;
  }

  @Override
  public QueryWrapperX<T> notIn(boolean condition, String column, Collection<?> coll) {
    super.notIn(condition, column, coll);
    return this;
  }

  @Override
  public QueryWrapperX<T> and(Consumer<QueryWrapper<T>> consumer) {
    super.and(consumer);
    return this;
  }

  @Override
  public QueryWrapperX<T> like(String column, Object val) {
    super.like(column, val);
    return this;
  }

  @Override
  public QueryWrapperX<T> orderByDesc(String column) {
    super.orderByDesc(true, column);
    return this;
  }

  @Override
  public QueryWrapperX<T> last(String lastSql) {
    super.last(lastSql);
    return this;
  }

  @Override
  public QueryWrapperX<T> in(String column, Collection<?> coll) {
    super.in(column, coll);
    return this;
  }

  @Override
  public QueryWrapperX<T> inSql(String column, String sql) {
    super.inSql(column, sql);
    return this;
  }
}
