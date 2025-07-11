package com.alphay.boot.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.alphay.boot.common.core.constant.CacheNames;
import com.alphay.boot.common.core.constant.Constants;
import com.alphay.boot.common.core.constant.SystemConstants;
import com.alphay.boot.common.core.constant.TenantConstants;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.common.core.utils.SpringUtils;
import com.alphay.boot.common.core.utils.StreamUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.alphay.boot.common.mybatis.core.service.ServiceImplX;
import com.alphay.boot.common.redis.utils.CacheUtils;
import com.alphay.boot.common.tenant.core.TenantEntity;
import com.alphay.boot.common.tenant.helper.TenantHelper;
import com.alphay.boot.system.api.domain.param.SysTenantQueryParam;
import com.alphay.boot.system.domain.*;
import com.alphay.boot.system.domain.bo.SysTenantBo;
import com.alphay.boot.system.domain.vo.SysTenantVo;
import com.alphay.boot.system.mapper.*;
import com.alphay.boot.system.service.ISysTenantService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户Service业务层处理
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl extends ServiceImplX<SysTenantMapper, SysTenant, SysTenantVo>
    implements ISysTenantService {

  @Resource private SysTenantPackageMapper tenantPackageMapper;
  @Resource private SysUserMapper userMapper;
  @Resource private SysDeptMapper deptMapper;
  @Resource private SysRoleMapper roleMapper;
  @Resource private SysRoleMenuMapper roleMenuMapper;
  @Resource private SysRoleDeptMapper roleDeptMapper;
  @Resource private SysUserRoleMapper userRoleMapper;
  @Resource private SysDictTypeMapper dictTypeMapper;
  @Resource private SysDictDataMapper dictDataMapper;
  @Resource private SysConfigMapper configMapper;

  /** 基于租户ID查询租户 */
  @Cacheable(cacheNames = CacheNames.SYS_TENANT, key = "#tenantId")
  @Override
  public SysTenantVo getVoByTenantId(String tenantId) {
    return getOneVo(SysTenant::getTenantId, tenantId);
  }

  /** 查询租户列表 */
  @Override
  public PageResult<SysTenantVo> queryPageList(SysTenantQueryParam param) {
    return listPageVo(param, buildQueryWrapper(param));
  }

  /** 查询租户列表 */
  @Override
  public List<SysTenantVo> queryList(SysTenantQueryParam param) {
    return listVo(buildQueryWrapper(param));
  }

  private LambdaQueryWrapper<SysTenant> buildQueryWrapper(SysTenantQueryParam param) {
    LambdaQueryWrapper<SysTenant> lqw =
        this.lambdaQueryWrapper()
            .eqIfPresent(SysTenant::getTenantId, param.getTenantId())
            .likeIfPresent(SysTenant::getContactUserName, param.getContactUserName())
            .eqIfPresent(SysTenant::getContactPhone, param.getContactPhone())
            .likeIfPresent(SysTenant::getCompanyName, param.getCompanyName())
            .eqIfPresent(SysTenant::getLicenseNumber, param.getLicenseNumber())
            .eqIfPresent(SysTenant::getAddress, param.getAddress())
            .eqIfPresent(SysTenant::getIntro, param.getIntro())
            .likeIfPresent(SysTenant::getDomain, param.getDomain())
            .eqIfPresent(SysTenant::getPackageId, param.getPackageId())
            .eqIfPresent(SysTenant::getExpireTime, param.getExpireTime())
            .eqIfPresent(SysTenant::getAccountCount, param.getAccountCount())
            .eqIfPresent(SysTenant::getStatus, param.getStatus());
    lqw.orderByAsc(SysTenant::getId);
    return lqw;
  }

  /** 新增租户 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean insertByBo(SysTenantBo bo) {
    SysTenant add = MapstructUtils.convert(bo, SysTenant.class);

    // 获取所有租户编号
    List<String> tenantIds =
        baseMapper.selectObjs(
            new LambdaQueryWrapper<SysTenant>().select(SysTenant::getTenantId),
            x -> {
              return Convert.toStr(x);
            });
    String tenantId = generateTenantId(tenantIds);
    add.setTenantId(tenantId);
    boolean flag = baseMapper.insert(add) > 0;
    if (!flag) {
      throw new ServiceException("创建租户失败");
    }
    bo.setId(add.getId());

    // 根据套餐创建角色
    Long roleId = createTenantRole(tenantId, bo.getPackageId());

    // 创建部门: 公司名是部门名称
    SysDept dept = new SysDept();
    dept.setTenantId(tenantId);
    dept.setDeptName(bo.getCompanyName());
    dept.setParentId(Constants.TOP_PARENT_ID);
    dept.setAncestors(Constants.TOP_PARENT_ID.toString());
    deptMapper.insert(dept);
    Long deptId = dept.getDeptId();

    // 角色和部门关联表
    SysRoleDept roleDept = new SysRoleDept();
    roleDept.setRoleId(roleId);
    roleDept.setDeptId(deptId);
    roleDeptMapper.insert(roleDept);

    // 创建系统用户
    SysUser user = new SysUser();
    user.setTenantId(tenantId);
    user.setUserName(bo.getUsername());
    user.setNickName(bo.getUsername());
    user.setPassword(BCrypt.hashpw(bo.getPassword()));
    user.setDeptId(deptId);
    userMapper.insert(user);
    // 新增系统用户后，默认当前用户为部门的负责人
    SysDept sd = new SysDept();
    sd.setLeader(user.getUserId());
    sd.setDeptId(deptId);
    deptMapper.updateById(sd);

    // 用户和角色关联表
    SysUserRole userRole = new SysUserRole();
    userRole.setUserId(user.getUserId());
    userRole.setRoleId(roleId);
    userRoleMapper.insert(userRole);

    String defaultTenantId = TenantConstants.DEFAULT_TENANT_ID;
    List<SysDictType> dictTypeList =
        dictTypeMapper.selectList(
            new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getTenantId, defaultTenantId));
    List<SysDictData> dictDataList =
        dictDataMapper.selectList(
            new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getTenantId, defaultTenantId));
    for (SysDictType dictType : dictTypeList) {
      dictType.setDictId(null);
      dictType.setTenantId(tenantId);
      dictType.setCreateDept(null);
      dictType.setCreateBy(null);
      dictType.setCreateTime(null);
      dictType.setUpdateBy(null);
      dictType.setUpdateTime(null);
    }
    for (SysDictData dictData : dictDataList) {
      dictData.setDictCode(null);
      dictData.setTenantId(tenantId);
      dictData.setCreateDept(null);
      dictData.setCreateBy(null);
      dictData.setCreateTime(null);
      dictData.setUpdateBy(null);
      dictData.setUpdateTime(null);
    }
    dictTypeMapper.insertBatch(dictTypeList);
    dictDataMapper.insertBatch(dictDataList);

    List<SysConfig> sysConfigList =
        configMapper.selectList(
            new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getTenantId, defaultTenantId));
    for (SysConfig config : sysConfigList) {
      config.setConfigId(null);
      config.setTenantId(tenantId);
      config.setCreateDept(null);
      config.setCreateBy(null);
      config.setCreateTime(null);
      config.setUpdateBy(null);
      config.setUpdateTime(null);
    }
    configMapper.insertBatch(sysConfigList);
    return true;
  }

  /**
   * 生成租户id
   *
   * @param tenantIds 已有租户id列表
   * @return 租户id
   */
  private String generateTenantId(List<String> tenantIds) {
    // 随机生成6位
    String numbers = RandomUtil.randomNumbers(6);
    // 判断是否存在，如果存在则重新生成
    if (tenantIds.contains(numbers)) {
      return generateTenantId(tenantIds);
    }
    return numbers;
  }

  /**
   * 根据租户菜单创建租户角色
   *
   * @param tenantId 租户编号
   * @param packageId 租户套餐id
   * @return 角色id
   */
  private Long createTenantRole(String tenantId, Long packageId) {
    // 获取租户套餐
    SysTenantPackage tenantPackage = tenantPackageMapper.selectById(packageId);
    if (ObjectUtil.isNull(tenantPackage)) {
      throw new ServiceException("套餐不存在");
    }
    // 获取套餐菜单id
    List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);

    // 创建角色
    SysRole role = new SysRole();
    role.setTenantId(tenantId);
    role.setRoleName(TenantConstants.TENANT_ADMIN_ROLE_NAME);
    role.setRoleKey(TenantConstants.TENANT_ADMIN_ROLE_KEY);
    role.setRoleSort(1);
    role.setStatus(SystemConstants.NORMAL);
    roleMapper.insert(role);
    Long roleId = role.getRoleId();

    // 创建角色菜单
    List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
    menuIds.forEach(
        menuId -> {
          SysRoleMenu roleMenu = new SysRoleMenu();
          roleMenu.setRoleId(roleId);
          roleMenu.setMenuId(menuId);
          roleMenus.add(roleMenu);
        });
    roleMenuMapper.insertBatch(roleMenus);

    return roleId;
  }

  /** 修改租户 */
  @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
  @Override
  public Boolean updateByBo(SysTenantBo bo) {
    SysTenant tenant = MapstructUtils.convert(bo, SysTenant.class);
    tenant.setTenantId(null);
    tenant.setPackageId(null);
    return baseMapper.updateById(tenant) > 0;
  }

  /**
   * 修改租户状态
   *
   * @param bo 租户信息
   * @return 结果
   */
  @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
  @Override
  public int updateTenantStatus(SysTenantBo bo) {
    SysTenant tenant = new SysTenant();
    tenant.setId(bo.getId());
    tenant.setStatus(bo.getStatus());
    return baseMapper.updateById(tenant);
  }

  /**
   * 校验租户是否允许操作
   *
   * @param tenantId 租户ID
   */
  @Override
  public void checkTenantAllowed(String tenantId) {
    if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
      throw new ServiceException("不允许操作管理租户");
    }
  }

  /** 批量删除租户 */
  @CacheEvict(cacheNames = CacheNames.SYS_TENANT, allEntries = true)
  @Override
  public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
    if (isValid) {
      // 做一些业务上的校验,判断是否需要校验
      if (ids.contains(TenantConstants.SUPER_ADMIN_ID)) {
        throw new ServiceException("超管租户不能删除");
      }
    }
    return baseMapper.deleteByIds(ids) > 0;
  }

  /** 校验企业名称是否唯一 */
  @Override
  public boolean checkCompanyNameUnique(SysTenantBo bo) {
    boolean exist =
        baseMapper.exists(
            new LambdaQueryWrapper<SysTenant>()
                .eq(SysTenant::getCompanyName, bo.getCompanyName())
                .ne(
                    ObjectUtil.isNotNull(bo.getTenantId()),
                    SysTenant::getTenantId,
                    bo.getTenantId()));
    return !exist;
  }

  /** 校验账号余额 */
  @Override
  public boolean checkAccountBalance(String tenantId) {
    SysTenantVo tenant = SpringUtils.getAopProxy(this).getVoByTenantId(tenantId);
    // 如果余额为-1代表不限制
    if (tenant.getAccountCount() == -1) {
      return true;
    }
    Long userNumber = userMapper.selectCount(new LambdaQueryWrapper<>());
    // 如果余额大于0代表还有可用名额
    return tenant.getAccountCount() - userNumber > 0;
  }

  /** 校验有效期 */
  @Override
  public boolean checkExpireTime(String tenantId) {
    SysTenantVo tenant = SpringUtils.getAopProxy(this).getVoByTenantId(tenantId);
    // 如果未设置过期时间代表不限制
    if (ObjectUtil.isNull(tenant.getExpireTime())) {
      return true;
    }
    // 如果当前时间在过期时间之前则通过
    return new Date().before(tenant.getExpireTime());
  }

  /** 同步租户套餐 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean syncTenantPackage(String tenantId, Long packageId) {
    SysTenantPackage tenantPackage = tenantPackageMapper.selectById(packageId);
    List<SysRole> roles =
        roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getTenantId, tenantId));
    List<Long> roleIds = new ArrayList<>(roles.size() - 1);
    List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);
    roles.forEach(
        item -> {
          if (TenantConstants.TENANT_ADMIN_ROLE_KEY.equals(item.getRoleKey())) {
            List<SysRoleMenu> roleMenus = new ArrayList<>(menuIds.size());
            menuIds.forEach(
                menuId -> {
                  SysRoleMenu roleMenu = new SysRoleMenu();
                  roleMenu.setRoleId(item.getRoleId());
                  roleMenu.setMenuId(menuId);
                  roleMenus.add(roleMenu);
                });
            roleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, item.getRoleId()));
            roleMenuMapper.insertBatch(roleMenus);
          } else {
            roleIds.add(item.getRoleId());
          }
        });
    if (!roleIds.isEmpty()) {
      roleMenuMapper.delete(
          new LambdaQueryWrapper<SysRoleMenu>()
              .in(SysRoleMenu::getRoleId, roleIds)
              .notIn(!menuIds.isEmpty(), SysRoleMenu::getMenuId, menuIds));
    }
    return true;
  }

  /** 同步租户字典 */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void syncTenantDict() {
    // 查询超管 所有字典数据
    List<SysDictType> dictTypeList = new ArrayList<>();
    List<SysDictData> dictDataList = new ArrayList<>();
    TenantHelper.ignore(
        () -> {
          dictTypeList.addAll(dictTypeMapper.selectList(null));
          dictDataList.addAll(dictDataMapper.selectList(null));
        });
    Map<String, List<SysDictType>> typeMap =
        StreamUtils.groupByKey(dictTypeList, TenantEntity::getTenantId);
    Map<String, Map<String, List<SysDictData>>> typeDataMap =
        StreamUtils.groupBy2Key(dictDataList, TenantEntity::getTenantId, SysDictData::getDictType);
    // 管理租户字典数据
    List<SysDictType> defaultTypeMap = typeMap.get(TenantConstants.DEFAULT_TENANT_ID);
    Map<String, List<SysDictData>> defaultTypeDataMap =
        typeDataMap.get(TenantConstants.DEFAULT_TENANT_ID);

    // 获取所有租户编号
    List<String> tenantIds =
        baseMapper.selectObjs(
            new LambdaQueryWrapper<SysTenant>()
                .select(SysTenant::getTenantId)
                .eq(SysTenant::getStatus, SystemConstants.NORMAL),
            x -> {
              return Convert.toStr(x);
            });
    List<SysDictType> saveTypeList = new ArrayList<>();
    List<SysDictData> saveDataList = new ArrayList<>();
    Set<String> set = new HashSet<>();
    for (String tenantId : tenantIds) {
      if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
        continue;
      }
      for (SysDictType dictType : defaultTypeMap) {
        List<String> typeList = StreamUtils.toList(typeMap.get(tenantId), SysDictType::getDictType);
        List<SysDictData> dataList = defaultTypeDataMap.get(dictType.getDictType());
        if (typeList.contains(dictType.getDictType())) {
          List<SysDictData> dataListTenant = typeDataMap.get(tenantId).get(dictType.getDictType());
          Map<String, SysDictData> map =
              StreamUtils.toIdentityMap(dataListTenant, SysDictData::getDictValue);
          for (SysDictData dictData : dataList) {
            if (!map.containsKey(dictData.getDictValue())) {
              SysDictData data = BeanUtil.toBean(dictData, SysDictData.class);
              // 设置字典编码为 null
              data.setDictCode(null);
              data.setTenantId(tenantId);
              data.setCreateTime(null);
              data.setUpdateTime(null);
              data.setCreateDept(null);
              data.setCreateBy(null);
              data.setUpdateBy(null);
              set.add(tenantId);
              saveDataList.add(data);
            }
          }
        } else {
          SysDictType type = BeanUtil.toBean(dictType, SysDictType.class);
          type.setDictId(null);
          type.setTenantId(tenantId);
          type.setCreateTime(null);
          type.setUpdateTime(null);
          type.setCreateDept(null);
          type.setCreateBy(null);
          type.setUpdateBy(null);
          set.add(tenantId);
          saveTypeList.add(type);
          if (CollUtil.isNotEmpty(dataList)) {
            // 筛选出 dictType 对应的 data
            for (SysDictData dictData : dataList) {
              SysDictData data = BeanUtil.toBean(dictData, SysDictData.class);
              // 设置字典编码为 null
              data.setDictCode(null);
              data.setTenantId(tenantId);
              data.setCreateTime(null);
              data.setUpdateTime(null);
              set.add(tenantId);
              saveDataList.add(data);
            }
          }
        }
      }
    }
    TenantHelper.ignore(
        () -> {
          if (CollUtil.isNotEmpty(saveTypeList)) {
            dictTypeMapper.insertBatch(saveTypeList);
          }
          if (CollUtil.isNotEmpty(saveDataList)) {
            dictDataMapper.insertBatch(saveDataList);
          }
        });
    for (String tenantId : set) {
      TenantHelper.dynamic(tenantId, () -> CacheUtils.clear(CacheNames.SYS_DICT));
    }
  }
}
