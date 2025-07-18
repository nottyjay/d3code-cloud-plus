package com.alphay.boot.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.alphay.boot.common.excel.annotation.ExcelDictFormat;
import com.alphay.boot.common.excel.convert.ExcelDictConvert;
import com.alphay.boot.system.domain.SysDictData;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 字典数据视图对象 sys_dict_data
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDictData.class)
public class SysDictDataVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 字典编码 */
  @ExcelProperty(value = "字典编码")
  private Long dictCode;

  /** 字典排序 */
  @ExcelProperty(value = "字典排序")
  private Integer dictSort;

  /** 字典标签 */
  @ExcelProperty(value = "字典标签")
  private String dictLabel;

  /** 字典键值 */
  @ExcelProperty(value = "字典键值")
  private String dictValue;

  /** 字典类型 */
  @ExcelProperty(value = "字典类型")
  private String dictType;

  /** 样式属性（其他样式扩展） */
  private String cssClass;

  /** 表格回显样式 */
  private String listClass;

  /** 是否默认（Y是 N否） */
  @ExcelProperty(value = "是否默认", converter = ExcelDictConvert.class)
  @ExcelDictFormat(dictType = "sys_yes_no")
  private String isDefault;

  /** 备注 */
  @ExcelProperty(value = "备注")
  private String remark;

  /** 创建时间 */
  @ExcelProperty(value = "创建时间")
  private Date createTime;
}
