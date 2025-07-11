package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 部门查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysDeptQueryParam extends PageReqParam {

  private Long deptId;
  private String delFlag;
  private Long parentId;
  private String deptName;
  private String deptCategory;
  private String status;
  private Date[] createTime;

  private Long belongDeptId;
}
