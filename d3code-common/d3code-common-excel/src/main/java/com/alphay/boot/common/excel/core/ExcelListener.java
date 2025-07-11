package com.alphay.boot.common.excel.core;

import cn.idev.excel.read.listener.ReadListener;

/**
 * Excel 导入监听
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface ExcelListener<T> extends ReadListener<T> {

  ExcelResult<T> getExcelResult();
}
