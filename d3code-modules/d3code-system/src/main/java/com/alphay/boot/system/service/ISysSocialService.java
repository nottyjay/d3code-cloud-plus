package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysSocialQueryParam;
import com.alphay.boot.system.domain.SysSocial;
import com.alphay.boot.system.domain.bo.SysSocialBo;
import com.alphay.boot.system.domain.vo.SysSocialVo;
import java.util.List;

/**
 * 社会化关系Service接口
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysSocialService extends IServiceX<SysSocial, SysSocialVo> {

  /**
   * 查询社会化关系列表
   *
   * @param param 用于过滤查询条件的SysSocialBo对象
   * @return 返回符合条件的SysSocialVo对象列表
   */
  List<SysSocialVo> queryList(SysSocialQueryParam param);

  /**
   * 根据用户ID查询社会化关系列表
   *
   * @param userId 用户的唯一标识符
   * @return 返回与给定用户ID相关联的SysSocialVo对象列表
   */
  List<SysSocialVo> queryListByUserId(Long userId);

  /**
   * 新增授权关系
   *
   * @param bo 包含新增授权关系信息的SysSocialBo对象
   * @return 返回新增操作的结果，成功返回true，失败返回false
   */
  Boolean insertByBo(SysSocialBo bo);

  /**
   * 更新社会化关系
   *
   * @param bo 包含更新信息的SysSocialBo对象
   * @return 返回更新操作的结果，成功返回true，失败返回false
   */
  Boolean updateByBo(SysSocialBo bo);

  /**
   * 删除社会化关系信息
   *
   * @param id 要删除的社会化关系的唯一标识符
   * @return 返回删除操作的结果，成功返回true，失败返回false
   */
  Boolean deleteWithValidById(Long id);

  /**
   * 根据认证ID查询社会化关系和用户信息
   *
   * @param authId 认证ID
   * @return 返回包含SysSocial和用户信息的SysSocialVo对象列表
   */
  List<SysSocialVo> selectByAuthId(String authId);
}
