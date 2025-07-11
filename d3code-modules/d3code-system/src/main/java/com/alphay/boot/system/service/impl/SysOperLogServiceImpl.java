package com.alphay.boot.system.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysOperLogQueryParam;
import com.alphay.boot.system.domain.SysOperLog;
import com.alphay.boot.system.domain.bo.SysOperLogBo;
import com.alphay.boot.system.domain.vo.SysOperLogVo;
import com.alphay.boot.system.mapper.SysOperLogMapper;
import com.alphay.boot.system.service.ISysOperLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 操作日志 服务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysOperLogServiceImpl extends ServiceImplX<SysOperLogMapper, SysOperLog, SysOperLogVo>
    implements ISysOperLogService {

  @Override
  public PageResult<SysOperLogVo> queryPageList(SysOperLogQueryParam param) {
    LambdaQueryWrapper<SysOperLog> lqw = buildQueryWrapper(param);
    if (StringUtils.isBlank(param.getOrderByColumn())) {
      lqw.orderByDesc(SysOperLog::getOperId);
    }
    return listPageVo(param, lqw);
  }

  private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(SysOperLogQueryParam param) {
    return this.lambdaQueryWrapper()
        .likeIfPresent(SysOperLog::getOperIp, param.getOperIp())
        .likeIfPresent(SysOperLog::getTitle, param.getTitle())
        .eq(
            param.getBusinessType() != null && param.getBusinessType() > 0,
            SysOperLog::getBusinessType,
            param.getBusinessType())
        .func(
            f -> {
              if (ArrayUtil.isNotEmpty(param.getBusinessTypes())) {
                f.in(SysOperLog::getBusinessType, Arrays.asList(param.getBusinessTypes()));
              }
            })
        .eqIfPresent(SysOperLog::getStatus, param.getStatus())
        .likeIfPresent(SysOperLog::getOperName, param.getOperName())
        .betweenIfPresent(SysOperLog::getOperTime, param.getOperTime());
  }

  /**
   * 新增操作日志
   *
   * @param bo 操作日志对象
   */
  @Override
  public void insertOperlog(SysOperLogBo bo) {
    SysOperLog operLog = MapstructUtils.convert(bo, SysOperLog.class);
    operLog.setOperTime(new Date());
    baseMapper.insert(operLog);
  }

  /**
   * 查询系统操作日志集合
   *
   * @param param 操作日志对象
   * @return 操作日志集合
   */
  @Override
  public List<SysOperLogVo> queryList(SysOperLogQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  /**
   * 批量删除系统操作日志
   *
   * @param operIds 需要删除的操作日志ID
   * @return 结果
   */
  @Override
  public int deleteOperLogByIds(Long[] operIds) {
    return baseMapper.deleteByIds(Arrays.asList(operIds));
  }

  /** 清空操作日志 */
  @Override
  public void cleanOperLog() {
    baseMapper.delete(new LambdaQueryWrapper<>());
  }
}
