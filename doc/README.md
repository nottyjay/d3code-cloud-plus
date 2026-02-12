# D3code-Cloud-Plus 项目文档

## 项目概览

D3code-Cloud-Plus 是基于 Spring Cloud 的微服务系统，由安惠科技开发。

## 项目基本信息

| 属性 | 值 |
|------|-----|
| 项目名称 | D3code-Cloud-Plus |
| 组织ID | com.alphay.boot |
| 项目版本 | 1.0.0 |
| 构建工具 | Maven |
| Java 版本 | 17 |
| 编码 | UTF-8 |

## 核心技术栈

### 框架与微服务

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.4.7 | 基础应用框架 |
| Spring Cloud | 2024.0.0 | 微服务框架 |
| Spring Cloud Alibaba | - | 阿里云微服务组件 |
| Spring Boot Admin | 3.4.7 | 应用监控 |

### 数据访问

| 技术 | 版本 | 用途 |
|------|------|------|
| MyBatis | 3.5.16 | ORM 框架 |
| MyBatis Plus | 3.5.12 | MyBatis 增强工具 |
| MyBatis Plus Join | 1.5.4 | 关联查询插件 |
| P6Spy | 3.9.1 | SQL 性能分析 |
| Dynamic Datasource | 4.3.1 | 动态数据源 |

### 文档与工具

| 技术 | 版本 | 用途 |
|------|------|------|
| Swagger Core | 2.2.30 | API 文档 |
| SpringDoc OpenAPI | 2.8.8 | OpenAPI 支持 |
| Velocity | 2.3 | 模板引擎（代码生成） |
| Lombok | 1.18.36 | 简化 Java 代码 |
| Hutool | 5.8.38 | Java 工具类库 |

### 缓存与分布式

| 技术 | 版本 | 用途 |
|------|------|------|
| Redisson | 3.50.0 | Redis 客户端 |
| Lock4j | 2.2.7 | 分布式锁 |

### 认证与授权

| 技术 | 版本 | 用途 |
|------|------|------|
| Sa-Token | 1.44.0 | 权限认证框架 |
| JustAuth | 1.16.7 | 第三方登录 |

### 任务调度

| 技术 | 版本 | 用途 |
|------|------|------|
| SnailJob | 1.5.0 | 分布式任务调度 |

### 消息队列

| 技术 | 版本 | 用途 |
|------|------|------|
| RocketMQ | 2.3.0 | 消息队列 |

### 工作流

| 技术 | 版本 | 用途 |
|------|------|------|
| Warm-Flow | 1.7.4 | 国产工作流引擎 |

### 监控与日志

| 技术 | 版本 | 用途 |
|------|------|------|
| Logstash Logback | 7.4 | 日志收集 |
| Easy-ES | 3.0.0 | Elasticsearch 封装 |
| SkyWalking Toolkit | 9.3.0 | APM 监控 |

### 其他

| 技术 | 版本 | 用途 |
|------|------|------|
| BouncyCastle | 1.80 | 加密解密 |
| MapStruct Plus | 1.4.8 | 对象映射 |
| IP2Region | 2.7.0 | IP 地址定位 |
| FastJSON | 1.2.83 | JSON 处理 |
| AWS SDK | 2.28.22 | 对象存储（OSS） |
| SMS4J | 3.3.4 | 短信发送 |
| FastExcel | 1.2.0 | Excel 处理 |
| Anyline | 8.7.2-20250603 | 数据库 ORM |

## 模块架构

