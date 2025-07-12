package com.alphay.boot.system.dubbo;

import com.alphay.boot.common.core.utils.MapstructUtils;
import com.alphay.boot.system.api.RemoteDictService;
import com.alphay.boot.system.api.domain.vo.RemoteDictDataVo;
import com.alphay.boot.system.api.domain.vo.RemoteDictTypeVo;
import com.alphay.boot.system.domain.vo.SysDictDataVo;
import com.alphay.boot.system.domain.vo.SysDictTypeVo;
import com.alphay.boot.system.service.ISysDictTypeService;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 字典服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Service
@DubboService
public class RemoteDictServiceImpl implements RemoteDictService {

  @Resource private ISysDictTypeService sysDictTypeService;

  @Override
  public RemoteDictTypeVo selectDictTypeByType(String dictType) {
    SysDictTypeVo vo = sysDictTypeService.selectDictTypeByType(dictType);
    return MapstructUtils.convert(vo, RemoteDictTypeVo.class);
  }

  /**
   * 根据字典类型查询字典数据
   *
   * @param dictType 字典类型
   * @return 字典数据集合信息
   */
  @Override
  public List<RemoteDictDataVo> selectDictDataByType(String dictType) {
    List<SysDictDataVo> list = sysDictTypeService.selectDictDataByType(dictType);
    return MapstructUtils.convert(list, RemoteDictDataVo.class);
  }
}
