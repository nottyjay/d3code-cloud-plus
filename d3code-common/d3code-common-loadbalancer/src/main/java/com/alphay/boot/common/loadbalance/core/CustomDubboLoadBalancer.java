package com.alphay.boot.common.loadbalance.core;

import cn.hutool.core.net.NetUtil;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

/**
 * 自定义 Dubbo 负载均衡算法
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@Slf4j
public class CustomDubboLoadBalancer extends AbstractLoadBalance {

  @Override
  protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
    for (Invoker<T> invoker : invokers) {
      if (NetUtil.localIpv4s().contains(invoker.getUrl().getHost())) {
        return invoker;
      }
    }
    return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
  }
}
