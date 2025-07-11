package com.alphay.boot.common.sse.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 消息的dto
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Data
public class SseMessageDto implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 需要推送到的session key 列表 */
  private List<Long> userIds;

  /** 需要发送的消息 */
  private String message;
}
