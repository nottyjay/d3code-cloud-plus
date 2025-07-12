package com.alphay.boot.system.dubbo;

import com.alphay.boot.system.api.RemotePermissionService;
import com.alphay.boot.system.service.ISysPermissionService;
import jakarta.annotation.Resource;
import java.util.Set;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 权限服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemotePermissionServiceImpl implements RemotePermissionService {

  @Resource private ISysPermissionService permissionService;

  @Override
  public Set<String> getRolePermission(Long userId) {
    return permissionService.getRolePermission(userId);
  }

  @Override
  public Set<String> getMenuPermission(Long userId) {
    return permissionService.getMenuPermission(userId);
  }
}
