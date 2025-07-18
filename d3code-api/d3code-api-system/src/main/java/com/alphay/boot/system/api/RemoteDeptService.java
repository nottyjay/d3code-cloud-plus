package com.alphay.boot.system.api;

import com.alphay.boot.system.api.domain.vo.RemoteDeptVo;
import java.util.List;

/**
 * 部门服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteDeptService {

  /**
   * 通过部门ID查询部门名称
   *
   * @param deptIds 部门ID串逗号分隔
   * @return 部门名称串逗号分隔
   */
  String selectDeptNameByIds(String deptIds);

  /**
   * 根据部门ID查询部门负责人
   *
   * @param deptId 部门ID，用于指定需要查询的部门
   * @return 返回该部门的负责人ID
   */
  Long selectDeptLeaderById(Long deptId);

  /**
   * 查询部门
   *
   * @return 部门列表
   */
  List<RemoteDeptVo> selectDeptsByList();
}
