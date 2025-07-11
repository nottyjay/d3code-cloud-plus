package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * 菜单查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysMenuQueryParam extends PageReqParam {

  private String menuName;
  private String parentId;
  private String menuType;
  private String status;
  private String visible;
}
