package com.alphay.boot.system.mapper;

import com.alphay.boot.common.mybatis.core.mapper.BaseMapperPlus;
import com.alphay.boot.system.domain.SysDictData;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
public interface SysDictDataMapper extends BaseMapperPlus<SysDictData, SysDictDataVo> {

  default List<SysDictDataVo> selectDictDataByType(String dictType) {
    return selectVoList(
        new LambdaQueryWrapper<SysDictData>()
            .eq(SysDictData::getDictType, dictType)
            .orderByAsc(SysDictData::getDictSort));
  }
}
