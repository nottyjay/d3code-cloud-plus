package com.alphay.boot.common.sse.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alphay.boot.common.core.domain.R;
import com.alphay.boot.common.satoken.utils.LoginHelper;
import com.alphay.boot.common.sse.core.SseEmitterManager;
import com.alphay.boot.common.sse.dto.SseMessageDto;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 控制器
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@RestController
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true")
public class SseController implements DisposableBean {

  @Resource private SseEmitterManager sseEmitterManager;

  /** 建立 SSE 连接 */
  @GetMapping(value = "${sse.path}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect() {
    StpUtil.checkLogin();
    String tokenValue = StpUtil.getTokenValue();
    Long userId = LoginHelper.getUserId();
    return sseEmitterManager.connect(userId, tokenValue);
  }

  /** 关闭 SSE 连接 */
  @GetMapping(value = "${sse.path}/close")
  public R<Void> close() {
    String tokenValue = StpUtil.getTokenValue();
    Long userId = LoginHelper.getUserId();
    sseEmitterManager.disconnect(userId, tokenValue);
    return R.ok();
  }

  /**
   * 向特定用户发送消息
   *
   * @param userId 目标用户的 ID
   * @param msg 要发送的消息内容
   */
  @GetMapping(value = "${sse.path}/send")
  public R<Void> send(Long userId, String msg) {
    SseMessageDto dto = new SseMessageDto();
    dto.setUserIds(List.of(userId));
    dto.setMessage(msg);
    sseEmitterManager.publishMessage(dto);
    return R.ok();
  }

  /**
   * 向所有用户发送消息
   *
   * @param msg 要发送的消息内容
   */
  @GetMapping(value = "${sse.path}/sendAll")
  public R<Void> send(String msg) {
    sseEmitterManager.publishAll(msg);
    return R.ok();
  }

  /** 清理资源。此方法目前不执行任何操作，但避免因未实现而导致错误 */
  @Override
  public void destroy() throws Exception {
    // 销毁时不需要做什么 此方法避免无用操作报错
  }
}
