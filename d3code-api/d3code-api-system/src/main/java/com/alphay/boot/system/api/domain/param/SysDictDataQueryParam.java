package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * 字典数据查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysDictDataQueryParam extends PageReqParam {

  /** 字典排序 */
  private Integer dictSort;

  private String dictType;

  private String dictLabel;
}
