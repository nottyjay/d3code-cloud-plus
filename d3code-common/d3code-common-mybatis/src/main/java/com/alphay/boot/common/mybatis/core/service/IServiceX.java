package com.alphay.boot.common.mybatis.core.service;

import com.alphay.boot.common.core.domain.param.PageReqParam;
import com.alphay.boot.common.mybatis.core.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.List;

/**
 * @author Nottyjay
 * @since
 */
public interface IServiceX<T, V> extends IService<T> {

  /**
   * 获取vo
   *
   * @param id
   * @return
   */
  V getVoById(Serializable id);

  V getOneVo(Wrapper<T> wrapper);

  PageResult<V> listPageVo(PageReqParam param, Wrapper<T> queryWrapper);

  List<V> listVo(Wrapper<T> queryWrapper);
}
