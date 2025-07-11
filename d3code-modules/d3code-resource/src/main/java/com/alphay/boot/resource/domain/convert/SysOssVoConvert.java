package com.alphay.boot.resource.domain.convert;

import com.alphay.boot.resource.api.domain.RemoteFile;
import com.alphay.boot.resource.domain.vo.SysOssVo;
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
public interface SysOssVoConvert extends BaseMapper<SysOssVo, RemoteFile> {

  @Mapping(target = "name", source = "fileName")
  RemoteFile convert(SysOssVo sysOssVo);
}
