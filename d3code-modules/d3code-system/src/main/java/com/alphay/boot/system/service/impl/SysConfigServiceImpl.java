package com.alphay.boot.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.ObjectUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.system.api.domain.param.SysConfigQueryParam;
import com.alphay.boot.system.domain.SysConfig;
import com.alphay.boot.system.domain.bo.SysConfigBo;
import com.alphay.boot.system.domain.vo.SysConfigVo;
import com.alphay.boot.system.mapper.SysConfigMapper;
import com.alphay.boot.system.service.ISysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 参数配置 服务层实现
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysConfigServiceImpl extends ServiceImplX<SysConfigMapper, SysConfig, SysConfigVo>
    implements ISysConfigService {

  @Override
  public PageResult<SysConfigVo> queryPageList(SysConfigQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /**
   * 根据键名查询参数配置信息
   *
   * @param configKey 参数key
   * @return 参数键值
   */
  @Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
  @Override
  public String selectConfigByKey(String configKey) {
    SysConfig retConfig = this.getOne(SysConfig::getConfigKey, configKey);
    return ObjectUtils.notNullGetter(retConfig, SysConfig::getConfigValue, StringUtils.EMPTY);
  }

  /**
   * 获取注册开关
   *
   * @param tenantId 租户id
   * @return true开启，false关闭
   */
  @Override
  public boolean selectRegisterEnabled(String tenantId) {
    String configValue =
        TenantHelper.dynamic(tenantId, () -> this.selectConfigByKey("sys.account.registerUser"));
    return Convert.toBool(configValue);
  }

  /**
   * 查询参数配置列表
   *
   * @param param 参数配置信息
   * @return 参数配置集合
   */
  @Override
  public List<SysConfigVo> queryList(SysConfigQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysConfig> buildQueryWrapper(SysConfigQueryParam param) {
    LambdaQueryWrapper<SysConfig> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysConfig::getConfigName, param.getConfigName())
            .eqIfPresent(SysConfig::getConfigType, param.getConfigType())
            .likeIfPresent(SysConfig::getConfigKey, param.getConfigKey())
            .betweenIfPresent(SysConfig::getCreateTime, param.getCreateTime());
    lqw.orderByAsc(SysConfig::getConfigId);
    return lqw;
  }

  /**
   * 新增参数配置
   *
   * @param bo 参数配置信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#bo.configKey")
  @Override
  public String insertConfig(SysConfigBo bo) {
    SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
    int row = baseMapper.insert(config);
    if (row > 0) {
      return config.getConfigValue();
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 修改参数配置
   *
   * @param bo 参数配置信息
   * @return 结果
   */
  @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#bo.configKey")
  @Override
  public String updateConfig(SysConfigBo bo) {
    int row = 0;
    SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
    if (config.getConfigId() != null) {
      SysConfig temp = baseMapper.selectById(config.getConfigId());
      if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
        CacheUtils.evict(CacheNames.SYS_CONFIG, temp.getConfigKey());
      }
      row = baseMapper.updateById(config);
    } else {
      CacheUtils.evict(CacheNames.SYS_CONFIG, config.getConfigKey());
      row =
          baseMapper.update(
              config,
              new LambdaQueryWrapper<SysConfig>()
                  .eq(SysConfig::getConfigKey, config.getConfigKey()));
    }
    if (row > 0) {
      return config.getConfigValue();
    }
    throw new ServiceException("操作失败");
  }

  /**
   * 批量删除参数信息
   *
   * @param configIds 需要删除的参数ID
   */
  @Override
  public void deleteConfigByIds(List<Long> configIds) {
    List<SysConfig> list = baseMapper.selectByIds(configIds);
    list.forEach(
        config -> {
          if (StringUtils.equals(SystemConstants.YES, config.getConfigType())) {
            throw new ServiceException(String.format("内置参数【%s】不能删除", config.getConfigKey()));
          }
          CacheUtils.evict(CacheNames.SYS_CONFIG, config.getConfigKey());
        });
    baseMapper.deleteByIds(configIds);
  }

  /** 重置参数缓存数据 */
  @Override
  public void resetConfigCache() {
    CacheUtils.clear(CacheNames.SYS_CONFIG);
  }

  /**
   * 校验参数键名是否唯一
   *
   * @param config 参数配置信息
   * @return 结果
   */
  @Override
  public boolean checkConfigKeyUnique(SysConfigBo config) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey())
                .ne(
                    ObjectUtil.isNotNull(config.getConfigId()),
                    SysConfig::getConfigId,
                    config.getConfigId()));
    return !exist;
  }
}
