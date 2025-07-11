package com.alphay.boot.auth.domain.convert;

import com.alphay.boot.auth.domain.vo.TenantListVo;
import com.alphay.boot.system.api.domain.vo.RemoteTenantVo;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 租户vo转换器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantVoConvert extends BaseMapper<RemoteTenantVo, TenantListVo> {}
