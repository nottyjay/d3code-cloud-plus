package com.alphay.boot.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

  /** 角色ID */
  @TableId(type = IdType.INPUT)
  private Long roleId;

  /** 菜单ID */
  private Long menuId;
}
