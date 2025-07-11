package com.alphay.boot.resource.api;

import com.alphay.boot.common.core.exception.ServiceException;

/**
 * 邮件服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public interface RemoteMailService {

  /**
   * 发送邮件
   *
   * @param to 接收人
   * @param subject 标题
   * @param text 内容
   */
  void send(String to, String subject, String text) throws ServiceException;
}
