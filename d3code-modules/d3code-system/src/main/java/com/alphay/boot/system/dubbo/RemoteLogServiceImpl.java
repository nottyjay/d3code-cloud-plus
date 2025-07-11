package com.alphay.boot.system.dubbo;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.system.api.RemoteLogService;
import com.alphay.boot.system.api.domain.bo.RemoteLogininforBo;
import com.alphay.boot.system.api.domain.bo.RemoteOperLogBo;
import com.alphay.boot.system.domain.bo.SysLogininforBo;
import com.alphay.boot.system.domain.bo.SysOperLogBo;
import com.alphay.boot.system.service.ISysLogininforService;
import com.alphay.boot.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志记录
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteLogServiceImpl implements RemoteLogService {

  private final ISysOperLogService operLogService;
  private final ISysLogininforService logininforService;

  /**
   * 保存系统日志
   *
   * @param remoteOperLogBo 日志实体
   */
  @Async
  @Override
  public void saveLog(RemoteOperLogBo remoteOperLogBo) {
    SysOperLogBo sysOperLogBo = MapstructUtils.convert(remoteOperLogBo, SysOperLogBo.class);
    operLogService.insertOperlog(sysOperLogBo);
  }

  /**
   * 保存访问记录
   *
   * @param remoteLogininforBo 访问实体
   */
  @Async
  @Override
  public void saveLogininfor(RemoteLogininforBo remoteLogininforBo) {
    SysLogininforBo sysLogininforBo =
        MapstructUtils.convert(remoteLogininforBo, SysLogininforBo.class);
    logininforService.insertLogininfor(sysLogininforBo);
  }
}
