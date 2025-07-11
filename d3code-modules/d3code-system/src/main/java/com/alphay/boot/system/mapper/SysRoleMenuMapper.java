package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysRoleMenu;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.List;

/**
 * 角色与菜单关联表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysRoleMenuMapper extends BaseMapperX<SysRoleMenu> {

  /**
   * 根据菜单ID串删除关联关系
   *
   * @param menuIds 菜单ID串
   * @return 结果
   */
  default int deleteByMenuIds(List<Long> menuIds) {
    return this.delete(new LambdaUpdateWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId, menuIds));
  }
}
