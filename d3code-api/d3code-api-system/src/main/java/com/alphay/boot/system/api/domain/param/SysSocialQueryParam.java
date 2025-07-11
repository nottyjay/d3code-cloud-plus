package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * @author Nottyjay
 * @since
 */
@Data
public class SysSocialQueryParam extends PageReqParam {

  private Long userId;

  private String authId;

  private String source;
}
