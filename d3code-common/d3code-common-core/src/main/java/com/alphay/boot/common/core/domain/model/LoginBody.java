package com.alphay.boot.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录对象
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class LoginBody {

  /** 客户端id */
  @NotBlank(message = "{auth.clientid.not.blank}")
  private String clientId;

  /** 授权类型 */
  @NotBlank(message = "{auth.grant.type.not.blank}")
  private String grantType;

  /** 租户ID */
  private String tenantId;

  /** 验证码 */
  private String code;

  /** 唯一标识 */
  private String uuid;
}
