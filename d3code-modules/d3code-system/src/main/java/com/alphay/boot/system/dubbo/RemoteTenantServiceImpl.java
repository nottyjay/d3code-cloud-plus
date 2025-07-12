package com.alphay.boot.system.dubbo;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.system.api.RemoteTenantService;
import com.alphay.boot.system.api.domain.param.SysTenantQueryParam;
import com.alphay.boot.system.api.domain.vo.RemoteTenantVo;
import com.alphay.boot.system.domain.vo.SysTenantVo;
import com.alphay.boot.system.service.ISysTenantService;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemoteTenantServiceImpl implements RemoteTenantService {

  @Resource private ISysTenantService tenantService;

  /** 根据租户id获取租户详情 */
  @Override
  public RemoteTenantVo queryByTenantId(String tenantId) {
    SysTenantVo vo = tenantService.getVoByTenantId(tenantId);
    return MapstructUtils.convert(vo, RemoteTenantVo.class);
  }

  /** 获取租户列表 */
  @Override
  public List<RemoteTenantVo> queryList() {
    List<SysTenantVo> list = tenantService.queryList(new SysTenantQueryParam());
    return MapstructUtils.convert(list, RemoteTenantVo.class);
  }
}
