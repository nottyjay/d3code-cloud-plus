package com.alphay.boot.system.domain;

import com.alphay.boot.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表 sys_dict_type
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends TenantEntity {

  /** 字典主键 */
  @TableId(value = "dict_id")
  private Long dictId;

  /** 字典名称 */
  private String dictName;

  /** 字典类型 */
  private String dictType;

  /** 备注 */
  private String remark;
}
