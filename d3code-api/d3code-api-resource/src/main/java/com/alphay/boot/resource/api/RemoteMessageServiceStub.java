package com.alphay.boot.resource.api;

import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息服务
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
public class RemoteMessageServiceStub implements RemoteMessageService {

  @Resource private RemoteMessageService remoteMessageService;

  /**
   * 发送消息
   *
   * @param sessionKey session主键 一般为用户id
   * @param message 消息文本
   */
  @Override
  public void publishMessage(List<Long> sessionKey, String message) {
    try {
      remoteMessageService.publishMessage(sessionKey, message);
    } catch (Exception e) {
      log.warn("推送功能未开启或服务未找到");
    }
  }

  /**
   * 发布订阅的消息(群发)
   *
   * @param message 消息内容
   */
  @Override
  public void publishAll(String message) {
    try {
      remoteMessageService.publishAll(message);
    } catch (Exception e) {
      log.warn("推送功能未开启或服务未找到");
    }
  }
}
