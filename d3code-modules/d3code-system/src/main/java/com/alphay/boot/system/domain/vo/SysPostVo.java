package com.alphay.boot.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.alphay.boot.common.excel.annotation.ExcelDictFormat;
import com.alphay.boot.common.excel.convert.ExcelDictConvert;
import com.alphay.boot.common.translation.annotation.Translation;
import com.alphay.boot.common.translation.constant.TransConstant;
import com.alphay.boot.system.domain.SysPost;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 岗位信息视图对象 sys_post
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysPost.class)
public class SysPostVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 岗位ID */
  @ExcelProperty(value = "岗位序号")
  private Long postId;

  /** 部门id */
  @ExcelProperty(value = "部门id")
  private Long deptId;

  /** 岗位编码 */
  @ExcelProperty(value = "岗位编码")
  private String postCode;

  /** 岗位名称 */
  @ExcelProperty(value = "岗位名称")
  private String postName;

  /** 岗位类别编码 */
  @ExcelProperty(value = "类别编码")
  private String postCategory;

  /** 显示顺序 */
  @ExcelProperty(value = "岗位排序")
  private Integer postSort;

  /** 状态（0正常 1停用） */
  @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
  @ExcelDictFormat(dictType = "sys_normal_disable")
  private String status;

  /** 备注 */
  @ExcelProperty(value = "备注")
  private String remark;

  /** 创建时间 */
  @ExcelProperty(value = "创建时间")
  private Date createTime;

  /** 部门名 */
  @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
  private String deptName;
}
