package com.alphay.boot.common.encrypt.config;

import com.alphay.boot.common.encrypt.core.EncryptorManager;
import com.alphay.boot.common.encrypt.interceptor.MybatisDecryptInterceptor;
import com.alphay.boot.common.encrypt.interceptor.MybatisEncryptInterceptor;
import com.alphay.boot.common.encrypt.properties.EncryptorProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 加解密配置
 *
 * @author Nottyjay
 * @since 1.0.0
 * @version 4.6.0
 */
@AutoConfiguration(after = MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties({EncryptorProperties.class, MybatisPlusProperties.class})
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@ConditionalOnProperty(value = "mybatis-encryptor.enable", havingValue = "true")
public class EncryptorAutoConfiguration {

  @Autowired private EncryptorProperties properties;

  @Bean
  public EncryptorManager encryptorManager(MybatisPlusProperties mybatisPlusProperties) {
    return new EncryptorManager(mybatisPlusProperties.getTypeAliasesPackage());
  }

  @Bean
  public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
    return new MybatisEncryptInterceptor(encryptorManager, properties);
  }

  @Bean
  public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
    return new MybatisDecryptInterceptor(encryptorManager, properties);
  }
}
