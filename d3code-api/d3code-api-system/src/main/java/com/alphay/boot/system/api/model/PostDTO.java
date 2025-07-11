package com.alphay.boot.system.api.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 岗位
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public class PostDTO implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 岗位ID */
  private Long postId;

  /** 部门id */
  private Long deptId;

  /** 岗位编码 */
  private String postCode;

  /** 岗位名称 */
  private String postName;

  /** 岗位类别编码 */
  private String postCategory;
}
