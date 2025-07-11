package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * SysClient分页查询
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysClientQueryParam extends PageReqParam {

  private String clientId;
  private String clientKey;
  private String clientSecret;
  private String status;
}
