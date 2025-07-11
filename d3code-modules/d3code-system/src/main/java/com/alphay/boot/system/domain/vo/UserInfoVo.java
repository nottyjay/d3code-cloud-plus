package com.alphay.boot.system.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import lombok.Data;

/**
 * 登录用户信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class UserInfoVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 用户基本信息 */
  private SysUserVo user;

  /** 菜单权限 */
  private Set<String> permissions;

  /** 角色权限 */
  private Set<String> roles;
}
