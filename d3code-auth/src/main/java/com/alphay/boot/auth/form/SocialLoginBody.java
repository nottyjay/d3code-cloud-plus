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
public class SocialLoginBody extends LoginBody {

  /** 第三方登录平台 */
  @NotBlank(message = "{social.source.not.blank}")
  private String source;

  /** 第三方登录code */
  @NotBlank(message = "{social.code.not.blank}")
  private String socialCode;

  /** 第三方登录socialState */
  @NotBlank(message = "{social.state.not.blank}")
  private String socialState;
}
