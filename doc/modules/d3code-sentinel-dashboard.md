# d3code-sentinel-dashboard Sentinel 控制台

## 模块概述

d3code-sentinel-dashboard 是 Alibaba Sentinel 的控制台，用于配置限流、熔断、降级规则，并实时监控各服务的流量和熔断情况。

## 启动信息

- **模块名称**: d3code-sentinel-dashboard
- **启动类**: com.alibaba.csp.sentinel.dashboard.DashboardApplication
- **端口**: 默认 8080（可在 Nacos 配置中修改）

## 功能用途

1. **实时监控**：实时查看服务的 QPS、拒绝数、响应时间
2. **规则配置**：配置限流、熔断、降级规则
3. **集群管理**：管理集群节点
4. **机器列表**：查看接入 Sentinel 的机器列表
5. **流控规则**：配置流量控制规则
6. **熔断规则**：配置熔断降级规则
7. **热点规则**：配置热点参数限流规则
8. **系统规则**：配置系统保护规则
9. **授权规则**：配置黑白名单规则

## 目录结构

```
d3code-sentinel-dashboard/
├── src/main/java/com/alibaba/csp/sentinel/dashboard/
│   ├── DashboardApplication.java         # 启动类

└── src/main/resources/
    ├── application.yml                  # 应用配置
    └── logback.xml                      # 日志配置
```

## 依赖模块

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Sentinel Dashboard | - | Sentinel 控制台 |
| Spring Cloud Alibaba Sentinel | - | Sentinel 集成 |

## 核心功能

### 1. 实时监控

提供实时监控视图：

- **实时监控**：通过曲线图展示 QPS、拒绝数等指标
- **机器监控**：查看单台机器的监控数据
- **簇点监控**：查看接口级别的监控数据

监控指标包括：

| 指标 | 说明 |
|------|------|
| passQps | 通过 QPS |
| blockQps | 拒绝 QPS |
| totalQps | 总 QPS |
| avgRt | 平均响应时间 |
| thread | 当前线程数 |

### 2. 流控规则

流量控制规则类型：

| 模式 | 说明 |
|------|------|
| QPS | 每秒请求数限制 |
| 线程数 | 并发线程数限制 |

流控效果：

| 效果 | 说明 |
|------|------|
| 快速失败 | 直接拒绝请求 |
| Warm Up | 预热模式 |
| 排队等待 | 匀速排队 |

### 3. 熔断降级规则

熔断策略：

| 策略 | 说明 |
|------|------|
| 慢调用比例 | 慢调用比例达到阈值熔断 |
| 异常比例 | 异常比例达到阈值熔断 |
| 异常数 | 异常数达到阈值熔断 |

熔断状态：

| 状态 | 说明 |
|------|------|
| 关闭 | 正常状态 |
| 开启 | 熔断状态 |
| 半开 | 尝试恢复状态 |

### 4. 热点规则

针对热点参数的限流规则：

- 支持按参数值限流
- 支持参数索引
- 支持统计窗口时长

### 5. 系统规则

系统保护规则：

| 规则 | 说明 |
|------|------|
| RT | 平均响应时间 |
| 线程数 | 并发线程数 |
| QPS | 入口 QPS |
| CPU 使用率 | CPU 占用率 |

### 6. 授权规则

黑白名单控制：

- **白名单**：只允许白名单中的来源访问
- **黑名单**：拒绝黑名单中的来源访问

## 客户端配置

接入 Sentinel 的服务需要添加依赖和配置：

### 依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

### 配置

```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080
      eager: true
      datasource:
        # Nacos 数据源配置
        flow:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}-flow-rules
            groupId: SENTINEL_GROUP
            rule-type: flow
```

## 规则持久化

支持将规则持久化到 Nacos、Apollo 等：

### Nacos 持久化

- **规则类型**：flow、degrade、authority、system、param-flow
- **数据格式**：JSON 数组
- **自动刷新**：Nacos 规则变更自动生效

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-visual/d3code-sentinel-dashboard/target/d3code-sentinel-dashboard.jar

# 或指定端口运行
java -jar d3code-visual/d3code-sentinel-dashboard/target/d3code-sentinel-dashboard.jar --server.port=8858
```

## 访问地址

启动后通过浏览器访问：

```
http://localhost:8080
```

默认账号密码：
- 用户名：sentinel
- 密码：sentinel

## 注意事项

1. Sentinel 控制台是轻量级应用，不适合高并发场景
2. 生产环境建议配置规则持久化
3. 规则配置需要充分测试后再上线
4. 监控数据有一定延迟（秒级）
5. 控制台重启后规则会丢失（除非持久化）

---

*模块文档生成时间: 2026-02-12*
