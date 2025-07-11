package com.alphay.boot.system.domain;

import com.alphay.boot.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表 sys_post
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends TenantEntity {

  /** 岗位序号 */
  @TableId(value = "post_id")
  private Long postId;

  /** 部门id */
  private Long deptId;

  /** 岗位编码 */
  private String postCode;

  /** 岗位名称 */
  private String postName;

  /** 岗位类别编码 */
  private String postCategory;

  /** 岗位排序 */
  private Integer postSort;

  /** 状态（0正常 1停用） */
  private String status;

  /** 备注 */
  private String remark;
}
