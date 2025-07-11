package com.alphay.boot.gen.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.gen.domain.GenTableColumn;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

/**
 * 业务字段 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@InterceptorIgnore(dataPermission = "true", tenantLine = "true")
public interface GenTableColumnMapper extends BaseMapperX<GenTableColumn> {}
