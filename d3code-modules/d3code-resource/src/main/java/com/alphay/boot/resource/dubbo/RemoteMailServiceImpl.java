package com.alphay.boot.resource.dubbo;

import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.mail.utils.MailUtils;
import com.alphay.boot.resource.api.RemoteMailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
@DubboService
public class RemoteMailServiceImpl implements RemoteMailService {

  /**
   * 发送邮件
   *
   * @param to 接收人
   * @param subject 标题
   * @param text 内容
   */
  @Override
  public void send(String to, String subject, String text) throws ServiceException {
    MailUtils.sendText(to, subject, text);
  }
}
