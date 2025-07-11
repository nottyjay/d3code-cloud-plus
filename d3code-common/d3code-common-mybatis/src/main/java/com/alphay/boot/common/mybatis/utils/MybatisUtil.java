package com.alphay.boot.common.mybatis.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alphay.boot.common.core.domain.param.PageReqParam;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Collection;

/**
 * Mybatis工具类
 *
 * @author Nottyjay
 * @since
 */
public class MybatisUtil {

  /**
   * 分页查询 - 分页对象构造器
   *
   * @param pageReqParam
   * @param sortOrders
   * @return
   * @param <T>
   */
  public static <T> Page<T> buildPage(
      PageReqParam pageReqParam, Collection<PageReqParam.SortOrder> sortOrders) {
    Page<T> page = new Page<>(pageReqParam.getPageNum(), pageReqParam.getPageSize());
    if (!CollectionUtil.isEmpty(sortOrders)) {
      page.addOrder(
          sortOrders.stream()
              .map(field -> new OrderItem().setColumn(field.getColumn()).setAsc(field.isAsc()))
              .toList());
    }
    return page;
  }
}
