package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.annotation.DataColumn;
import com.alphay.boot.common.mybatis.annotation.DataPermission;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.common.mybatis.helper.DataBaseHelper;
import com.alphay.boot.system.domain.SysDept;
import com.alphay.boot.system.domain.vo.SysDeptVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 部门管理 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysDeptMapper extends BaseMapperX<SysDept> {

  /**
   * 查询部门管理数据
   *
   * @param queryWrapper 查询条件
   * @return 部门信息集合
   */
  @DataPermission({@DataColumn(key = "deptName", value = "dept_id")})
  default List<SysDeptVo> selectDeptList(Wrapper<SysDept> queryWrapper) {
    return this.selectList(queryWrapper, SysDeptVo.class);
  }

  /**
   * 统计指定部门ID的部门数量
   *
   * @param deptId 部门ID
   * @return 该部门ID的部门数量
   */
  @DataPermission({@DataColumn(key = "deptName", value = "dept_id")})
  default long countDeptById(Long deptId) {
    return this.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptId, deptId));
  }

  /**
   * 根据父部门ID查询其所有子部门的列表
   *
   * @param parentId 父部门ID
   * @return 包含子部门的列表
   */
  default List<SysDept> selectListByParentId(Long parentId) {
    return this.selectList(
        new LambdaQueryWrapper<SysDept>()
            .select(SysDept::getDeptId)
            .apply(DataBaseHelper.findInSet(parentId, "ancestors")));
  }

  /**
   * 根据角色ID查询部门树信息
   *
   * @param roleId 角色ID
   * @param deptCheckStrictly 部门树选择项是否关联显示
   * @return 选中部门列表
   */
  List<Long> selectDeptListByRoleId(
      @Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);
}
