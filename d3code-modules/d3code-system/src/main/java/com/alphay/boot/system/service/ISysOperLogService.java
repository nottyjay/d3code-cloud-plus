package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysOperLogQueryParam;
import com.alphay.boot.system.domain.SysOperLog;
import com.alphay.boot.system.domain.bo.SysOperLogBo;
import com.alphay.boot.system.domain.vo.SysOperLogVo;
import java.util.List;

/**
 * 操作日志 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysOperLogService extends IServiceX<SysOperLog, SysOperLogVo> {

  PageResult<SysOperLogVo> queryPageList(SysOperLogQueryParam param);

  /**
   * 新增操作日志
   *
   * @param bo 操作日志对象
   */
  void insertOperlog(SysOperLogBo bo);

  /**
   * 查询系统操作日志集合
   *
   * @param param 操作日志对象
   * @return 操作日志集合
   */
  List<SysOperLogVo> queryList(SysOperLogQueryParam param);

  /**
   * 批量删除系统操作日志
   *
   * @param operIds 需要删除的操作日志ID
   * @return 结果
   */
  int deleteOperLogByIds(Long[] operIds);

  /** 清空操作日志 */
  void cleanOperLog();
}
