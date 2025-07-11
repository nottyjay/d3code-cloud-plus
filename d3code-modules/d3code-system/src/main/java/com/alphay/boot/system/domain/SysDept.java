package com.alphay.boot.system.domain;

import com.alphay.boot.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表 sys_dept
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends TenantEntity {

  @Serial private static final long serialVersionUID = 1L;

  /** 部门ID */
  @TableId(value = "dept_id")
  private Long deptId;

  /** 父部门ID */
  private Long parentId;

  /** 部门名称 */
  private String deptName;

  /** 部门类别编码 */
  private String deptCategory;

  /** 显示顺序 */
  private Integer orderNum;

  /** 负责人 */
  private Long leader;

  /** 联系电话 */
  private String phone;

  /** 邮箱 */
  private String email;

  /** 部门状态:0正常,1停用 */
  private String status;

  /** 删除标志（0代表存在 2代表删除） */
  @TableLogic private String delFlag;

  /** 祖级列表 */
  private String ancestors;

  /** 子菜单 */
  @TableField(exist = false)
  private List<SysDept> children = new ArrayList<>();
}
