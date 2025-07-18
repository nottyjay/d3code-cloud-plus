package com.alphay.boot.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.alphay.boot.common.excel.annotation.ExcelDictFormat;
import com.alphay.boot.common.excel.convert.ExcelDictConvert;
import com.alphay.boot.system.domain.SysConfig;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 参数配置视图对象 sys_config
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysConfig.class)
public class SysConfigVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 参数主键 */
  @ExcelProperty(value = "参数主键")
  private Long configId;

  /** 参数名称 */
  @ExcelProperty(value = "参数名称")
  private String configName;

  /** 参数键名 */
  @ExcelProperty(value = "参数键名")
  private String configKey;

  /** 参数键值 */
  @ExcelProperty(value = "参数键值")
  private String configValue;

  /** 系统内置（Y是 N否） */
  @ExcelProperty(value = "系统内置", converter = ExcelDictConvert.class)
  @ExcelDictFormat(dictType = "sys_yes_no")
  private String configType;

  /** 备注 */
  @ExcelProperty(value = "备注")
  private String remark;

  /** 创建时间 */
  @ExcelProperty(value = "创建时间")
  private Date createTime;
}
