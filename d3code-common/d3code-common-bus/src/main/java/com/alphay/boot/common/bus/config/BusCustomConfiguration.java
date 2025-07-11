package com.alphay.boot.common.bus.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;

/**
 * bus 配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@RemoteApplicationEventScan(basePackages = "${spring.cloud.bus.base-packages}")
public class BusCustomConfiguration {}
