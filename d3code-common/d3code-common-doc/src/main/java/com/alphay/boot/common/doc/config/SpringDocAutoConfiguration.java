package com.alphay.boot.common.doc.config;

import com.alphay.boot.common.core.utils.ServletUtils;
import com.alphay.boot.common.core.utils.StringUtils;
import com.alphay.boot.common.doc.config.properties.SpringDocProperties;
import com.alphay.boot.common.doc.handler.OpenApiHandler;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 接口文档配置
 *
 * @author Nottyjay
 * @since 1.0.0
 */
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties(SpringDocProperties.class)
@ConditionalOnProperty(
    name = "springdoc.api-docs.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SpringDocAutoConfiguration {

  @Resource private ServerProperties serverProperties;

  @Value("${spring.application.name}")
  private String appName;

  @Bean
  @ConditionalOnMissingBean(OpenAPI.class)
  public OpenAPI openApi(SpringDocProperties properties) {
    OpenAPI openApi = new OpenAPI();
    // 文档基本信息
    SpringDocProperties.InfoProperties infoProperties = properties.getInfo();
    Info info = convertInfo(infoProperties);
    openApi.info(info);
    // 扩展文档信息
    openApi.externalDocs(properties.getExternalDocs());
    openApi.tags(properties.getTags());
    openApi.paths(properties.getPaths());
    openApi.components(properties.getComponents());
    Set<String> keySet = properties.getComponents().getSecuritySchemes().keySet();
    List<SecurityRequirement> list = new ArrayList<>();
    SecurityRequirement securityRequirement = new SecurityRequirement();
    keySet.forEach(securityRequirement::addList);
    list.add(securityRequirement);
    openApi.security(list);

    return openApi;
  }

  private Info convertInfo(SpringDocProperties.InfoProperties infoProperties) {
    Info info = new Info();
    info.setTitle(infoProperties.getTitle());
    info.setDescription(infoProperties.getDescription());
    info.setContact(infoProperties.getContact());
    info.setLicense(infoProperties.getLicense());
    info.setVersion(infoProperties.getVersion());
    return info;
  }

  /** 自定义 openapi 处理器 */
  @Bean
  public OpenAPIService openApiBuilder(
      Optional<OpenAPI> openAPI,
      SecurityService securityParser,
      SpringDocConfigProperties springDocConfigProperties,
      PropertyResolverUtils propertyResolverUtils,
      Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
      Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
      Optional<JavadocProvider> javadocProvider) {
    return new OpenApiHandler(
        openAPI,
        securityParser,
        springDocConfigProperties,
        propertyResolverUtils,
        openApiBuilderCustomisers,
        serverBaseUrlCustomisers,
        javadocProvider);
  }

  /** 对已经生成好的 OpenApi 进行自定义操作 */
  @Bean
  public OpenApiCustomizer openApiCustomizer() {
    // 对所有路径增加前置上下文路径
    return openApi -> {
      HttpServletRequest request = ServletUtils.getRequest();
      // 从请求头获取gateway转发的服务前缀
      String prefix = StringUtils.blankToDefault(request.getHeader("X-Forwarded-Prefix"), "");
      Paths oldPaths = openApi.getPaths();
      if (oldPaths instanceof PlusPaths) {
        return;
      }
      PlusPaths newPaths = new PlusPaths();
      oldPaths.forEach((k, v) -> newPaths.addPathItem(prefix + k, v));
      openApi.setPaths(newPaths);
    };
  }
}
