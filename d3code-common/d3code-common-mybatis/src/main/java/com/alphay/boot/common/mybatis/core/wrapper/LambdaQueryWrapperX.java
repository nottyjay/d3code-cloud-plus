package com.alphay.boot.common.mybatis.core.wrapper;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Collection;
import java.util.function.Consumer;
import org.springframework.util.StringUtils;

/**
 * LambdaQueryWrapper 增强
 *
 * @auther nottyjay
 * @date 2025/6/29
 */
public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

  public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
    if (StringUtils.hasText(val)) {
      return (LambdaQueryWrapperX<T>) super.like(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
    if (!CollectionUtils.isEmpty(values)) {
      return (LambdaQueryWrapperX<T>) super.in(column, values);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Object... values) {
    if (!ArrayUtil.isEmpty(values)) {
      return (LambdaQueryWrapperX<T>) super.in(column, values);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> notInIfPresent(SFunction<T, ?> column, Collection<?> values) {
    if (!CollectionUtils.isEmpty(values)) {
      return (LambdaQueryWrapperX<T>) super.notIn(column, values);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.eq(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.ne(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.gt(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.ge(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.lt(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
    if (val != null) {
      return (LambdaQueryWrapperX<T>) super.le(column, val);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
    if (val1 != null && val2 != null) {
      return (LambdaQueryWrapperX<T>) super.between(column, val1, val2);
    }
    if (val1 != null) {
      return (LambdaQueryWrapperX<T>) ge(column, val1);
    }
    if (val2 != null) {
      return (LambdaQueryWrapperX<T>) le(column, val2);
    }
    return this;
  }

  public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object[] values) {
    Object val1 = ArrayUtil.get(values, 0);
    Object val2 = ArrayUtil.get(values, 1);
    return betweenIfPresent(column, val1, val2);
  }

  // ========== 重写父类方法，方便链式调用 ==========

  @Override
  public LambdaQueryWrapperX<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
    super.eq(condition, column, val);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> func(Consumer<LambdaQueryWrapper<T>> consumer) {
    super.func(consumer);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> eq(SFunction<T, ?> column, Object val) {
    super.eq(column, val);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> like(SFunction<T, ?> column, Object val) {
    super.like(column, val);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> orderByDesc(SFunction<T, ?> column) {
    super.orderByDesc(true, column);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> last(String lastSql) {
    super.last(lastSql);
    return this;
  }

  @Override
  public LambdaQueryWrapperX<T> in(SFunction<T, ?> column, Collection<?> coll) {
    super.in(column, coll);
    return this;
  }
}
