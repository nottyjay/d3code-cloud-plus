package com.alphay.boot.common.core.domain.param;

import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.core.utils.sql.SqlUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 分页参数请求
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public abstract class PageReqParam {

  /** 当前记录起始索引 默认值 */
  public static final long DEFAULT_PAGE_NUM = 1l;

  /** 每页显示记录数 默认值 默认查全部 */
  public static final long DEFAULT_PAGE_SIZE = 20l;

  /**
   * 每页条数 - 不分页
   *
   * <p>例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
   */
  public static final Integer PAGE_SIZE_NONE = -1;

  private Long pageNum = DEFAULT_PAGE_NUM;
  private Long pageSize = DEFAULT_PAGE_SIZE;
  private String orderByColumn;
  private String isAsc;

  public List<SortOrder> getSortOrder() {
    if (StringUtils.isBlank(orderByColumn) || StringUtils.isBlank(isAsc)) {
      return null;
    }
    String orderBy = SqlUtil.escapeOrderBySql(orderByColumn);
    orderBy = StringUtils.toUnderScoreCase(orderBy);

    // 兼容前端排序类型
    isAsc =
        StringUtils.replaceEach(
            isAsc, new String[] {"ascending", "descending"}, new String[] {"asc", "desc"});

    String[] orderByArr = orderBy.split(StringUtils.SEPARATOR);
    String[] isAscArr = isAsc.split(StringUtils.SEPARATOR);
    if (isAscArr.length != 1 && isAscArr.length != orderByArr.length) {
      throw new ServiceException("排序参数有误");
    }
    // 每个字段各自排序
    List<SortOrder> orders = new ArrayList<SortOrder>();
    for (int i = 0; i < orderByArr.length; i++) {
      String orderByStr = orderByArr[i];
      String isAscStr = isAscArr.length == 1 ? isAscArr[0] : isAscArr[i];
      if ("asc".equals(isAscStr)) {
        orders.add(SortOrder.asc(orderByStr));
      } else if ("desc".equals(isAscStr)) {
        orders.add(SortOrder.desc(orderByStr));
      } else {
        throw new ServiceException("排序参数有误");
      }
    }
    return orders;
  }

  @Data
  public static class SortOrder {
    private boolean asc;
    private String column;

    public SortOrder(boolean asc, String column) {
      this.asc = asc;
      this.column = column;
    }

    public static SortOrder asc(String column) {
      return new SortOrder(true, column);
    }

    public static SortOrder desc(String column) {
      return new SortOrder(false, column);
    }
  }
}
