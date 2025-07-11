package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysLogininforQueryParam;
import com.alphay.boot.system.domain.SysLogininfor;
import com.alphay.boot.system.domain.bo.SysLogininforBo;
import com.alphay.boot.system.domain.vo.SysLogininforVo;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysLogininforService extends IServiceX<SysLogininfor, SysLogininforVo> {

  PageResult<SysLogininforVo> queryPageList(SysLogininforQueryParam param);

  /**
   * 新增系统登录日志
   *
   * @param bo 访问日志对象
   */
  void insertLogininfor(SysLogininforBo bo);

  /**
   * 查询系统登录日志集合
   *
   * @param param 访问日志对象
   * @return 登录记录集合
   */
  List<SysLogininforVo> queryList(SysLogininforQueryParam param);

  /**
   * 批量删除系统登录日志
   *
   * @param infoIds 需要删除的登录日志ID
   * @return 结果
   */
  int deleteLogininforByIds(Long[] infoIds);

  /** 清空系统登录日志 */
  void cleanLogininfor();
}
