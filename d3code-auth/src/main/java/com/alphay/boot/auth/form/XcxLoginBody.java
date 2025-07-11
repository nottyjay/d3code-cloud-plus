package com.alphay.boot.auth.form;

import com.alphay.boot.common.core.domain.model.LoginBody;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 三方登录对象
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class XcxLoginBody extends LoginBody {

  /** 小程序id(多个小程序时使用) */
  private String appid;

  /** 小程序code */
  @NotBlank(message = "{xcx.code.not.blank}")
  private String xcxCode;
}
