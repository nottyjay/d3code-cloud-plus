package com.alphay.boot.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.system.api.domain.param.SysTenantPackageQueryParam;
import com.alphay.boot.system.domain.SysTenant;
import com.alphay.boot.system.domain.SysTenantPackage;
import com.alphay.boot.system.domain.bo.SysTenantPackageBo;
import com.alphay.boot.system.domain.vo.SysTenantPackageVo;
import com.alphay.boot.system.mapper.SysTenantMapper;
import com.alphay.boot.system.mapper.SysTenantPackageMapper;
import com.alphay.boot.system.service.ISysTenantPackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户套餐Service业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
public class SysTenantPackageServiceImpl
    extends ServiceImplX<SysTenantPackageMapper, SysTenantPackage, SysTenantPackageVo>
    implements ISysTenantPackageService {

  @Resource private SysTenantMapper tenantMapper;

  /** 查询租户套餐列表 */
  @Override
  public PageResult<SysTenantPackageVo> queryPageList(SysTenantPackageQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  @Override
  public List<SysTenantPackageVo> queryList() {
    return listVo(
        new LambdaQueryWrapper<SysTenantPackage>()
            .eq(SysTenantPackage::getStatus, SystemConstants.NORMAL));
  }

  /** 查询租户套餐列表 */
  @Override
  public List<SysTenantPackageVo> queryList(SysTenantPackageQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysTenantPackage> buildQueryWrapper(SysTenantPackageQueryParam param) {
    LambdaQueryWrapper<SysTenantPackage> lqw =
        this.lambdaQueryWrapper()
            .likeIfPresent(SysTenantPackage::getPackageName, param.getPackageName())
            .eqIfPresent(SysTenantPackage::getStatus, param.getStatus());
    lqw.orderByAsc(SysTenantPackage::getPackageId);
    return lqw;
  }

  /** 新增租户套餐 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean insertByBo(SysTenantPackageBo bo) {
    SysTenantPackage add = MapstructUtils.convert(bo, SysTenantPackage.class);
    // 保存菜单id
    List<Long> menuIds = Arrays.asList(bo.getMenuIds());
    if (CollUtil.isNotEmpty(menuIds)) {
      add.setMenuIds(StringUtils.join(menuIds, ", "));
    } else {
      add.setMenuIds("");
    }
    boolean flag = baseMapper.insert(add) > 0;
    if (flag) {
      bo.setPackageId(add.getPackageId());
    }
    return flag;
  }

  /** 修改租户套餐 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean updateByBo(SysTenantPackageBo bo) {
    SysTenantPackage update = MapstructUtils.convert(bo, SysTenantPackage.class);
    // 保存菜单id
    List<Long> menuIds = Arrays.asList(bo.getMenuIds());
    if (CollUtil.isNotEmpty(menuIds)) {
      update.setMenuIds(StringUtils.join(menuIds, ", "));
    } else {
      update.setMenuIds("");
    }
    return baseMapper.updateById(update) > 0;
  }

  /** 校验套餐名称是否唯一 */
  @Override
  public boolean checkPackageNameUnique(SysTenantPackageQueryParam param) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysTenantPackage>()
                .eq(SysTenantPackage::getPackageName, param.getPackageName())
                .ne(
                    ObjectUtil.isNotNull(param.getPackageId()),
                    SysTenantPackage::getPackageId,
                    param.getPackageId()));
    return !exist;
  }

  /**
   * 修改套餐状态
   *
   * @param bo 套餐信息
   * @return 结果
   */
  @Override
  public int updatePackageStatus(SysTenantPackageBo bo) {
    SysTenantPackage tenantPackage = MapstructUtils.convert(bo, SysTenantPackage.class);
    return baseMapper.updateById(tenantPackage);
  }

  /** 批量删除租户套餐 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
    if (isValid) {
      boolean exists =
          tenantMapper.exists(new LambdaQueryWrapper<SysTenant>().in(SysTenant::getPackageId, ids));
      if (exists) {
        throw new ServiceException("租户套餐已被使用");
      }
    }
    return baseMapper.deleteByIds(ids) > 0;
  }
}
