# d3code-monitor 监控服务

## 模块概述

d3code-monitor 是 Spring Boot Admin 监控服务，用于监控和管理所有微服务实例的健康状态、指标信息、日志等。

## 启动信息

- **模块名称**: d3code-monitor
- **启动类**: com.alphay.boot.modules.monitor.D3codeMonitorApplication
- **端口**: 由 Nacos 配置文件指定（通常为 9100 或其他指定端口）

## 功能用途

1. **应用监控**：监控所有注册到 Nacos 的 Spring Boot 应用
2. **健康检查**：查看应用健康状态
3. **指标查看**：JVM、HTTP、数据库等指标
4. **日志查看**：实时查看应用日志
5. **环境信息**：查看应用环境配置
6. **线程监控**：查看线程池状态
7. **堆栈跟踪**：线程堆栈跟踪
8. **JMX 操作**：执行 JMX 操作
9. **HTTP 追踪**：HTTP 请求追踪
10. **应用管理**：重启、关闭应用

## 目录结构

```
d3code-monitor/
├── src/main/java/com/alphay/boot/modules/monitor/
│   ├── D3codeMonitorApplication.java    # 启动类
│   └── config/                           # 配置类
│       └── SecurityConfig.java         # 安全配置
└── src/main/resources/
    ├── application.yml                  # 应用配置
    ├── bootstrap.yml                   # 引导配置
    └── logback.xml                     # 日志配置
```

## 依赖模块

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Boot Admin Server | 3.4.7 | Admin 服务端 |
| Spring Boot Admin Client | 3.4.7 | Admin 客户端 |
| Spring Boot Starter Actuator | - | 监控端点 |

## 监控端点

监控服务暴露的端点：

| 端点 | 说明 |
|------|------|
| /actuator/health | 健康检查 |
| /actuator/info | 应用信息 |
| /actuator/metrics | 指标信息 |
| /actuator/env | 环境变量 |
| /actuator/loggers | 日志配置 |
| /actuator/heapdump | 堆栈转储 |
| /actuator/threaddump | 线程转储 |
| /actuator/httptrace | HTTP 追踪 |

## 监控功能详情

### 1. 健康检查

查看各应用的健康状态：
- UP：应用正常运行
- DOWN：应用异常
- OUT_OF_SERVICE：应用停止服务

### 2. JVM 监控

- **内存监控**：堆内存、非堆内存使用情况
- **线程监控**：线程数、线程池状态
- **类加载**：已加载类数量
- **垃圾回收**：GC 次数、GC 时间

### 3. HTTP 监控

- **请求数**：HTTP 请求总数
- **响应时间**：平均响应时间、最大响应时间
- **错误率**：错误请求占比

### 4. 数据库监控

- **连接池**：活跃连接数、空闲连接数
- **查询统计**：慢 SQL 统计

### 5. 日志查看

- 实时查看应用日志
- 按级别过滤日志
- 按关键字搜索日志

## 安全配置

监控服务支持登录鉴权：

- **默认用户**：admin
- **默认密码**：由配置文件指定
- **权限控制**：不同用户不同权限

## 被监控应用配置

被监控的应用需要添加以下依赖和配置：

### 依赖

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 配置

```yaml
spring:
  boot:
    admin:
      client:
        url: http://d3code-monitor:9100
        username: admin
        password: admin
        instance:
          prefer-ip: true
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-visual/d3code-monitor/target/d3code-monitor.jar
```

## 访问地址

启动后通过浏览器访问：

```
http://localhost:9100
```

## 注意事项

1. 监控服务端口需要被外部访问
2. 生产环境建议修改默认密码
3. 堆栈转储功能会消耗较多内存，谨慎使用
4. 日志查看功能需要日志文件可读
5. 建议定期清理历史监控数据

---

*模块文档生成时间: 2026-02-12*
