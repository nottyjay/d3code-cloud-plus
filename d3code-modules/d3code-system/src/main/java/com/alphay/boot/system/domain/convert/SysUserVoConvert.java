package com.alphay.boot.system.domain.convert;

import com.alphay.boot.system.api.domain.vo.RemoteUserVo;
import com.alphay.boot.system.domain.vo.SysUserVo;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * 租户转换器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserVoConvert extends BaseMapper<SysUserVo, RemoteUserVo> {}
