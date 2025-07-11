package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 字典类型查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysDictTypeQueryParam extends PageReqParam {

  private String dictName;
  private String dictType;
  private Date[] createTime;
}
