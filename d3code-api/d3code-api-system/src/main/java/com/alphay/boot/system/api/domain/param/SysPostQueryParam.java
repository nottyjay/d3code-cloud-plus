package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 岗位查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysPostQueryParam extends PageReqParam {

  private String postCode;
  private String postCategory;
  private String postName;
  private String status;
  private Long deptId;
  private Long belongDeptId;
  private Date[] createTime;
}
