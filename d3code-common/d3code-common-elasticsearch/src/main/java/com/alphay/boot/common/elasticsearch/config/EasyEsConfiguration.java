package com.alphay.boot.common.elasticsearch.config;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * easy-es 配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
@EsMapperScan("com.d3code.boot.**.esmapper")
public class EasyEsConfiguration {}
