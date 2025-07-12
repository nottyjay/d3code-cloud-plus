package com.alphay.boot.system.dubbo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.system.api.RemoteDataScopeService;
import com.alphay.boot.system.domain.SysDept;
import com.alphay.boot.system.domain.SysRoleDept;
import com.alphay.boot.system.mapper.SysDeptMapper;
import com.alphay.boot.system.mapper.SysRoleDeptMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 数据权限 实现
 *
 * <p>注意: 此Service内不允许调用标注`数据权限`注解的方法 例如: deptMapper.selectList 此 selectList 方法标注了`数据权限`注解
 * 会出现循环解析的问题
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemoteDataScopeServiceImpl implements RemoteDataScopeService {

  @Resource private SysRoleDeptMapper roleDeptMapper;
  @Resource private SysDeptMapper deptMapper;

  /**
   * 获取角色自定义权限语句
   *
   * @param roleId 角色ID
   * @return 返回角色的自定义权限语句，如果没有找到则返回 null
   */
  @Cacheable(
      cacheNames = CacheNames.SYS_ROLE_CUSTOM,
      key = "#roleId",
      condition = "#roleId != null")
  @Override
  public String getRoleCustom(Long roleId) {
    if (ObjectUtil.isNull(roleId)) {
      return "-1";
    }
    List<SysRoleDept> list =
        roleDeptMapper.selectList(
            new LambdaQueryWrapper<SysRoleDept>()
                .select(SysRoleDept::getDeptId)
                .eq(SysRoleDept::getRoleId, roleId));
    if (CollUtil.isNotEmpty(list)) {
      return StreamUtils.join(list, rd -> Convert.toStr(rd.getDeptId()));
    }
    return "-1";
  }

  /**
   * 获取部门和下级权限语句
   *
   * @param deptId 部门ID
   * @return 返回部门及其下级的权限语句，如果没有找到则返回 null
   */
  @Cacheable(
      cacheNames = CacheNames.SYS_DEPT_AND_CHILD,
      key = "#deptId",
      condition = "#deptId != null")
  @Override
  public String getDeptAndChild(Long deptId) {
    if (ObjectUtil.isNull(deptId)) {
      return "-1";
    }
    List<SysDept> deptList = deptMapper.selectListByParentId(deptId);
    List<Long> ids = StreamUtils.toList(deptList, SysDept::getDeptId);
    ids.add(deptId);
    if (CollUtil.isNotEmpty(ids)) {
      return StreamUtils.join(ids, Convert::toStr);
    }
    return "-1";
  }
}
