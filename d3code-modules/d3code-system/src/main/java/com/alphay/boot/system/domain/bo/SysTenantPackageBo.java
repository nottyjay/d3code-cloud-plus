package com.alphay.boot.system.domain.bo;

import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.mybatis.core.domain.BaseEntity;
import com.alphay.boot.system.domain.SysTenantPackage;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户套餐业务对象 sys_tenant_package
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenantPackage.class, reverseConvertGenerate = false)
public class SysTenantPackageBo extends BaseEntity {

  /** 租户套餐id */
  @NotNull(
      message = "租户套餐id不能为空",
      groups = {EditGroup.class})
  private Long packageId;

  /** 套餐名称 */
  @NotBlank(
      message = "套餐名称不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String packageName;

  /** 关联菜单id */
  @AutoMapping(
      target = "menuIds",
      expression =
          "java(com.alphay.boot.common.core.utils.StringUtils.join(source.getMenuIds(), \",\"))")
  private Long[] menuIds;

  /** 备注 */
  private String remark;

  /** 菜单树选择项是否关联显示 */
  private Boolean menuCheckStrictly;

  /** 状态（0正常 1停用） */
  private String status;
}
