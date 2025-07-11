package com.alphay.boot.common.loadbalance.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * 自定义负载均衡自动配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@LoadBalancerClients(defaultConfiguration = CustomLoadBalanceClientConfiguration.class)
public class CustomLoadBalanceAutoConfiguration {}
