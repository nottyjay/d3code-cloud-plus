package com.alphay.boot.resource.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * OSS存储查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysOssQueryParam extends PageReqParam {

  private String fileName;
  private String originalName;
  private String fileSuffix;
  private String url;
  private Date[] createTime;
  private String createBy;
  private String service;
}
