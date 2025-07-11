package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysPost;
import com.alphay.boot.system.domain.vo.SysPostVo;
import java.util.List;

/**
 * 岗位信息 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysPostMapper extends BaseMapperX<SysPost> {

  /**
   * 查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 结果
   */
  List<SysPostVo> selectPostsByUserId(Long userId);
}
