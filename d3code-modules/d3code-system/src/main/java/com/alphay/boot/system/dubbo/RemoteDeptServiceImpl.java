package com.alphay.boot.system.dubbo;

import cn.hutool.core.bean.BeanUtil;
import com.alphay.boot.system.api.RemoteDeptService;
import com.alphay.boot.system.api.domain.vo.RemoteDeptVo;
import com.alphay.boot.system.domain.vo.SysDeptVo;
import com.alphay.boot.system.service.ISysDeptService;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 部门服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemoteDeptServiceImpl implements RemoteDeptService {

  @Resource private ISysDeptService sysDeptService;

  /**
   * 通过部门ID查询部门名称
   *
   * @param deptIds 部门ID串逗号分隔
   * @return 部门名称串逗号分隔
   */
  @Override
  public String selectDeptNameByIds(String deptIds) {
    return sysDeptService.selectDeptNameByIds(deptIds);
  }

  /**
   * 根据部门ID查询部门负责人
   *
   * @param deptId 部门ID，用于指定需要查询的部门
   * @return 返回该部门的负责人ID
   */
  @Override
  public Long selectDeptLeaderById(Long deptId) {
    SysDeptVo vo = sysDeptService.getVoById(deptId);
    return vo.getLeader();
  }

  /**
   * 查询部门
   *
   * @return 部门列表
   */
  @Override
  public List<RemoteDeptVo> selectDeptsByList() {
    List<SysDeptVo> list = sysDeptService.selectDeptsSimple();
    return BeanUtil.copyToList(list, RemoteDeptVo.class);
  }
}
