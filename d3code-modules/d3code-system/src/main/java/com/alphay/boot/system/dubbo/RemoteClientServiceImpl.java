package com.alphay.boot.system.dubbo;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.system.api.RemoteClientService;
import com.alphay.boot.system.api.domain.vo.RemoteClientVo;
import com.alphay.boot.system.domain.vo.SysClientVo;
import com.alphay.boot.system.service.ISysClientService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 客户端服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemoteClientServiceImpl implements RemoteClientService {

  @Resource private ISysClientService sysClientService;

  /** 根据客户端id获取客户端详情 */
  @Override
  public RemoteClientVo queryByClientId(String clientId) {
    SysClientVo vo = sysClientService.queryByClientId(clientId);
    return MapstructUtils.convert(vo, RemoteClientVo.class);
  }
}
