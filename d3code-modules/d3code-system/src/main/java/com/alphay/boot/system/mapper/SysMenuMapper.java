package com.alphay.boot.system.mapper;

import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysMenu;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 菜单表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysMenuMapper extends BaseMapperX<SysMenu> {

  /**
   * 根据用户查询系统菜单列表
   *
   * @param queryWrapper 查询条件
   * @return 菜单列表
   */
  List<SysMenu> selectMenuListByUserId(@Param(Constants.WRAPPER) Wrapper<SysMenu> queryWrapper);

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return 权限列表
   */
  List<String> selectMenuPermsByUserId(Long userId);

  /**
   * 根据角色ID查询权限
   *
   * @param roleId 角色ID
   * @return 权限列表
   */
  List<String> selectMenuPermsByRoleId(Long roleId);

  /**
   * 根据用户ID查询菜单
   *
   * @return 菜单列表
   */
  default List<SysMenu> selectMenuTreeAll() {
    LambdaQueryWrapper<SysMenu> lqw =
        new LambdaQueryWrapper<SysMenu>()
            .in(SysMenu::getMenuType, SystemConstants.TYPE_DIR, SystemConstants.TYPE_MENU)
            .eq(SysMenu::getStatus, SystemConstants.NORMAL)
            .orderByAsc(SysMenu::getParentId)
            .orderByAsc(SysMenu::getOrderNum);
    return this.selectList(lqw);
  }

  /**
   * 根据用户ID查询菜单
   *
   * @param userId 用户ID
   * @return 菜单列表
   */
  List<SysMenu> selectMenuTreeByUserId(Long userId);

  /**
   * 根据角色ID查询菜单树信息
   *
   * @param roleId 角色ID
   * @param menuCheckStrictly 菜单树选择项是否关联显示
   * @return 选中菜单列表
   */
  List<Long> selectMenuListByRoleId(
      @Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);
}
