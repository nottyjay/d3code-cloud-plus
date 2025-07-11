package com.alphay.boot.common.mybatis.core.page;

import cn.hutool.core.collection.CollectionUtil;
import com.alphay.boot.common.core.domain.param.PageReqParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 分页返回结果
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

  private int code;

  private String msg;

  private List<T> rows;

  private Long total;

  public PageResult() {
    this.rows = null;
    this.total = 0L;
    this.code = 200;
    this.msg = "success";
  }

  public PageResult(List<T> rows, Long total) {
    this.rows = rows;
    this.total = total;
    this.code = 200;
    this.msg = "success";
  }

  public static <T> PageResult<T> build(IPage<T> page) {
    return new PageResult<>(page.getRecords(), page.getTotal());
  }

  public static <T> PageResult<T> build(List<T> list) {
    return new PageResult<>(list, Long.valueOf(list.size()));
  }

  public static PageResult build() {
    return new PageResult(null, 0L);
  }

  public static <T> PageResult<T> build(List<T> list, PageReqParam page) {
    if (CollectionUtil.isEmpty(list)) {
      return build();
    }
    List<T> rows =
        CollectionUtil.page(page.getPageNum().intValue() - 1, page.getPageSize().intValue(), list);
    return new PageResult<>(rows, Long.valueOf(list.size()));
  }
}
