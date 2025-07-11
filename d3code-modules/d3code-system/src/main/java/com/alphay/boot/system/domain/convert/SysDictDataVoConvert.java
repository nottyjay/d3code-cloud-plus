package com.alphay.boot.system.domain.convert;

import com.alphay.boot.system.api.domain.vo.RemoteDictDataVo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import io.github.linpeilie.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * 字典数据转换器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysDictDataVoConvert extends BaseMapper<SysDictDataVo, RemoteDictDataVo> {}
