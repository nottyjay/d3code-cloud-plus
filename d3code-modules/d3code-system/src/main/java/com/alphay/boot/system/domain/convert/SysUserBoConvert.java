package com.alphay.boot.system.domain.convert;

import com.alphay.boot.system.api.domain.bo.RemoteUserBo;
import com.alphay.boot.system.domain.bo.SysUserBo;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * 用户信息转换器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserBoConvert extends BaseMapper<RemoteUserBo, SysUserBo> {

  /**
   * RemoteUserBoToSysUserBo
   *
   * @param remoteUserBo 待转换对象
   * @return 转换后对象
   */
  @Mapping(target = "roleIds", ignore = true)
  @Mapping(target = "postIds", ignore = true)
  SysUserBo convert(RemoteUserBo remoteUserBo);
}
