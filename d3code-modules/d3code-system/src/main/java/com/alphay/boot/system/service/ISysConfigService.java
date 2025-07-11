package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysConfigQueryParam;
import com.alphay.boot.system.domain.SysConfig;
import com.alphay.boot.system.domain.bo.SysConfigBo;
import com.alphay.boot.system.domain.vo.SysConfigVo;
import java.util.List;

/**
 * 参数配置 服务层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysConfigService extends IServiceX<SysConfig, SysConfigVo> {

  PageResult<SysConfigVo> queryPageList(SysConfigQueryParam param);

  /**
   * 根据键名查询参数配置信息
   *
   * @param configKey 参数键名
   * @return 参数键值
   */
  String selectConfigByKey(String configKey);

  /**
   * 获取注册开关
   *
   * @param tenantId 租户id
   * @return true开启，false关闭
   */
  boolean selectRegisterEnabled(String tenantId);

  /**
   * 查询参数配置列表
   *
   * @param param 参数配置信息
   * @return 参数配置集合
   */
  List<SysConfigVo> queryList(SysConfigQueryParam param);

  /**
   * 新增参数配置
   *
   * @param bo 参数配置信息
   * @return 结果
   */
  String insertConfig(SysConfigBo bo);

  /**
   * 修改参数配置
   *
   * @param bo 参数配置信息
   * @return 结果
   */
  String updateConfig(SysConfigBo bo);

  /**
   * 批量删除参数信息
   *
   * @param configIds 需要删除的参数ID
   */
  void deleteConfigByIds(List<Long> configIds);

  /** 重置参数缓存数据 */
  void resetConfigCache();

  /**
   * 校验参数键名是否唯一
   *
   * @param config 参数信息
   * @return 结果
   */
  boolean checkConfigKeyUnique(SysConfigBo config);
}
