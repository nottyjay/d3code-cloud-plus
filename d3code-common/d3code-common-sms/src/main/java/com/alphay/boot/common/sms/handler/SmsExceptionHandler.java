package com.alphay.boot.common.sms.handler;

import cn.hutool.http.HttpStatus;
import com.alphay.boot.common.core.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.comm.exception.SmsBlendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SMS异常处理器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class SmsExceptionHandler {

  /** sms异常 */
  @ExceptionHandler(SmsBlendException.class)
  public R<Void> handleSmsBlendException(SmsBlendException e, HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    log.error("请求地址'{}',发生sms短信异常.", requestURI, e);
    return R.fail(HttpStatus.HTTP_INTERNAL_ERROR, "短信发送失败，请稍后再试...");
  }
}
