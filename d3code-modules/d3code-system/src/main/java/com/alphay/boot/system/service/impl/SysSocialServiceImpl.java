package com.alphay.boot.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysSocialQueryParam;
import com.alphay.boot.system.domain.SysSocial;
import com.alphay.boot.system.domain.bo.SysSocialBo;
import com.alphay.boot.system.domain.vo.SysSocialVo;
import com.alphay.boot.system.mapper.SysSocialMapper;
import com.alphay.boot.system.service.ISysSocialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 社会化关系Service业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 * @date 2023-06-12
 */
@Service
public class SysSocialServiceImpl extends ServiceImplX<SysSocialMapper, SysSocial, SysSocialVo>
    implements ISysSocialService {

  /**
   * 查询社会化关系列表
   *
   * @param param 用于过滤查询条件的SysSocialBo对象
   * @return 返回符合条件的SysSocialVo对象列表
   */
  @Override
  public List<SysSocialVo> queryList(SysSocialQueryParam param) {
    LambdaQueryWrapper<SysSocial> lqw =
        new LambdaQueryWrapper<SysSocial>()
            .eq(ObjectUtil.isNotNull(param.getUserId()), SysSocial::getUserId, param.getUserId())
            .eq(StringUtils.isNotBlank(param.getAuthId()), SysSocial::getAuthId, param.getAuthId())
            .eq(StringUtils.isNotBlank(param.getSource()), SysSocial::getSource, param.getSource());
    return listVo(lqw);
  }

  /**
   * 根据用户ID查询社会化关系列表
   *
   * @param userId 用户的唯一标识符
   * @return 返回与给定用户ID相关联的SysSocialVo对象列表
   */
  @Override
  public List<SysSocialVo> queryListByUserId(Long userId) {
    return listVo(new LambdaQueryWrapper<SysSocial>().eq(SysSocial::getUserId, userId));
  }

  /**
   * 新增授权关系
   *
   * @param bo 包含新增授权关系信息的SysSocialBo对象
   * @return 返回新增操作的结果，成功返回true，失败返回false
   */
  @Override
  public Boolean insertByBo(SysSocialBo bo) {
    SysSocial add = MapstructUtils.convert(bo, SysSocial.class);
    validEntityBeforeSave(add);
    boolean flag = baseMapper.insert(add) > 0;
    if (flag) {
      if (add != null) {
        bo.setId(add.getId());
      } else {
        return false;
      }
    }
    return flag;
  }

  /**
   * 更新社会化关系
   *
   * @param bo 包含更新信息的SysSocialBo对象
   * @return 返回更新操作的结果，成功返回true，失败返回false
   */
  @Override
  public Boolean updateByBo(SysSocialBo bo) {
    SysSocial update = MapstructUtils.convert(bo, SysSocial.class);
    validEntityBeforeSave(update);
    return baseMapper.updateById(update) > 0;
  }

  /** 保存前的数据校验 */
  private void validEntityBeforeSave(SysSocial entity) {
    // TODO 做一些数据校验,如唯一约束
  }

  /**
   * 删除社会化关系信息
   *
   * @param id 要删除的社会化关系的唯一标识符
   * @return 返回删除操作的结果，成功返回true，失败返回false
   */
  @Override
  public Boolean deleteWithValidById(Long id) {
    return baseMapper.deleteById(id) > 0;
  }

  /**
   * 根据认证ID查询社会化关系和用户信息
   *
   * @param authId 认证ID
   * @return 返回包含SysSocial和用户信息的SysSocialVo对象列表
   */
  @Override
  public List<SysSocialVo> selectByAuthId(String authId) {
    return listVo(new LambdaQueryWrapper<SysSocial>().eq(SysSocial::getAuthId, authId));
  }
}
