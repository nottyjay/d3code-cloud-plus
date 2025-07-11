package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysUserRole;
import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysUserRoleMapper extends BaseMapperX<SysUserRole> {

  List<Long> selectUserIdsByRoleId(Long roleId);
}
