package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 角色查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysRoleQueryParam extends PageReqParam {

  private Long roleId;
  private String roleName;
  private String status;
  private String roleKey;

  private Date[] createTime;
}
