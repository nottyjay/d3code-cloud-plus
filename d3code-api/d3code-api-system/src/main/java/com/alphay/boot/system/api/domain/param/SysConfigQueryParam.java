package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 参数配置查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysConfigQueryParam extends PageReqParam {

  private String configKey;
  private String configName;
  private String configType;
  private Date[] createTime;
}
