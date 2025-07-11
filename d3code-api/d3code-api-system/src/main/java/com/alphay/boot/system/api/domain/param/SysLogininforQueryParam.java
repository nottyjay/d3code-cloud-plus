package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 登录记录查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysLogininforQueryParam extends PageReqParam {

  private String ipaddr;
  private String status;
  private String userName;
  private Date[] loginTime;
}
