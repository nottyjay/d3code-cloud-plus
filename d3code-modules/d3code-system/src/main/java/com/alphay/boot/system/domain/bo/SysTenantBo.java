package com.alphay.boot.system.domain.bo;

import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.mybatis.core.domain.BaseEntity;
import com.alphay.boot.system.domain.SysTenant;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户业务对象 sys_tenant
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysTenant.class, reverseConvertGenerate = false)
public class SysTenantBo extends BaseEntity {

  /** id */
  @NotNull(
      message = "id不能为空",
      groups = {EditGroup.class})
  private Long id;

  /** 租户编号 */
  private String tenantId;

  /** 联系人 */
  @NotBlank(
      message = "联系人不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String contactUserName;

  /** 联系电话 */
  @NotBlank(
      message = "联系电话不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String contactPhone;

  /** 企业名称 */
  @NotBlank(
      message = "企业名称不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String companyName;

  /** 用户名（创建系统用户） */
  @NotBlank(
      message = "用户名不能为空",
      groups = {AddGroup.class})
  private String username;

  /** 密码（创建系统用户） */
  @NotBlank(
      message = "密码不能为空",
      groups = {AddGroup.class})
  private String password;

  /** 统一社会信用代码 */
  private String licenseNumber;

  /** 地址 */
  private String address;

  /** 域名 */
  private String domain;

  /** 企业简介 */
  private String intro;

  /** 备注 */
  private String remark;

  /** 租户套餐编号 */
  @NotNull(
      message = "租户套餐不能为空",
      groups = {AddGroup.class})
  private Long packageId;

  /** 过期时间 */
  private Date expireTime;

  /** 用户数量（-1不限制） */
  private Long accountCount;

  /** 租户状态（0正常 1停用） */
  private String status;
}
