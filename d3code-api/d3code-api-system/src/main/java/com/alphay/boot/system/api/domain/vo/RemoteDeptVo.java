package com.alphay.boot.system.api.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class RemoteDeptVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 部门ID */
  private Long deptId;

  /** 父部门ID */
  private Long parentId;

  /** 部门名称 */
  private String deptName;
}
