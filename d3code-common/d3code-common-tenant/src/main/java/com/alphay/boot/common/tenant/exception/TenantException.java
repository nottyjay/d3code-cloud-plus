package com.alphay.boot.common.tenant.exception;

import com.alphay.boot.common.core.exception.base.BaseException;
import java.io.Serial;

/**
 * 租户异常类
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class TenantException extends BaseException {

  @Serial private static final long serialVersionUID = 1L;

  public TenantException(String code, Object... args) {
    super("tenant", code, args, null);
  }
}
