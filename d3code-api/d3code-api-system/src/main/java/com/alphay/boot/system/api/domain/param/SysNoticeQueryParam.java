package com.alphay.boot.system.api.domain.param;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import lombok.Data;

/**
 * 公告查询对象
 *
 * @author Nottyjay
 * @since
 */
@Data
public class SysNoticeQueryParam extends PageReqParam {

  private String noticeTitle;
  private String noticeType;
  private String createByName;
}
