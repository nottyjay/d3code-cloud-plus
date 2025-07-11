package com.alphay.boot.common.core.factory;

import com.alphay.boot.common.core.utils.StringUtils;
import java.io.IOException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

/**
 * yml 配置源工厂
 *
 * @author Nottyjay
 * @since 1.0.0
 */
public class YmlPropertySourceFactory extends DefaultPropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    String sourceName = resource.getResource().getFilename();
    if (StringUtils.isNotBlank(sourceName)
        && StringUtils.endsWithAny(sourceName, ".yml", ".yaml")) {
      YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
      factory.setResources(resource.getResource());
      factory.afterPropertiesSet();
      return new PropertiesPropertySource(sourceName, factory.getObject());
    }
    return super.createPropertySource(name, resource);
  }
}
