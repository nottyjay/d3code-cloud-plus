package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import java.util.Date;
import lombok.Data;

/**
 * 系统操作日志查询对象
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SysOperLogQueryParam extends PageReqParam {

  private String operIp;
  private String title;
  private Integer businessType;
  private Integer[] businessTypes;
  private String operName;
  private String status;
  private Date[] operTime;
}