```
d3code-cloud-plus
├── d3code-auth              # 认证授权中心
├── d3code-gateway           # API 网关
├── d3code-visual            # 图形化管理模块
│   ├── d3code-monitor      # 监控服务
│   ├── d3code-sentinel-dashboard  # Sentinel 控制台
│   ├── d3code-seata-server # 分布式事务服务
│   └── d3code-snailjob-server    # 任务调度服务
├── d3code-modules            # 业务模块
│   ├── d3code-system       # 系统管理模块
│   ├── d3code-gen          # 代码生成模块
│   ├── d3code-job          # 定时任务模块
│   └── d3code-resource     # 资源服务模块
├── d3code-api                # API 接口定义
│   ├── d3code-api-system   # 系统 API
│   ├── d3code-api-resource # 资源 API
│   └── d3code-api-bom      # API 依赖管理
└── d3code-common             # 公共模块（27个子模块）
    ├── d3code-common-bom       # 公共依赖管理
    ├── d3code-common-alibaba-bom  # 阿里云依赖管理
    ├── d3code-common-core       # 核心工具类
    ├── d3code-common-web        # Web 相关
    ├── d3code-common-mybatis    # MyBatis 配置
    ├── d3code-common-redis      # Redis 配置
    ├── d3code-common-security   # 安全相关
    ├── d3code-common-satoken    # Sa-Token 配置
    ├── d3code-common-nacos     # Nacos 配置
    ├── d3code-common-dubbo      # Dubbo 配置
    ├── d3code-common-seata      # Seata 配置
    ├── d313code-common-job       # 任务调度
    ├── d3code-common-log        # 日志配置
    ├── d3code-common-logstash   # Logstash 日志收集
    ├── d3code-common-skylog     # SkyWalking 日志
    ├── d3code-common-prometheus # Prometheus 监控
    ├── d3code-common-oss        # 对象存储
    ├── d3code-common-excel      # Excel 处理
    ├── d3code-common-mail       # 邮件发送
    ├── d3code-common-sms        # 短信发送
    ├── d3code-common-ratelimiter # 限流
    ├── d3code-common-idempotent  # 幂等
    ├── d3code-common-sentinel   # Sentinel 限流熔断
    ├── d3code-common-elasticsearch  # ES 配置
    ├── d3code-common-translation # 翻译
    ├── d3code-common-sensitive   # 敏感词过滤
    ├── d3code-common-json        # JSON 处理
    ├── d3code-common-encrypt     # 加密解密
    ├── d3code-common-tenant      # 多租户
    ├── d3code-common-loadbalancer  # 负载均衡
    ├── d3code-common-websocket   # WebSocket
    ├── d3code-common-social      # 社交登录
    ├── d3code-common-bus         # 消息总线
    ├── d3code-common-sse         # SSE 服务推送
    └── d3code-common-service-impl  # 服务实现基础
```

## SpringBoot 启动器模块

以下模块包含 SpringBoot 应用启动类：

| 模块 | 启动类 | 文档 |
|------|--------|------|
| d3code-auth | D3codeAuthApplication | [查看详情](modules/d3code-auth.md) |
| d3code-gateway | D3codeGatewayApplication | [查看详情](modules/d3code-gateway.md) |
| d3code-system | D3codeSystemApplication | [查看详情](modules/d3code-system.md) |
| d3code-gen | D3codeGenApplication | [查看详情](modules/d3code-gen.md) |
| d3code-job | D3codeJobApplication | [查看详情](modules/d3code-job.md) |
| d3code-resource | D3codeResourceApplication | [查看详情](modules/d3code-resource.md) |
| d3code-visual/d3code-monitor | D3codeMonitorApplication | [查看详情](modules/d3code-monitor.md) |
| d3code-visual/d3code-sentinel-dashboard | DashboardApplication | [查看详情](modules/d3code-sentinel-dashboard.md) |
| d3code-visual/d3code-seata-server | SeataServerApplication | [查看详情](modules/d3code-seata-server.md) |
| d3code-visual/d3code-snailjob-server | SnailJobServerApplication | [查看详情](modules/d3code-snailjob-server.md) |

## 环境配置

项目支持多环境配置：

| 环境 | Profile | 说明 |
|------|---------|------|
| 开发环境 | dev | 默认环境 |
| 生产环境 | prod | 生产环境 |

### Nacos 配置

- 服务地址: 192.168.1.52:8848
- 命名空间: DEFAULT_GROUP
- 用户名: nacos
- 密码: nacos

### Logstash 配置

- 日志收集地址: 127.0.0.1:4560

## 构建与运行

### 构建

```bash
# 开发环境构建
mvn clean package -Pdev

# 生产环境构建
mvn clean package -Pprod
```

### 运行

各模块的 jar 包位于各模块的 target 目录，可通过 `java -jar` 命令运行。

---

*文档生成时间: 2026-02-12*
