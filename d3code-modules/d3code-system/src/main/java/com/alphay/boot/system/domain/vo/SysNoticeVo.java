package com.alphay.boot.system.domain.vo;

import com.alphay.boot.common.translation.annotation.Translation;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.system.domain.SysNotice;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 通知公告视图对象 sys_notice
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@AutoMapper(target = SysNotice.class)
public class SysNoticeVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 公告ID */
  private Long noticeId;

  /** 公告标题 */
  private String noticeTitle;

  /** 公告类型（1通知 2公告） */
  private String noticeType;

  /** 公告内容 */
  private String noticeContent;

  /** 公告状态（0正常 1关闭） */
  private String status;

  /** 备注 */
  private String remark;

  /** 创建者 */
  private Long createBy;

  /** 创建人名称 */
  @Translation(type = TransConstant.USER_ID_TO_NAME, mapper = "createBy")
  private String createByName;

  /** 创建时间 */
  private Date createTime;
}
