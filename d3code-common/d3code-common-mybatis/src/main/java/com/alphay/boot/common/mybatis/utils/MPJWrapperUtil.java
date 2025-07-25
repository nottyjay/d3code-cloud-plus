package com.alphay.boot.common.mybatis.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import java.util.Collection;

/**
 * @auther nottyjay
 * @date 2025/7/24
 */
public class MPJWrapperUtil {

  public static <T> MPJLambdaWrapper<T> betweenIfExists(
      MPJLambdaWrapper<T> wrapper, SFunction<T, ?> column, Object val1, Object val2) {
    if (val1 != null && val2 != null) {
      return wrapper.between(column, val1, val2);
    }
    if (val1 != null) {
      return wrapper.ge(column, val1);
    }
    if (val2 != null) {
      return wrapper.le(column, val2);
    }
    return wrapper;
  }

  public static <T> MPJLambdaWrapper<T> betweenIfExists(
      MPJLambdaWrapper<T> wrapper, SFunction<T, ?> column, Object[] values) {
    Object val1 = ArrayUtil.get(values, 0);
    Object val2 = ArrayUtil.get(values, 1);
    return betweenIfExists(wrapper, column, val1, val2);
  }

  public static <T> MPJLambdaWrapper<T> inIfExists(
      MPJLambdaWrapper<T> wrapper, SFunction<T, ?> column, Collection<?> values) {
    if (CollUtil.isEmpty(values)) {
      return wrapper;
    }
    return wrapper.in(column, values);
  }

  public static <T> MPJLambdaWrapper<T> inIfExists(
      MPJLambdaWrapper<T> wrapper, SFunction<T, ?> column, Object... values) {
    if (ArrayUtil.isEmpty(values)) {
      return wrapper;
    }
    return wrapper.in(column, values);
  }
}
