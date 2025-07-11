package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperX;
import com.alphay.boot.system.domain.SysDictData;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;

/**
 * 字典表 数据层
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface SysDictDataMapper extends BaseMapperX<SysDictData> {

  default List<SysDictDataVo> selectDictDataByType(String dictType) {
    return selectList(
        new LambdaQueryWrapper<SysDictData>()
            .eq(SysDictData::getDictType, dictType)
            .orderByAsc(SysDictData::getDictSort),
        SysDictDataVo.class);
  }
}
