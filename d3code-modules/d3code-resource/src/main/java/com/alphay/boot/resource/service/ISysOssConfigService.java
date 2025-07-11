package com.alphay.boot.resource.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.resource.api.domain.param.SysOssConfigQueryParam;
import com.alphay.boot.resource.domain.SysOssConfig;
import com.alphay.boot.resource.domain.bo.SysOssConfigBo;
import com.alphay.boot.resource.domain.vo.SysOssConfigVo;
import java.util.Collection;

/**
 * 对象存储配置Service接口
 *
 * @author Nottyjay
 * @since 1.0.0
 * @author Nottyjay
 * @since 1.0.0
 * @date 2021-08-13
 */
public interface ISysOssConfigService extends IServiceX<SysOssConfig, SysOssConfigVo> {

  /** 初始化OSS配置 */
  void init();

  /** 查询列表 */
  PageResult<SysOssConfigVo> queryPageList(SysOssConfigQueryParam param);

  /**
   * 根据新增业务对象插入对象存储配置
   *
   * @param bo 对象存储配置新增业务对象
   * @return 结果
   */
  Boolean insertByBo(SysOssConfigBo bo);

  /**
   * 根据编辑业务对象修改对象存储配置
   *
   * @param bo 对象存储配置编辑业务对象
   * @return 结果
   */
  Boolean updateByBo(SysOssConfigBo bo);

  /**
   * 校验并删除数据
   *
   * @param ids 主键集合
   * @param isValid 是否校验,true-删除前校验,false-不校验
   * @return 结果
   */
  Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

  /** 启用停用状态 */
  int updateOssConfigStatus(SysOssConfigBo bo);
}
