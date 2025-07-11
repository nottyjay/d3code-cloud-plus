package com.alphay.boot.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.ObjectUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.json.utils.JsonUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.oss.constant.OssConstant;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.common.redis.utils.RedisUtils;
import com.alphay.boot.resource.api.domain.param.SysOssConfigQueryParam;
import com.alphay.boot.resource.domain.SysOssConfig;
import com.alphay.boot.resource.domain.bo.SysOssConfigBo;
import com.alphay.boot.resource.domain.vo.SysOssConfigVo;
import com.alphay.boot.resource.mapper.SysOssConfigMapper;
import com.alphay.boot.resource.service.ISysOssConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对象存储配置Service业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 * @author Nottyjay
 * @since 1.0.0
 * @date 2021-08-13
 */
@Slf4j
@Service
public class SysOssConfigServiceImpl
    extends ServiceImplX<SysOssConfigMapper, SysOssConfig, SysOssConfigVo>
    implements ISysOssConfigService {

  /** 项目启动时，初始化参数到缓存，加载配置类 */
  @Override
  public void init() {
    List<SysOssConfig> list = list();
    // 加载OSS初始化配置
    for (SysOssConfig config : list) {
      String configKey = config.getConfigKey();
      if ("0".equals(config.getStatus())) {
        RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey);
      }
      CacheUtils.put(
          CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
    }
  }

  @Override
  public PageResult<SysOssConfigVo> queryPageList(SysOssConfigQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysOssConfig> buildQueryWrapper(SysOssConfigQueryParam param) {
    LambdaQueryWrapper<SysOssConfig> lqw =
        this.lambdaQueryWrapper()
            .eqIfPresent(SysOssConfig::getConfigKey, param.getConfigKey())
            .likeIfPresent(SysOssConfig::getBucketName, param.getBucketName())
            .eqIfPresent(SysOssConfig::getStatus, param.getStatus());
    lqw.orderByAsc(SysOssConfig::getOssConfigId);
    return lqw;
  }

  @Override
  public Boolean insertByBo(SysOssConfigBo bo) {
    SysOssConfig config = BeanUtil.toBean(bo, SysOssConfig.class);
    validEntityBeforeSave(config);
    boolean flag = baseMapper.insert(config) > 0;
    if (flag) {
      // 从数据库查询完整的数据做缓存
      config = baseMapper.selectById(config.getOssConfigId());
      CacheUtils.put(
          CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
    }
    return flag;
  }

  @Override
  public Boolean updateByBo(SysOssConfigBo bo) {
    SysOssConfig config = BeanUtil.toBean(bo, SysOssConfig.class);
    validEntityBeforeSave(config);
    LambdaUpdateWrapper<SysOssConfig> luw = new LambdaUpdateWrapper<>();
    luw.set(ObjectUtil.isNull(config.getPrefix()), SysOssConfig::getPrefix, "");
    luw.set(ObjectUtil.isNull(config.getRegion()), SysOssConfig::getRegion, "");
    luw.set(ObjectUtil.isNull(config.getExt1()), SysOssConfig::getExt1, "");
    luw.set(ObjectUtil.isNull(config.getRemark()), SysOssConfig::getRemark, "");
    luw.eq(SysOssConfig::getOssConfigId, config.getOssConfigId());
    boolean flag = baseMapper.update(config, luw) > 0;
    if (flag) {
      // 从数据库查询完整的数据做缓存
      config = baseMapper.selectById(config.getOssConfigId());
      CacheUtils.put(
          CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
    }
    return flag;
  }

  /** 保存前的数据校验 */
  private void validEntityBeforeSave(SysOssConfig entity) {
    if (StringUtils.isNotEmpty(entity.getConfigKey()) && !checkConfigKeyUnique(entity)) {
      throw new ServiceException("操作配置'" + entity.getConfigKey() + "'失败, 配置key已存在!");
    }
  }

  @Override
  public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
    if (isValid) {
      if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
        throw new ServiceException("系统内置, 不可删除!");
      }
    }
    List<SysOssConfig> list = CollUtil.newArrayList();
    for (Long configId : ids) {
      SysOssConfig config = baseMapper.selectById(configId);
      list.add(config);
    }
    boolean flag = baseMapper.deleteByIds(ids) > 0;
    if (flag) {
      list.forEach(
          sysOssConfig -> CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey()));
    }
    return flag;
  }

  /** 判断configKey是否唯一 */
  private boolean checkConfigKeyUnique(SysOssConfig sysOssConfig) {
    long ossConfigId = ObjectUtils.notNull(sysOssConfig.getOssConfigId(), -1L);
    SysOssConfig info =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysOssConfig>()
                .select(SysOssConfig::getOssConfigId, SysOssConfig::getConfigKey)
                .eq(SysOssConfig::getConfigKey, sysOssConfig.getConfigKey()));
    if (ObjectUtil.isNotNull(info) && info.getOssConfigId() != ossConfigId) {
      return false;
    }
    return true;
  }

  /** 启用禁用状态 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateOssConfigStatus(SysOssConfigBo bo) {
    SysOssConfig sysOssConfig = BeanUtil.toBean(bo, SysOssConfig.class);
    int row =
        baseMapper.update(
            null, new LambdaUpdateWrapper<SysOssConfig>().set(SysOssConfig::getStatus, "1"));
    row += baseMapper.updateById(sysOssConfig);
    if (row > 0) {
      RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, sysOssConfig.getConfigKey());
    }
    return row;
  }
}
