package com.alphay.boot.resource.domain.vo;

import com.alphay.boot.resource.domain.SysOssConfig;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 对象存储配置视图对象 sys_oss_config
 *
 * @author Nottyjay
 * @since 1.0.0
 * @author Nottyjay
 * @since 1.0.0
 * @date 2021-08-13
 */
@Data
@AutoMapper(target = SysOssConfig.class)
public class SysOssConfigVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 主键 */
  private Long ossConfigId;

  /** 配置key */
  private String configKey;

  /** accessKey */
  private String accessKey;

  /** 秘钥 */
  private String secretKey;

  /** 桶名称 */
  private String bucketName;

  /** 前缀 */
  private String prefix;

  /** 访问站点 */
  private String endpoint;

  /** 自定义域名 */
  private String domain;

  /** 是否https（Y=是,N=否） */
  private String isHttps;

  /** 域 */
  private String region;

  /** 是否默认（0=是,1=否） */
  private String status;

  /** 扩展字段 */
  private String ext1;

  /** 备注 */
  private String remark;

  /** 桶权限类型(0private 1public 2custom) */
  private String accessPolicy;
}
