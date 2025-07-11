package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysPostQueryParam;
import com.alphay.boot.system.domain.SysPost;
import com.alphay.boot.system.domain.bo.SysPostBo;
import com.alphay.boot.system.domain.vo.SysPostVo;
import java.util.List;

/**
 * 岗位信息 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysPostService extends IServiceX<SysPost, SysPostVo> {

  PageResult<SysPostVo> queryPageList(SysPostQueryParam param);

  /**
   * 查询岗位信息集合
   *
   * @param param 岗位信息
   * @return 岗位列表
   */
  List<SysPostVo> queryList(SysPostQueryParam param);

  /**
   * 查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 岗位ID
   */
  List<SysPostVo> queryListByUserId(Long userId);

  /**
   * 根据用户ID获取岗位选择框列表
   *
   * @param userId 用户ID
   * @return 选中岗位ID列表
   */
  List<Long> selectPostListByUserId(Long userId);

  /**
   * 通过岗位ID串查询岗位
   *
   * @param postIds 岗位id串
   * @return 岗位列表信息
   */
  List<SysPostVo> selectPostByIds(List<Long> postIds);

  /**
   * 校验岗位名称
   *
   * @param post 岗位信息
   * @return 结果
   */
  boolean checkPostNameUnique(SysPostBo post);

  /**
   * 校验岗位编码
   *
   * @param post 岗位信息
   * @return 结果
   */
  boolean checkPostCodeUnique(SysPostBo post);

  /**
   * 通过岗位ID查询岗位使用数量
   *
   * @param postId 岗位ID
   * @return 结果
   */
  long countUserPostById(Long postId);

  /**
   * 通过部门ID查询岗位使用数量
   *
   * @param deptId 部门id
   * @return 结果
   */
  long countPostByDeptId(Long deptId);

  /**
   * 删除岗位信息
   *
   * @param postId 岗位ID
   * @return 结果
   */
  int deletePostById(Long postId);

  /**
   * 批量删除岗位信息
   *
   * @param postIds 需要删除的岗位ID
   * @return 结果
   */
  int deletePostByIds(Long[] postIds);

  /**
   * 新增保存岗位信息
   *
   * @param bo 岗位信息
   * @return 结果
   */
  int insertPost(SysPostBo bo);

  /**
   * 修改保存岗位信息
   *
   * @param bo 岗位信息
   * @return 结果
   */
  int updatePost(SysPostBo bo);
}
