package com.alphay.boot.system.domain;

import com.alphay.boot.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 授权管理对象 sys_client
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2023-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_client")
public class SysClient extends BaseEntity {

  @Serial private static final long serialVersionUID = 1L;

  /** id */
  @TableId(value = "id")
  private Long id;

  /** 客户端id */
  private String clientId;

  /** 客户端key */
  private String clientKey;

  /** 客户端秘钥 */
  private String clientSecret;

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

  /** 删除标志（0代表存在 2代表删除） */
  @TableLogic private String delFlag;
}
