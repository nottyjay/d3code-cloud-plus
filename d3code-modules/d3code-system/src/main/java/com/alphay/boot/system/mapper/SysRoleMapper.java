package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.annotation.DataColumn;
import com.alphay.boot.common.mybatis.annotation.DataPermission;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysRole;
import com.alphay.boot.system.domain.vo.SysRoleVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysRoleMapper extends BaseMapperX<SysRole> {

  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "r.create_by")
  })
  Page<SysRoleVo> selectPageRoleList(
      @Param("page") Page<SysRole> page, @Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

  /**
   * 根据条件分页查询角色数据
   *
   * @param queryWrapper 查询条件
   * @return 角色数据集合信息
   */
  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "r.create_by")
  })
  List<SysRoleVo> selectRoleList(@Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "r.create_by")
  })
  SysRoleVo selectRoleById(Long roleId);

  /**
   * 根据用户ID查询角色
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  List<SysRoleVo> selectRolePermissionByUserId(Long userId);

  /**
   * 根据用户ID查询角色
   *
   * @param userId 用户ID
   * @return 角色列表
   */
  List<SysRoleVo> selectRolesByUserId(Long userId);
}
