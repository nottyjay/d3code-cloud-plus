package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.annotation.DataColumn;
import com.alphay.boot.common.mybatis.annotation.DataPermission;
import com.alphay.boot.common.mybatis.core.mapper.BaseMapperPlus;
import com.alphay.boot.system.domain.SysPost;
import com.alphay.boot.system.domain.vo.SysPostVo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 岗位信息 数据层
 *
 * @author Lion Li
 */
public interface SysPostMapper extends BaseMapperPlus<SysPost, SysPostVo> {

  @DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "create_by")
  })
  default Page<SysPostVo> selectPagePostList(Page<SysPost> page, Wrapper<SysPost> queryWrapper) {
    return this.selectVoPage(page, queryWrapper);
  }

  /**
   * 查询用户所属岗位组
   *
   * @param userId 用户ID
   * @return 结果
   */
  List<SysPostVo> selectPostsByUserId(Long userId);
}
