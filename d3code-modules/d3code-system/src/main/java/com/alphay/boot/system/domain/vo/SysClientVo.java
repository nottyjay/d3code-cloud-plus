package com.alphay.boot.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.alphay.boot.common.excel.annotation.ExcelDictFormat;
import com.alphay.boot.common.excel.convert.ExcelDictConvert;
import com.alphay.boot.system.domain.SysClient;
import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 授权管理视图对象 sys_client
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysClient.class)
public class SysClientVo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** id */
  @ExcelProperty(value = "id")
  private Long id;

  /** 客户端id */
  @ExcelProperty(value = "客户端id")
  private String clientId;

  /** 客户端key */
  @ExcelProperty(value = "客户端key")
  private String clientKey;

  /** 客户端秘钥 */
  @ExcelProperty(value = "客户端秘钥")
  private String clientSecret;

  /** 授权类型 */
  @ExcelProperty(value = "授权类型")
  private List<String> grantTypeList;

  /** 授权类型 */
  private String grantType;

  /** 设备类型 */
  private String deviceType;

  /** token活跃超时时间 */
  @ExcelProperty(value = "token活跃超时时间")
  private Long activeTimeout;

  /** token固定超时时间 */
  @ExcelProperty(value = "token固定超时时间")
  private Long timeout;

  /** 状态（0正常 1停用） */
  @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
  @ExcelDictFormat(readConverterExp = "0=正常,1=停用")
  private String status;
}
