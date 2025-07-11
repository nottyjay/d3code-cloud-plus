package com.alphay.boot.system.dubbo;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.system.api.RemoteSocialService;
import com.alphay.boot.system.api.domain.bo.RemoteSocialBo;
import com.alphay.boot.system.api.domain.param.SysSocialQueryParam;
import com.alphay.boot.system.api.domain.vo.RemoteSocialVo;
import com.alphay.boot.system.domain.bo.SysSocialBo;
import com.alphay.boot.system.domain.vo.SysSocialVo;
import com.alphay.boot.system.service.ISysSocialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 社会化关系服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSocialServiceImpl implements RemoteSocialService {

  private final ISysSocialService sysSocialService;

  /**
   * 根据 authId 查询用户授权信息
   *
   * @param authId 认证id
   * @return 授权信息
   */
  @Override
  public List<RemoteSocialVo> selectByAuthId(String authId) {
    List<SysSocialVo> list = sysSocialService.selectByAuthId(authId);
    return MapstructUtils.convert(list, RemoteSocialVo.class);
  }

  /**
   * 查询列表
   *
   * @param bo 社会化关系业务对象
   */
  @Override
  public List<RemoteSocialVo> queryList(RemoteSocialBo bo) {
    SysSocialQueryParam params = MapstructUtils.convert(bo, SysSocialQueryParam.class);
    List<SysSocialVo> list = sysSocialService.queryList(params);
    return MapstructUtils.convert(list, RemoteSocialVo.class);
  }

  /**
   * 保存社会化关系
   *
   * @param bo 社会化关系业务对象
   */
  @Override
  public void insertByBo(RemoteSocialBo bo) {
    sysSocialService.insertByBo(MapstructUtils.convert(bo, SysSocialBo.class));
  }

  /**
   * 更新社会化关系
   *
   * @param bo 社会化关系业务对象
   */
  @Override
  public void updateByBo(RemoteSocialBo bo) {
    sysSocialService.updateByBo(MapstructUtils.convert(bo, SysSocialBo.class));
  }

  /**
   * 删除社会化关系
   *
   * @param socialId 社会化关系ID
   * @return 结果
   */
  @Override
  public Boolean deleteWithValidById(Long socialId) {
    return sysSocialService.deleteWithValidById(socialId);
  }
}
