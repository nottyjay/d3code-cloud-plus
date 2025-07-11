package com.alphay.boot.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysDeptQueryParam;
import com.alphay.boot.system.domain.SysDept;
import com.alphay.boot.system.domain.bo.SysDeptBo;
import com.alphay.boot.system.domain.vo.SysDeptVo;
import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysDeptService extends IServiceX<SysDept, SysDeptVo> {

  /**
   * 分页查询部门管理数据
   *
   * @param param 部门信息
   * @return 部门信息集合
   */
  PageResult<SysDeptVo> queryPageList(SysDeptQueryParam param);

  /**
   * 查询部门管理数据
   *
   * @param param 部门信息
   * @return 部门信息集合
   */
  List<SysDeptVo> queryList(SysDeptQueryParam param);

  /**
   * 查询部门树结构信息
   *
   * @param param 部门信息
   * @return 部门树信息集合
   */
  List<Tree<Long>> selectDeptTreeList(SysDeptQueryParam param);

  /**
   * 构建前端所需要下拉树结构
   *
   * @param depts 部门列表
   * @return 下拉树结构列表
   */
  List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts);

  /**
   * 根据角色ID查询部门树信息
   *
   * @param roleId 角色ID
   * @return 选中部门列表
   */
  List<Long> selectDeptListByRoleId(Long roleId);

  /**
   * 通过部门ID串查询部门
   *
   * @param deptIds 部门id串
   * @return 部门列表信息
   */
  List<SysDeptVo> selectDeptByIds(List<Long> deptIds);

  /**
   * 通过部门ID查询部门名称
   *
   * @param deptIds 部门ID串逗号分隔
   * @return 部门名称串逗号分隔
   */
  String selectDeptNameByIds(String deptIds);

  /**
   * 根据ID查询所有子部门数（正常状态）
   *
   * @param deptId 部门ID
   * @return 子部门数
   */
  long selectNormalChildrenDeptById(Long deptId);

  /**
   * 是否存在部门子节点
   *
   * @param deptId 部门ID
   * @return 结果
   */
  boolean hasChildByDeptId(Long deptId);

  /**
   * 查询部门是否存在用户
   *
   * @param deptId 部门ID
   * @return 结果 true 存在 false 不存在
   */
  boolean checkDeptExistUser(Long deptId);

  /**
   * 校验部门名称是否唯一
   *
   * @param dept 部门信息
   * @return 结果
   */
  boolean checkDeptNameUnique(SysDeptBo dept);

  /**
   * 校验部门是否有数据权限
   *
   * @param deptId 部门id
   */
  void checkDeptDataScope(Long deptId);

  /**
   * 新增保存部门信息
   *
   * @param bo 部门信息
   * @return 结果
   */
  int insertDept(SysDeptBo bo);

  /**
   * 修改保存部门信息
   *
   * @param bo 部门信息
   * @return 结果
   */
  int updateDept(SysDeptBo bo);

  /**
   * 删除部门管理信息
   *
   * @param deptId 部门ID
   * @return 结果
   */
  int deleteDeptById(Long deptId);

  /**
   * 查询部门(简单查询)
   *
   * @return 部门列表
   */
  List<SysDeptVo> selectDeptsSimple();
}
