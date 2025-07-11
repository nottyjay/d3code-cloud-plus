package com.alphay.boot.auth.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * 登录租户对象
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class LoginTenantVo {

  /** 租户开关 */
  private Boolean tenantEnabled;

  /** 租户对象列表 */
  private List<TenantListVo> voList;
}
