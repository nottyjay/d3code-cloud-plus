package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysPostQueryParam;
import com.alphay.boot.system.domain.SysDept;
import com.alphay.boot.system.domain.SysPost;
import com.alphay.boot.system.domain.SysUserPost;
import com.alphay.boot.system.domain.bo.SysPostBo;
import com.alphay.boot.system.domain.vo.SysPostVo;
import com.alphay.boot.system.mapper.SysDeptMapper;
import com.alphay.boot.system.mapper.SysPostMapper;
import com.alphay.boot.system.mapper.SysUserPostMapper;
import com.alphay.boot.system.service.ISysPostService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 岗位信息 服务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysPostServiceImpl extends ServiceImplX<SysPostMapper, SysPost, SysPostVo>
    implements ISysPostService {

  @Resource private SysDeptMapper deptMapper;
  @Resource private SysUserPostMapper userPostMapper;

  @Override
  public PageResult<SysPostVo> queryPageList(SysPostQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 查询岗位信息集合
   *
   * @param param 岗位信息
   * @return 岗位信息集合
   */
  @Override
  public List<SysPostVo> queryList(SysPostQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  /**
   * 查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 岗位ID
   */
  @Override
  public List<SysPostVo> queryListByUserId(Long userId) {
    return baseMapper.selectPostsByUserId(userId);
  }

  /**
   * 根据查询条件构建查询包装器
   *
   * @param param 查询条件对象
   * @return 构建好的查询包装器
   */
  private LambdaQueryWrapper<SysPost> buildQueryWrapper(SysPostQueryParam param) {
    LambdaQueryWrapper<SysPost> wrapper =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysPost::getPostCode, param.getPostCode())
            .likeIfPresent(SysPost::getPostCategory, param.getPostCategory())
            .likeIfPresent(SysPost::getPostName, param.getPostName())
            .eqIfPresent(SysPost::getStatus, param.getStatus())
            .betweenIfPresent(SysPost::getCreateTime, param.getCreateTime())
            .orderByAsc(SysPost::getPostSort);
    if (ObjectUtil.isNotNull(param.getDeptId())) {
      // 优先单部门搜索
      wrapper.eq(SysPost::getDeptId, param.getDeptId());
    } else if (ObjectUtil.isNotNull(param.getBelongDeptId())) {
      // 部门树搜索
      wrapper.and(
          x -> {
            List<SysDept> deptList = deptMapper.selectListByParentId(param.getBelongDeptId());
            List<Long> deptIds = StreamUtils.toList(deptList, SysDept::getDeptId);
            deptIds.add(param.getBelongDeptId());
            x.in(SysPost::getDeptId, deptIds);
          });
    }
    return wrapper;
  }

  /**
   * 根据用户ID获取岗位选择框列表
   *
   * @param userId 用户ID
   * @return 选中岗位ID列表
   */
  @Override
  public List<Long> selectPostListByUserId(Long userId) {
    List<SysPostVo> list = baseMapper.selectPostsByUserId(userId);
    return StreamUtils.toList(list, SysPostVo::getPostId);
  }

  /**
   * 通过岗位ID串查询岗位
   *
   * @param postIds 岗位id串
   * @return 岗位列表信息
   */
  @Override
  public List<SysPostVo> selectPostByIds(List<Long> postIds) {
    return listVo(
        new LambdaQueryWrapper<SysPost>()
            .select(SysPost::getPostId, SysPost::getPostName, SysPost::getPostCode)
            .eq(SysPost::getStatus, SystemConstants.NORMAL)
            .in(CollUtil.isNotEmpty(postIds), SysPost::getPostId, postIds));
  }

  /**
   * 校验岗位名称是否唯一
   *
   * @param post 岗位信息
   * @return 结果
   */
  @Override
  public boolean checkPostNameUnique(SysPostBo post) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysPost>()
                .eq(SysPost::getPostName, post.getPostName())
                .eq(SysPost::getDeptId, post.getDeptId())
                .ne(ObjectUtil.isNotNull(post.getPostId()), SysPost::getPostId, post.getPostId()));
    return !exist;
  }

  /**
   * 校验岗位编码是否唯一
   *
   * @param post 岗位信息
   * @return 结果
   */
  @Override
  public boolean checkPostCodeUnique(SysPostBo post) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysPost>()
                .eq(SysPost::getPostCode, post.getPostCode())
                .ne(ObjectUtil.isNotNull(post.getPostId()), SysPost::getPostId, post.getPostId()));
    return !exist;
  }

  /**
   * 通过岗位ID查询岗位使用数量
   *
   * @param postId 岗位ID
   * @return 结果
   */
  @Override
  public long countUserPostById(Long postId) {
    return userPostMapper.selectCount(
        new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getPostId, postId));
  }

  /**
   * 通过部门ID查询岗位使用数量
   *
   * @param deptId 部门id
   * @return 结果
   */
  @Override
  public long countPostByDeptId(Long deptId) {
    return baseMapper.selectCount(new LambdaQueryWrapper<SysPost>().eq(SysPost::getDeptId, deptId));
  }

  /**
   * 删除岗位信息
   *
   * @param postId 岗位ID
   * @return 结果
   */
  @Override
  public int deletePostById(Long postId) {
    return baseMapper.deleteById(postId);
  }

  /**
   * 批量删除岗位信息
   *
   * @param postIds 需要删除的岗位ID
   * @return 结果
   */
  @Override
  public int deletePostByIds(Long[] postIds) {
    for (Long postId : postIds) {
      SysPost post = baseMapper.selectById(postId);
      if (countUserPostById(postId) > 0) {
        throw new ServiceException(String.format("%1$s已分配，不能删除!", post.getPostName()));
      }
    }
    return baseMapper.deleteByIds(Arrays.asList(postIds));
  }

  /**
   * 新增保存岗位信息
   *
   * @param bo 岗位信息
   * @return 结果
   */
  @Override
  public int insertPost(SysPostBo bo) {
    SysPost post = MapstructUtils.convert(bo, SysPost.class);
    return baseMapper.insert(post);
  }

  /**
   * 修改保存岗位信息
   *
   * @param bo 岗位信息
   * @return 结果
   */
  @Override
  public int updatePost(SysPostBo bo) {
    SysPost post = MapstructUtils.convert(bo, SysPost.class);
    return baseMapper.updateById(post);
  }
}
