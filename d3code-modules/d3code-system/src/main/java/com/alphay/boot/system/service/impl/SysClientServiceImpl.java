package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.SecureUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysClientQueryParam;
import com.alphay.boot.system.domain.SysClient;
import com.alphay.boot.system.domain.bo.SysClientBo;
import com.alphay.boot.system.domain.vo.SysClientVo;
import com.alphay.boot.system.mapper.SysClientMapper;
import com.alphay.boot.system.service.ISysClientService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 客户端管理Service业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
public class SysClientServiceImpl extends ServiceImplX<SysClientMapper, SysClient, SysClientVo>
    implements ISysClientService {

  /** 查询客户端管理 */
  @Override
  public SysClientVo getVoById(Serializable id) {
    SysClientVo vo = super.getVoById(id);
    vo.setGrantTypeList(StringUtils.splitList(vo.getGrantType()));
    return vo;
  }

  /** 查询客户端管理 */
  @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
  @Override
  public SysClientVo queryByClientId(String clientId) {
    return this.getOneVo(SysClient::getClientId, clientId);
  }

  /** 查询客户端管理列表 */
  @Override
  public PageResult<SysClientVo> queryPageList(SysClientQueryParam param) {
    LambdaQueryWrapper<SysClient> lqw = buildQueryWrapper(param);
    PageResult<SysClientVo> result = this.listPageVo(param, lqw);
    result.getRows().forEach(r -> r.setGrantTypeList(StringUtils.splitList(r.getGrantType())));
    return result;
  }

  /** 查询客户端管理列表 */
  @Override
  public List<SysClientVo> queryList(SysClientQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysClient> buildQueryWrapper(SysClientQueryParam param) {
    LambdaQueryWrapper<SysClient> lqw =
        this.lambdaQueryWrapper()
            .eqIfPresent(SysClient::getClientId, param.getClientId())
            .eqIfPresent(SysClient::getClientKey, param.getClientKey())
            .eqIfPresent(SysClient::getClientSecret, param.getClientSecret())
            .eqIfPresent(SysClient::getStatus, param.getStatus());
    lqw.orderByAsc(SysClient::getId);
    return lqw;
  }

  /** 新增客户端管理 */
  @Override
  public Boolean insertByBo(SysClientBo bo) {
    SysClient add = MapstructUtils.convert(bo, SysClient.class);
    add.setGrantType(CollUtil.join(bo.getGrantTypeList(), StringUtils.SEPARATOR));
    // 生成clientId
    String clientKey = bo.getClientKey();
    String clientSecret = bo.getClientSecret();
    add.setClientId(SecureUtil.md5(clientKey + clientSecret));
    boolean flag = baseMapper.insert(add) > 0;
    if (flag) {
      bo.setId(add.getId());
    }
    return flag;
  }

  /** 修改客户端管理 */
  @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
  @Override
  public Boolean updateByBo(SysClientBo bo) {
    SysClient update = MapstructUtils.convert(bo, SysClient.class);
    update.setGrantType(String.join(",", bo.getGrantTypeList()));
    return baseMapper.updateById(update) > 0;
  }

  /** 修改状态 */
  @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
  @Override
  public int updateClientStatus(String clientId, String status) {
    return baseMapper.update(
        null,
        new LambdaUpdateWrapper<SysClient>()
            .set(SysClient::getStatus, status)
            .eq(SysClient::getClientId, clientId));
  }

  /** 批量删除客户端管理 */
  @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, allEntries = true)
  @Override
  public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
    return baseMapper.deleteByIds(ids) > 0;
  }
}
