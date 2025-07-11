package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.*;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.mybatis.helper.DataBaseHelper;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.system.api.domain.param.SysDeptQueryParam;
import com.alphay.boot.system.domain.SysDept;
import com.alphay.boot.system.domain.SysRole;
import com.alphay.boot.system.domain.SysUser;
import com.alphay.boot.system.domain.bo.SysDeptBo;
import com.alphay.boot.system.domain.vo.SysDeptVo;
import com.alphay.boot.system.mapper.SysDeptMapper;
import com.alphay.boot.system.mapper.SysRoleMapper;
import com.alphay.boot.system.mapper.SysUserMapper;
import com.alphay.boot.system.service.ISysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 部门管理 服务实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysDeptServiceImpl extends ServiceImplX<SysDeptMapper, SysDept, SysDeptVo>
    implements ISysDeptService {

  @Resource private SysRoleMapper roleMapper;
  @Resource private SysUserMapper userMapper;

  /**
   * 分页查询部门管理数据
   *
   * @param param 部门信息
   * @return 部门信息集合
   */
  @Override
  public PageResult<SysDeptVo> queryPageList(SysDeptQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 查询部门管理数据
   *
   * @param param 部门信息
   * @return 部门信息集合
   */
  @Override
  public List<SysDeptVo> queryList(SysDeptQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  /**
   * 查询部门树结构信息
   *
   * @param param 部门信息
   * @return 部门树信息集合
   */
  @Override
  public List<Tree<Long>> selectDeptTreeList(SysDeptQueryParam param) {
    LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(param);
    List<SysDeptVo> depts = baseMapper.selectDeptList(lqw);
    return buildDeptTreeSelect(depts);
  }

  private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDeptQueryParam param) {
    LambdaQueryWrapper<SysDept> lqw =
        this.lambdaQueryWrapper()
            .eqIfPresent(SysDept::getDelFlag, SystemConstants.NORMAL)
            .eqIfPresent(SysDept::getDeptId, param.getDeptId())
            .eqIfPresent(SysDept::getParentId, param.getParentId())
            .likeIfPresent(SysDept::getDeptName, param.getDeptName())
            .likeIfPresent(SysDept::getDeptCategory, param.getDeptCategory())
            .eqIfPresent(SysDept::getStatus, param.getStatus())
            .betweenIfPresent(SysDept::getCreateTime, param.getCreateTime());
    lqw.orderByAsc(SysDept::getAncestors);
    lqw.orderByAsc(SysDept::getParentId);
    lqw.orderByAsc(SysDept::getOrderNum);
    lqw.orderByAsc(SysDept::getDeptId);
    if (ObjectUtil.isNotNull(param.getBelongDeptId())) {
      // 部门树搜索
      lqw.and(
          x -> {
            Long parentId = param.getBelongDeptId();
            List<SysDept> deptList = baseMapper.selectListByParentId(parentId);
            List<Long> deptIds = StreamUtils.toList(deptList, SysDept::getDeptId);
            deptIds.add(parentId);
            x.in(SysDept::getDeptId, deptIds);
          });
    }
    return lqw;
  }

  /**
   * 构建前端所需要下拉树结构
   *
   * @param depts 部门列表
   * @return 下拉树结构列表
   */
  @Override
  public List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts) {
    if (CollUtil.isEmpty(depts)) {
      return CollUtil.newArrayList();
    }
    return TreeBuildUtils.buildMultiRoot(
        depts,
        SysDeptVo::getDeptId,
        SysDeptVo::getParentId,
        (node, treeNode) ->
            treeNode
                .setId(node.getDeptId())
                .setParentId(node.getParentId())
                .setName(node.getDeptName())
                .setWeight(node.getOrderNum())
                .putExtra("disabled", SystemConstants.DISABLE.equals(node.getStatus())));
  }

  /**
   * 根据角色ID查询部门树信息
   *
   * @param roleId 角色ID
   * @return 选中部门列表
   */
  @Override
  public List<Long> selectDeptListByRoleId(Long roleId) {
    SysRole role = roleMapper.selectById(roleId);
    return baseMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
  }

  /**
   * 根据部门ID查询信息
   *
   * @param deptId 部门ID
   * @return 部门信息
   */
  @Cacheable(cacheNames = CacheNames.SYS_DEPT, key = "#deptId")
  @Override
  public SysDeptVo getVoById(Serializable deptId) {
    SysDeptVo dept = super.getVoById(deptId);
    if (ObjectUtil.isNull(dept)) {
      return null;
    }
    SysDeptVo parentDept =
        this.getOneVo(
            new LambdaQueryWrapper<SysDept>()
                .select(SysDept::getDeptName)
                .eq(SysDept::getDeptId, dept.getParentId()));
    dept.setParentName(ObjectUtils.notNullGetter(parentDept, SysDeptVo::getDeptName));
    return dept;
  }

  /**
   * 通过部门ID串查询部门
   *
   * @param deptIds 部门id串
   * @return 部门列表信息
   */
  @Override
  public List<SysDeptVo> selectDeptByIds(List<Long> deptIds) {
    return baseMapper.selectDeptList(
        new LambdaQueryWrapper<SysDept>()
            .select(SysDept::getDeptId, SysDept::getDeptName, SysDept::getLeader)
            .eq(SysDept::getStatus, SystemConstants.NORMAL)
            .in(CollUtil.isNotEmpty(deptIds), SysDept::getDeptId, deptIds));
  }

  /**
   * 通过部门ID查询部门名称
   *
   * @param deptIds 部门ID串逗号分隔
   * @return 部门名称串逗号分隔
   */
  @Override
  public String selectDeptNameByIds(String deptIds) {
    List<String> list = new ArrayList<>();
    for (Long id : StringUtils.splitTo(deptIds, Convert::toLong)) {
      SysDeptVo vo = SpringUtils.getAopProxy(this).getVoById(id);
      if (ObjectUtil.isNotNull(vo)) {
        list.add(vo.getDeptName());
      }
    }
    return String.join(StringUtils.SEPARATOR, list);
  }

  /**
   * 根据ID查询所有子部门数（正常状态）
   *
   * @param deptId 部门ID
   * @return 子部门数
   */
  @Override
  public long selectNormalChildrenDeptById(Long deptId) {
    return baseMapper.selectCount(
        new LambdaQueryWrapper<SysDept>()
            .eq(SysDept::getStatus, SystemConstants.NORMAL)
            .apply(DataBaseHelper.findInSet(deptId, "ancestors")));
  }

  /**
   * 是否存在子节点
   *
   * @param deptId 部门ID
   * @return 结果
   */
  @Override
  public boolean hasChildByDeptId(Long deptId) {
    return baseMapper.exists(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId));
  }

  /**
   * 查询部门是否存在用户
   *
   * @param deptId 部门ID
   * @return 结果 true 存在 false 不存在
   */
  @Override
  public boolean checkDeptExistUser(Long deptId) {
    return userMapper.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, deptId));
  }

  /**
   * 校验部门名称是否唯一
   *
   * @param dept 部门信息
   * @return 结果
   */
  @Override
  public boolean checkDeptNameUnique(SysDeptBo dept) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getDeptName, dept.getDeptName())
                .eq(SysDept::getParentId, dept.getParentId())
                .ne(ObjectUtil.isNotNull(dept.getDeptId()), SysDept::getDeptId, dept.getDeptId()));
    return !exist;
  }

  /**
   * 校验部门是否有数据权限
   *
   * @param deptId 部门id
   */
  @Override
  public void checkDeptDataScope(Long deptId) {
    if (ObjectUtil.isNull(deptId)) {
      return;
    }
    if (LoginHelper.isSuperAdmin()) {
      return;
    }
    if (baseMapper.countDeptById(deptId) == 0) {
      throw new ServiceException("没有权限访问部门数据！");
    }
  }

  /**
   * 新增保存部门信息
   *
   * @param bo 部门信息
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)
  @Override
  public int insertDept(SysDeptBo bo) {
    SysDept info = baseMapper.selectById(bo.getParentId());
    // 如果父节点不为正常状态,则不允许新增子节点
    if (!SystemConstants.NORMAL.equals(info.getStatus())) {
      throw new ServiceException("部门停用，不允许新增");
    }
    SysDept dept = MapstructUtils.convert(bo, SysDept.class);
    dept.setAncestors(info.getAncestors() + StringUtils.SEPARATOR + dept.getParentId());
    return baseMapper.insert(dept);
  }

  /**
   * 修改保存部门信息
   *
   * @param bo 部门信息
   * @return 结果
   */
  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#bo.deptId"),
        @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, allEntries = true)
      })
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateDept(SysDeptBo bo) {
    SysDept dept = MapstructUtils.convert(bo, SysDept.class);
    SysDept oldDept = baseMapper.selectById(dept.getDeptId());
    if (!oldDept.getParentId().equals(dept.getParentId())) {
      // 如果是新父部门 则校验是否具有新父部门权限 避免越权
      this.checkDeptDataScope(dept.getParentId());
      SysDept newParentDept = baseMapper.selectById(dept.getParentId());
      if (ObjectUtil.isNotNull(newParentDept) && ObjectUtil.isNotNull(oldDept)) {
        String newAncestors =
            newParentDept.getAncestors() + StringUtils.SEPARATOR + newParentDept.getDeptId();
        String oldAncestors = oldDept.getAncestors();
        dept.setAncestors(newAncestors);
        updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
      }
    } else {
      dept.setAncestors(oldDept.getAncestors());
    }
    int result = baseMapper.updateById(dept);
    // 如果部门状态为启用，且部门祖级列表不为空，且部门祖级列表不等于根部门祖级列表（如果部门祖级列表不等于根部门祖级列表，则说明存在上级部门）
    if (SystemConstants.NORMAL.equals(dept.getStatus())
        && StringUtils.isNotEmpty(dept.getAncestors())
        && !StringUtils.equals(SystemConstants.ROOT_DEPT_ANCESTORS, dept.getAncestors())) {
      // 如果该部门是启用状态，则启用该部门的所有上级部门
      updateParentDeptStatusNormal(dept);
    }
    return result;
  }

  /**
   * 修改该部门的父级部门状态
   *
   * @param dept 当前部门
   */
  private void updateParentDeptStatusNormal(SysDept dept) {
    String ancestors = dept.getAncestors();
    Long[] deptIds = Convert.toLongArray(ancestors);
    baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysDept>()
            .set(SysDept::getStatus, SystemConstants.NORMAL)
            .in(SysDept::getDeptId, Arrays.asList(deptIds)));
  }

  /**
   * 修改子元素关系
   *
   * @param deptId 被修改的部门ID
   * @param newAncestors 新的父ID集合
   * @param oldAncestors 旧的父ID集合
   */
  private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
    List<SysDept> children =
        baseMapper.selectList(
            new LambdaQueryWrapper<SysDept>().apply(DataBaseHelper.findInSet(deptId, "ancestors")));
    List<Long> deptIds = new ArrayList<>();
    StringBuilder sql = new StringBuilder("case dept_id");
    for (SysDept child : children) {
      sql.append(" when ")
          .append(child.getDeptId())
          .append(" then '")
          .append(child.getAncestors().replaceFirst(oldAncestors, newAncestors))
          .append("'");
      deptIds.add(child.getDeptId());
    }
    sql.append("end");
    UpdateWrapper<SysDept> updateWrapper = new UpdateWrapper<>();

    updateWrapper.setSql("ancestors= " + sql.toString());
    if (CollUtil.isNotEmpty(deptIds)) {
      if (baseMapper.update(updateWrapper) > 0) {
        deptIds.forEach(id -> CacheUtils.evict(CacheNames.SYS_DEPT, id));
      }
    }
  }

  /**
   * 删除部门管理信息
   *
   * @param deptId 部门ID
   * @return 结果
   */
  @Caching(
      evict = {
        @CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#deptId"),
        @CacheEvict(cacheNames = CacheNames.SYS_DEPT_AND_CHILD, key = "#deptId")
      })
  @Override
  public int deleteDeptById(Long deptId) {
    return baseMapper.deleteById(deptId);
  }

  /**
   * 查询部门(简单查询)
   *
   * @return 部门列表
   */
  @Override
  public List<SysDeptVo> selectDeptsSimple() {
    return baseMapper.selectDeptList(
        new LambdaQueryWrapper<SysDept>()
            .select(SysDept::getDeptId, SysDept::getDeptName, SysDept::getParentId)
            .eq(SysDept::getStatus, SystemConstants.NORMAL));
  }
}
