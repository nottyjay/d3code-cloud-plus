package com.alphay.boot.system.domain.bo;

import com.alphay.boot.common.core.validate.AddGroup;
import com.alphay.boot.common.core.validate.EditGroup;
import com.alphay.boot.common.mybatis.core.domain.BaseEntity;
import com.alphay.boot.system.domain.SysClient;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 授权管理业务对象 sys_client
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysClient.class, reverseConvertGenerate = false)
public class SysClientBo extends BaseEntity {

  /** id */
  @NotNull(
      message = "id不能为空",
      groups = {EditGroup.class})
  private Long id;

  /** 客户端id */
  private String clientId;

  /** 客户端key */
  @NotBlank(
      message = "客户端key不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String clientKey;

  /** 客户端秘钥 */
  @NotBlank(
      message = "客户端秘钥不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private String clientSecret;

  /** 授权类型 */
  @NotNull(
      message = "授权类型不能为空",
      groups = {AddGroup.class, EditGroup.class})
  private List<String> grantTypeList;

  /** 授权类型 */
  private String grantType;

  /** 设备类型 */
  private String deviceType;

  /** token活跃超时时间 */
  private Long activeTimeout;

  /** token固定超时时间 */
  private Long timeout;

  /** 状态（0正常 1停用） */
  private String status;
}
