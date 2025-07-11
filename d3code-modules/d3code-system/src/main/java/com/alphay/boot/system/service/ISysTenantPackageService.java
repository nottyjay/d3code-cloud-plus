package com.alphay.boot.system.service;

import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.IServiceX;
import com.alphay.boot.system.api.domain.param.SysTenantPackageQueryParam;
import com.alphay.boot.system.domain.SysTenantPackage;
import com.alphay.boot.system.domain.bo.SysTenantPackageBo;
import com.alphay.boot.system.domain.vo.SysTenantPackageVo;
import java.util.Collection;
import java.util.List;

/**
 * 租户套餐Service接口
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ISysTenantPackageService extends IServiceX<SysTenantPackage, SysTenantPackageVo> {

  /** 查询租户套餐列表 */
  PageResult<SysTenantPackageVo> queryPageList(SysTenantPackageQueryParam param);

  /** 查询租户套餐已启用列表 */
  List<SysTenantPackageVo> queryList();

  /** 查询租户套餐列表 */
  List<SysTenantPackageVo> queryList(SysTenantPackageQueryParam param);

  /** 新增租户套餐 */
  Boolean insertByBo(SysTenantPackageBo bo);

  /** 修改租户套餐 */
  Boolean updateByBo(SysTenantPackageBo bo);

  /** 校验套餐名称是否唯一 */
  boolean checkPackageNameUnique(SysTenantPackageQueryParam param);

  /** 修改套餐状态 */
  int updatePackageStatus(SysTenantPackageBo bo);

  /** 校验并批量删除租户套餐信息 */
  Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
