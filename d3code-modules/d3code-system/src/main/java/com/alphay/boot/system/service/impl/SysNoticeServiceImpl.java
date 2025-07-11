package com.alphay.boot.system.service.impl;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.ObjectUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysNoticeQueryParam;
import com.alphay.boot.system.domain.SysNotice;
import com.alphay.boot.system.domain.SysUser;
import com.alphay.boot.system.domain.bo.SysNoticeBo;
import com.alphay.boot.system.domain.vo.SysNoticeVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.alphay.boot.system.mapper.SysNoticeMapper;
import com.alphay.boot.system.mapper.SysUserMapper;
import com.alphay.boot.system.service.ISysNoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 公告 服务层实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysNoticeServiceImpl extends ServiceImplX<SysNoticeMapper, SysNotice, SysNoticeVo>
    implements ISysNoticeService {

  @Resource private SysUserMapper userMapper;

  @Override
  public PageResult<SysNoticeVo> queryPageList(SysNoticeQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 查询公告列表
   *
   * @param param 公告信息
   * @return 公告集合
   */
  @Override
  public List<SysNoticeVo> queryList(SysNoticeQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysNotice> buildQueryWrapper(SysNoticeQueryParam param) {
    LambdaQueryWrapper<SysNotice> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysNotice::getNoticeTitle, param.getNoticeTitle())
            .eqIfPresent(SysNotice::getNoticeType, param.getNoticeType());
    if (StringUtils.isNotBlank(param.getCreateByName())) {
      SysUserVo sysUser =
          userMapper.selectOne(
              new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, param.getCreateByName()),
              SysUserVo.class);
      lqw.eq(SysNotice::getCreateBy, ObjectUtils.notNullGetter(sysUser, SysUserVo::getUserId));
    }
    lqw.orderByAsc(SysNotice::getNoticeId);
    return lqw;
  }

  /**
   * 新增公告
   *
   * @param bo 公告信息
   * @return 结果
   */
  @Override
  public int insertNotice(SysNoticeBo bo) {
    SysNotice notice = MapstructUtils.convert(bo, SysNotice.class);
    return baseMapper.insert(notice);
  }

  /**
   * 修改公告
   *
   * @param bo 公告信息
   * @return 结果
   */
  @Override
  public int updateNotice(SysNoticeBo bo) {
    SysNotice notice = MapstructUtils.convert(bo, SysNotice.class);
    return baseMapper.updateById(notice);
  }

  /**
   * 删除公告对象
   *
   * @param noticeId 公告ID
   * @return 结果
   */
  @Override
  public int deleteNoticeById(Long noticeId) {
    return baseMapper.deleteById(noticeId);
  }

  /**
   * 批量删除公告信息
   *
   * @param noticeIds 需要删除的公告ID
   * @return 结果
   */
  @Override
  public int deleteNoticeByIds(Long[] noticeIds) {
    return baseMapper.deleteByIds(Arrays.asList(noticeIds));
  }
}
