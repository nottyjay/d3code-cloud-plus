package com.alphay.boot.system.api.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 字典类型视图对象 sys_dict_type
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class RemoteDictTypeVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 字典主键 */
  private Long dictId;

  /** 字典名称 */
  private String dictName;

  /** 字典类型 */
  private String dictType;

  /** 备注 */
  private String remark;

  /** 创建时间 */
  private Date createTime;
}
