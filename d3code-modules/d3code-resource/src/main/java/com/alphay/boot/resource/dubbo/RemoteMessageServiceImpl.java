package com.alphay.boot.resource.dubbo;

import com.alphay.boot.common.sse.dto.SseMessageDto;
import com.alphay.boot.common.sse.utils.SseMessageUtils;
import com.alphay.boot.resource.api.RemoteMessageService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 消息服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
@Service
@DubboService
public class RemoteMessageServiceImpl implements RemoteMessageService {

  /**
   * 发送消息
   *
   * @param sessionKey session主键 一般为用户id
   * @param message 消息文本
   */
  @Override
  public void publishMessage(List<Long> sessionKey, String message) {
    SseMessageDto dto = new SseMessageDto();
    dto.setMessage(message);
    dto.setUserIds(sessionKey);
    SseMessageUtils.publishMessage(dto);
  }

  /**
   * 发布订阅的消息(群发)
   *
   * @param message 消息内容
   */
  @Override
  public void publishAll(String message) {
    SseMessageUtils.publishAll(message);
  }
}
