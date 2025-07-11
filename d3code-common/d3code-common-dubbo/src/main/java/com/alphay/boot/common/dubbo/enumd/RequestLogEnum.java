package com.alphay.boot.common.dubbo.enumd;

import lombok.AllArgsConstructor;

/**
 * 请求日志泛型
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AllArgsConstructor
public enum RequestLogEnum {

  /** info 基础信息 */
  INFO,

  /** param 参数信息 */
  PARAM,

  /** full 全部 */
  FULL;
}
