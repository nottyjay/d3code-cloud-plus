package com.alphay.boot.system.api.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class RemoteUserVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 用户ID */
  private Long userId;

  /** 部门ID */
  private Long deptId;

  /** 用户账号 */
  private String userName;

  /** 用户昵称 */
  private String nickName;

  /** 用户类型（sys_user系统用户） */
  private String userType;

  /** 用户邮箱 */
  private String email;

  /** 手机号码 */
  private String phonenumber;

  /** 用户性别（0男 1女 2未知） */
  private String sex;

  /** 帐号状态（0正常 1停用） */
  private String status;

  /** 创建时间 */
  private Date createTime;
}
