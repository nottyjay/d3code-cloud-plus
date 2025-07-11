package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysTenantQueryParam;
import com.alphay.boot.system.domain.SysTenant;
import com.alphay.boot.system.domain.bo.SysTenantBo;
import com.alphay.boot.system.domain.vo.SysTenantVo;
import java.util.Collection;
import java.util.List;

/**
 * 租户Service接口
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysTenantService extends IServiceX<SysTenant, SysTenantVo> {

  /** 基于租户ID查询租户 */
  SysTenantVo getVoByTenantId(String tenantId);

  /** 查询租户列表 */
  PageResult<SysTenantVo> queryPageList(SysTenantQueryParam param);

  /** 查询租户列表 */
  List<SysTenantVo> queryList(SysTenantQueryParam param);

  /** 新增租户 */
  Boolean insertByBo(SysTenantBo bo);

  /** 修改租户 */
  Boolean updateByBo(SysTenantBo bo);

  /** 修改租户状态 */
  int updateTenantStatus(SysTenantBo bo);

  /** 校验租户是否允许操作 */
  void checkTenantAllowed(String tenantId);

  /** 校验并批量删除租户信息 */
  Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

  /** 校验企业名称是否唯一 */
  boolean checkCompanyNameUnique(SysTenantBo bo);

  /** 校验账号余额 */
  boolean checkAccountBalance(String tenantId);

  /** 校验有效期 */
  boolean checkExpireTime(String tenantId);

  /** 同步租户套餐 */
  Boolean syncTenantPackage(String tenantId, Long packageId);

  /** 同步租户字典 */
  void syncTenantDict();
}
