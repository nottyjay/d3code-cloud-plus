package com.alphay.boot.common.mybatis.handler;

import cn.hutool.http.HttpStatus;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.core.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Mybatis异常处理器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class MybatisExceptionHandler {

  /** 主键或UNIQUE索引，数据重复异常 */
  @ExceptionHandler(DuplicateKeyException.class)
  public R<Void> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    log.error("请求地址'{}',数据库中已存在记录'{}'", requestURI, e.getMessage());
    return R.fail(HttpStatus.HTTP_CONFLICT, "数据库中已存在该记录，请联系管理员确认");
  }

  /** Mybatis系统异常 通用处理 */
  @ExceptionHandler(MyBatisSystemException.class)
  public R<Void> handleCannotFindDataSourceException(
      MyBatisSystemException e, HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    String message = e.getMessage();
    if (StringUtils.contains(message, "CannotFindDataSourceException")) {
      log.error("请求地址'{}', 未找到数据源", requestURI);
      return R.fail(HttpStatus.HTTP_INTERNAL_ERROR, "未找到数据源，请联系管理员确认");
    }
    log.error("请求地址'{}', Mybatis系统异常", requestURI, e);
    return R.fail(HttpStatus.HTTP_INTERNAL_ERROR, message);
  }
}
