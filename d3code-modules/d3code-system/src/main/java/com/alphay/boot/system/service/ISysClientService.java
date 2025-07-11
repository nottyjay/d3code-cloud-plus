package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysClientQueryParam;
import com.alphay.boot.system.domain.SysClient;
import com.alphay.boot.system.domain.bo.SysClientBo;
import com.alphay.boot.system.domain.vo.SysClientVo;
import java.util.Collection;
import java.util.List;

/**
 * 客户端管理Service接口
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysClientService extends IServiceX<SysClient, SysClientVo> {

  /** 查询客户端信息基于客户端id */
  SysClientVo queryByClientId(String clientId);

  /** 查询客户端管理列表 */
  PageResult<SysClientVo> queryPageList(SysClientQueryParam param);

  /** 查询客户端管理列表 */
  List<SysClientVo> queryList(SysClientQueryParam param);

  /** 新增客户端管理 */
  Boolean insertByBo(SysClientBo bo);

  /** 修改客户端管理 */
  Boolean updateByBo(SysClientBo bo);

  /** 修改状态 */
  int updateClientStatus(String clientId, String status);

  /** 校验并批量删除客户端管理信息 */
  Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
