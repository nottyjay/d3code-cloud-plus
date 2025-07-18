package com.alphay.boot.common.core.config;

import cn.hutool.core.util.ArrayUtil;
import com.alphay.boot.common.core.exception.ServiceException;
import com.alphay.boot.common.core.utils.SpringUtils;
import java.util.Arrays;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * 异步配置
 *
 * <p>如果未使用虚拟线程则生效
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
public class AsyncConfig implements AsyncConfigurer {

  /** 自定义 @Async 注解使用系统线程池 */
  @Override
  public Executor getAsyncExecutor() {
    if (SpringUtils.isVirtual()) {
      return new VirtualThreadTaskExecutor("async-");
    }
    return SpringUtils.getBean("scheduledExecutorService");
  }

  /** 异步执行异常处理 */
  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, objects) -> {
      throwable.printStackTrace();
      StringBuilder sb = new StringBuilder();
      sb.append("Exception message - ")
          .append(throwable.getMessage())
          .append(", Method name - ")
          .append(method.getName());
      if (ArrayUtil.isNotEmpty(objects)) {
        sb.append(", Parameter value - ").append(Arrays.toString(objects));
      }
      throw new ServiceException(sb.toString());
    };
  }
}
