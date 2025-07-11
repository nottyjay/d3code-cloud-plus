package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.annotation.DataColumn;
import com.alphay.boot.common.mybatis.annotation.DataPermission;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysUser;
import com.alphay.boot.system.domain.vo.SysUserExportVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysUserMapper extends BaseMapperX<SysUser> {

  @DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
  })
  default List<SysUserVo> selectUserList(Wrapper<SysUser> queryWrapper) {
    return this.selectList(queryWrapper, SysUserVo.class);
  }

  /**
   * 根据条件分页查询用户列表
   *
   * @param queryWrapper 查询条件
   * @return 用户信息集合信息
   */
  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "u.user_id")
  })
  List<SysUserExportVo> selectUserExportList(
      @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

  /**
   * 根据条件分页查询已配用户角色列表
   *
   * @param page 分页信息
   * @param queryWrapper 查询条件
   * @return 用户信息集合信息
   */
  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "u.user_id")
  })
  Page<SysUserVo> selectAllocatedList(
      @Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

  /**
   * 根据条件分页查询未分配用户角色列表
   *
   * @param queryWrapper 查询条件
   * @return 用户信息集合信息
   */
  @DataPermission({
    @DataColumn(key = "deptName", value = "d.dept_id"),
    @DataColumn(key = "userName", value = "u.user_id")
  })
  Page<SysUserVo> selectUnallocatedList(
      @Param("page") Page<SysUser> page, @Param(Constants.WRAPPER) Wrapper<SysUser> queryWrapper);

  @DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
  })
  default long countUserById(Long userId) {
    return this.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId));
  }

  @Override
  @DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
  })
  int update(
      @Param(Constants.ENTITY) SysUser user,
      @Param(Constants.WRAPPER) Wrapper<SysUser> updateWrapper);

  @Override
  @DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
  })
  int updateById(@Param(Constants.ENTITY) SysUser user);
}
