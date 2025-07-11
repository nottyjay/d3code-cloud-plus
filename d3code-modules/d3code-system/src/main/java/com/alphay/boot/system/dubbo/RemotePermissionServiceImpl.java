package com.alphay.boot.system.dubbo;

import com.alphay.boot.system.api.RemotePermissionService;
import com.alphay.boot.system.service.ISysPermissionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 权限服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemotePermissionServiceImpl implements RemotePermissionService {

  private final ISysPermissionService permissionService;

  @Override
  public Set<String> getRolePermission(Long userId) {
    return permissionService.getRolePermission(userId);
  }

  @Override
  public Set<String> getMenuPermission(Long userId) {
    return permissionService.getMenuPermission(userId);
  }
}
