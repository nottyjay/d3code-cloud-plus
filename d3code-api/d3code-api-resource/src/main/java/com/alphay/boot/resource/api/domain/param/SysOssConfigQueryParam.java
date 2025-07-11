package com.alphay.boot.resource.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * OSS 配置查询参数
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysOssConfigQueryParam extends PageReqParam {

  private String configKey;

  private String bucketName;

  private String status;
}
