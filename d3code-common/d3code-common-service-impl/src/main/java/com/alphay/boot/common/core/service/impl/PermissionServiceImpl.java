package com.alphay.boot.common.core.service.impl;

import com.alphay.boot.common.core.service.PermissionService;
import com.alphay.boot.system.api.RemotePermissionService;
import java.util.Set;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 权限服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class PermissionServiceImpl implements PermissionService {

  @DubboReference private RemotePermissionService remotePermissionService;

  @Override
  public Set<String> getRolePermission(Long userId) {
    return remotePermissionService.getRolePermission(userId);
  }

  @Override
  public Set<String> getMenuPermission(Long userId) {
    return remotePermissionService.getMenuPermission(userId);
  }
}
