package com.alphay.boot.common.web.core;

import com.alphay.boot.common.core.domain.R;

/**
 * web层通用数据处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class BaseController {

  /**
   * 响应返回结果
   *
   * @param rows 影响行数
   * @return 操作结果
   */
  protected R<Void> toAjax(int rows) {
    return rows > 0 ? R.ok() : R.fail();
  }

  /**
   * 响应返回结果
   *
   * @param result 结果
   * @return 操作结果
   */
  protected R<Void> toAjax(boolean result) {
    return result ? R.ok() : R.fail();
  }
}
