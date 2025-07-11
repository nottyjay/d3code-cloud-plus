package com.alphay.boot.resource.api.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 文件信息
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class RemoteSms implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 是否成功 */
  private Boolean success;

  /** 配置标识名 如未配置取对应渠道名例如 Alibaba */
  private String configId;

  /**
   * 厂商原返回体
   *
   * <p>可自行转换为 SDK 对应的 SendSmsResponse
   */
  private String response;
}
