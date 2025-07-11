package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 租户查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysTenantQueryParam extends PageReqParam {

  private String tenantId;
  private String contactUserName;
  private String contactPhone;
  private String companyName;
  private String licenseNumber;
  private String address;
  private String domain;
  private String intro;
  private String status;
  private Long accountCount;
  private Long packageId;
  private Date expireTime;
}
