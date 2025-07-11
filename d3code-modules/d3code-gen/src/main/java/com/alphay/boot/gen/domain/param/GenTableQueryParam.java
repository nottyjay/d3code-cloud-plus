package com.alphay.boot.gen.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 构建表查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class GenTableQueryParam extends PageReqParam {

  private String tableName;
  private String dataName;
  private String tableComment;
  private Date[] createTime;
}
