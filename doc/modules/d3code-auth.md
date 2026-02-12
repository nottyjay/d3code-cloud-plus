# d3code-auth 认证授权中心

## 模块概述

d3code-auth 是系统的认证授权中心，负责处理用户登录、权限验证、令牌管理等安全相关功能。

## 启动信息

- **模块名称**: d3code-auth
- **启动类**: com.alphay.boot.auth.D3codeAuthApplication
- **端口**: 由 Nacos 配置文件指定

## 功能用途

1. **用户认证**：处理用户登录、登出操作
2. **令牌管理**：JWT 令牌的生成与验证
3. **权限控制**：基于角色的权限验证（RBAC）
4. **验证码服务**：图形验证码生成与校验
5. **社交登录**：集成第三方登录（如 GitHub、微信等）
6. **多租户支持**：支持多租户场景下的认证
7. **限流保护**：防止暴力破解和恶意请求

## 目录结构

```
d3code-auth/
├── src/main/java/com/alphay/boot/auth/
│   ├── D3codeAuthApplication.java    # 启动类
│   ├── controller/                    # 控制器层
│   ├── domain/                        # 实体类对象
│   ├── mapper/                        # 数据访问层
│   ├── service/                       # 业务逻辑层
│   └── listener/                      # 事件监听器
└── src/main/resources/
    ├── application.yml                # 应用配置
    ├── bootstrap.yml                 # 引导配置
    └── logback.xml                   # 日志配置
```

## 依赖模块

### 内部依赖

| 依赖模块 | 用途 |
|---------|------|
| d3code-common-nacos | Nacos 服务注册与配置中心 |
| d3code-common-sentinel | Sentinel 限流熔断 |
| d3code-common-security | 安全认证基础组件 |
| d3code-common-social | 社交登录支持 |
| d3code-common-log | 日志组件 |
| d3code-common-doc | API 文档 |
| d3code-common-web | Web 相关组件 |
| d3code-common-ratelimiter | 限流组件 |
| d3code-common-encrypt | 加密解密 |
| d3code-common-dubbo | Dubbo RPC 支持 |
| d3code-common-seata | 分布式事务 |
| d3code-common-tenant | 多租户支持 |
| d3code-common-service-impl | 服务实现基础 |
| d3code-api-resource | 资源 API 接口 |

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Hutool Captcha | - | 验证码生成 |

## 核心配置

### 认证配置

- **认证方式**: Sa-Token + JWT
- **Token 有效期**: 由配置文件指定
- **支持多端登录**: 支持同一账号多设备登录

### 社交登录

- **支持平台**: GitHub、微信、QQ 等（由 JustAuth 支持）
- **配置来源**: Nacos 配置中心

## 核心功能模块

1. **登录模块**
   - 用户名密码登录
   - 验证码校验
   - 社交登录回调处理

2. **权限模块**
   - 基于角色的权限验证
   - 动态权限加载

3. **令牌模块**
   - 令牌生成
   - 令牌刷新
   - 令牌验证

4. **租户模块**
   - 租户隔离
   - 租户配置管理

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-auth/target/d3code-auth.jar

# 或指定端口运行
java -jar d3code-auth/target/d3code-auth.jar --server.port=9200
```

## Nacos 配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | - |
| spring.application.name | 服务名称 | d3code-auth |
| spring.cloud.nacos.discovery.server-addr | Nacos 服务地址 | - |
| sa-token.jwt-secret-key | JWT 密钥 | - |

## 注意事项

1. 首次运行需要配置 Nacos 服务地址
2. 需要依赖 Redis 存储会话信息
3. 社交登录需要申请对应的第三方应用 ID 和密钥

---

*模块文档生成时间: 2026-02-12*
