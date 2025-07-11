package com.alphay.boot.auth.form;

import com.alphay.boot.common.core.domain.model.LoginBody;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册对象
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterBody extends LoginBody {

  /** 用户名 */
  @NotBlank(message = "{user.username.not.blank}")
  @Length(min = 2, max = 30, message = "{user.username.length.valid}")
  private String username;

  /** 用户密码 */
  @NotBlank(message = "{user.password.not.blank}")
  @Length(min = 5, max = 30, message = "{user.password.length.valid}")
  private String password;

  /** 用户类型 */
  private String userType;
}
