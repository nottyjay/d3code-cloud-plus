package com.alphay.boot.system.service.impl;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysLogininforQueryParam;
import com.alphay.boot.system.domain.SysLogininfor;
import com.alphay.boot.system.domain.bo.SysLogininforBo;
import com.alphay.boot.system.domain.vo.SysLogininforVo;
import com.alphay.boot.system.mapper.SysLogininforMapper;
import com.alphay.boot.system.service.ISysLogininforService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
public class SysLogininforServiceImpl
    extends ServiceImplX<SysLogininforMapper, SysLogininfor, SysLogininforVo>
    implements ISysLogininforService {

  @Override
  public PageResult<SysLogininforVo> queryPageList(SysLogininforQueryParam param) {
    LambdaQueryWrapper<SysLogininfor> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysLogininfor::getIpaddr, param.getIpaddr())
            .eqIfPresent(SysLogininfor::getStatus, param.getStatus())
            .likeIfPresent(SysLogininfor::getUserName, param.getUserName())
            .betweenIfPresent(SysLogininfor::getLoginTime, param.getLoginTime());
    if (StringUtils.isBlank(param.getOrderByColumn())) {
      lqw.orderByDesc(SysLogininfor::getInfoId);
    }
    return listPageVo(param, lqw);
  }

  /**
   * 新增系统登录日志
   *
   * @param bo 访问日志对象
   */
  @Override
  public void insertLogininfor(SysLogininforBo bo) {
    SysLogininfor logininfor = MapstructUtils.convert(bo, SysLogininfor.class);
    logininfor.setLoginTime(new Date());
    baseMapper.insert(logininfor);
  }

  /**
   * 查询系统登录日志集合
   *
   * @param param 访问日志对象
   * @return 登录记录集合
   */
  @Override
  public List<SysLogininforVo> queryList(SysLogininforQueryParam param) {
    return listVo(
        this.lambdaQueryWrapper()
            .likeIfPresent(SysLogininfor::getIpaddr, param.getIpaddr())
            .eqIfPresent(SysLogininfor::getStatus, param.getStatus())
            .likeIfPresent(SysLogininfor::getUserName, param.getUserName())
            .betweenIfPresent(SysLogininfor::getLoginTime, param.getLoginTime())
            .orderByDesc(SysLogininfor::getInfoId));
  }

  /**
   * 批量删除系统登录日志
   *
   * @param infoIds 需要删除的登录日志ID
   * @return 结果
   */
  @Override
  public int deleteLogininforByIds(Long[] infoIds) {
    return baseMapper.deleteByIds(Arrays.asList(infoIds));
  }

  /** 清空系统登录日志 */
  @Override
  public void cleanLogininfor() {
    baseMapper.delete(new LambdaQueryWrapper<>());
  }
}
