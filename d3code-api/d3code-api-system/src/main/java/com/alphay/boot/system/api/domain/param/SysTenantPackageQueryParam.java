package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * 租户套餐包 查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysTenantPackageQueryParam extends PageReqParam {

  private Long packageId;
  private String packageName;
  private String status;
}
