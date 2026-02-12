# d3code-gateway 网关模块

## 模块概述

d3code-gateway 是系统的 API 网关，作为微服务架构的统一入口，负责路由转发、负载均衡、限流熔断、认证鉴权等功能。

## 启动信息

- **模块名称**: d3code-gateway
- **启动类**: com.alphay.boot.gateway.D3codeGatewayApplication
- **端口**: 由 Nacos 配置文件指定（通常为 8080 或其他指定端口）

## 功能用途

1. **路由转发**：将请求路由到对应的后端服务
2. **负载均衡**：基于 Ribbon/LoadBalancer 的负载均衡
3. **限流熔断**：集成 Sentinel 实现限流和熔断保护
4. **认证鉴权**：统一处理认证和权限验证
5. **请求过滤**：请求日志记录、参数校验、响应修改
6. **跨域处理**：CORS 跨域配置
7. **灰度发布**：支持多版本服务的灰度路由

## 目录结构

```
d3code-gateway/
├── src/main/java/com/alphay/boot/gateway/
│   ├── D3codeGatewayApplication.java   # 启动类
│   ├── filter/                         # Gateway 过滤器
│   │   ├── AuthFilter.java            # 认证过滤器
│   │   ├── LogFilter.java             # 日志过滤器
│   │   └── RateLimiterFilter.java     # 限流过滤器
│   ├── config/                         # 配置类
│   │   └── GatewayConfig.java         # 网关配置
│   └── handler/                        # 异常处理器
└── src/main/resources/
    ├── application.yml                 # 应用配置
    ├── bootstrap.yml                  # 引导配置
    └── logback.xml                    # 日志配置
```

## 依赖模块

### 内部依赖

| 依赖模块 | 用途 |
|---------|------|
| d3code-common-nacos | Nacos 服务注册与配置中心 |
| d3code-common-sentinel | Sentinel 限流熔断 |
| d3code-common-satoken | Sa-Token 权限认证 |
| d3code-common-redis | Redis 缓存支持 |
| d3code-common-tenant | 多租户支持 |

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Cloud Gateway | - | 网关核心框架 |
| Spring Cloud LoadBalancer | - | 负载均衡 |
| Caffeine | - | 本地缓存 |
| Spring Boot Actuator | - | 应用监控端点 |
| Hutool HTTP | - | HTTP 工具类 |
| Sa-Token Reactor | 1.44.0 | 响应式认证集成 |
| Spring Cloud Alibaba Sentinel Gateway | - | Sentinel 网关适配 |

## 核心配置

### 路由配置

路由规则通过 Nacos 配置中心动态管理，支持：

- **路径匹配**：基于 URL 路径的路由
- **服务名路由**：基于服务名的路由
- **权重路由**：支持灰度发布
- **断言路由**：基于 Header、Query 等条件的路由

### 限流配置

- **限流维度**：IP、用户、接口等
- **限流算法**：令牌桶、漏桶等
- **降级策略**：快速失败、返回默认值

### 认证配置

- **认证方式**：Sa-Token
- **白名单路径**：登录、注册等不需要认证的路径
- **Token 传递**：Header 中传递 Authorization

## 核心过滤器

1. **AuthFilter**
   - 拦截所有请求
   - 验证 Token 有效性
   - 提取用户信息并透传到下游服务

2. **LogFilter**
   - 记录请求日志
   - 记录响应日志
   - 统计请求耗时

3. **RateLimiterFilter**
   - 基于 Sentinel 的限流
   - 自定义限流规则

## 路由示例

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: d3code-system
          uri: lb://d3code-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
```

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-gateway/target/d3code-gateway.jar

# 或指定端口运行
java -jar d3code-gateway/target/d3code-gateway.jar --server.port=8080
```

## 监控端点

| 端点 | 说明 |
|------|------|
| /actuator/gateway/routes | 查看所有路由 |
| /actuator/gateway/actuator/refresh | 刷新路由配置 |
| /actuator/health | 健康检查 |

## 注意事项

1. 网关作为系统入口，需要重点保障高可用
2. 路由配置变更后需要刷新网关配置
3. 限流规则需要根据实际业务场景合理配置
4. 认证过滤器需要放在最前面的过滤器链中

---

*模块文档生成时间: 2026-02-12*
