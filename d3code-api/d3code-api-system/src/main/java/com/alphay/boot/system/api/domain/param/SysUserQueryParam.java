package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 用户查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysUserQueryParam extends PageReqParam {

  private Long userId;
  private String userIds;
  private String userName;
  private String nickName;
  private String phonenumber;
  private String email;
  private Date[] createTime;
  private Long deptId;
  private String status;
  private Long roleId;
  private String excludeUserIds;
}
