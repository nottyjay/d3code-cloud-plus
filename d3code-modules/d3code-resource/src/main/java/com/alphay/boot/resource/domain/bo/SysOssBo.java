package com.alphay.boot.resource.domain.bo;

import com.alphay.boot.common.mybatis.core.domain.BaseEntity;
import com.alphay.boot.resource.domain.SysOss;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS对象存储分页查询对象 sys_oss
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOss.class, reverseConvertGenerate = false)
public class SysOssBo extends BaseEntity {

  /** ossId */
  private Long ossId;

  /** 文件名 */
  private String fileName;

  /** 原名 */
  private String originalName;

  /** 文件后缀名 */
  private String fileSuffix;

  /** URL地址 */
  private String url;

  /** 扩展字段 */
  private String ext1;

  /** 服务商 */
  private String service;
}
