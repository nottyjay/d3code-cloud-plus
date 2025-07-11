package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysNoticeQueryParam;
import com.alphay.boot.system.domain.SysNotice;
import com.alphay.boot.system.domain.bo.SysNoticeBo;
import com.alphay.boot.system.domain.vo.SysNoticeVo;
import java.util.List;

/**
 * 公告 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysNoticeService extends IServiceX<SysNotice, SysNoticeVo> {

  PageResult<SysNoticeVo> queryPageList(SysNoticeQueryParam param);

  /**
   * 查询公告列表
   *
   * @param param 公告信息
   * @return 公告集合
   */
  List<SysNoticeVo> queryList(SysNoticeQueryParam param);

  /**
   * 新增公告
   *
   * @param bo 公告信息
   * @return 结果
   */
  int insertNotice(SysNoticeBo bo);

  /**
   * 修改公告
   *
   * @param bo 公告信息
   * @return 结果
   */
  int updateNotice(SysNoticeBo bo);

  /**
   * 删除公告信息
   *
   * @param noticeId 公告ID
   * @return 结果
   */
  int deleteNoticeById(Long noticeId);

  /**
   * 批量删除公告信息
   *
   * @param noticeIds 需要删除的公告ID
   * @return 结果
   */
  int deleteNoticeByIds(Long[] noticeIds);
}
