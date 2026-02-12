# d3code-snailjob-server 任务调度服务

## 模块概述

d3code-snailjob-server 是 SnailJob 分布式任务调度服务器，提供可视化的任务管理、调度、监控功能，支持多种任务类型和调度策略。

## 启动信息

- **模块名称**: d3code-snailjob-server
- **启动类**: com.alphay.boot.snailjob.SnailJobServerApplication
- **端口**：默认由 Nacos 配置文件指定

## 功能用途

1. **任务管理**：可视化任务增删改查
2. **任务调度**：分布式任务调度
3. **任务执行**：分布式任务执行
4. **任务监控**：任务执行日志与状态监控
5. **失败重试**：任务失败自动重试
6. **任务依赖**：任务之间依赖关系管理
7. **任务分组**：任务分组管理
8. **集群管理**：多节点集群管理
9. **报警通知**：任务失败报警
10. **日志管理**：任务执行日志查询

## 目录结构

```
d3code-snailjob-server/
├── src/main/java/com/alphay/boot/snailjob/
│   └── SnailJobServerApplication.java    # 启动类
└── src/main/resources/
    ├── application.yml                    # 应用配置
    ├── bootstrap.yml                     # 引导配置
    └── logback.xml                        # 日志配置
```

## 依赖模块

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| SnailJob Server | 1.5.0 | 任务调度服务器 |
| Spring Cloud Alibaba Nacos | - | 服务注册发现 |

## 核心功能

### 1. 任务类型

支持的任务类型：

| 类型 | 说明 |
|------|------|
| Bean 任务 | 调用 Spring Bean 方法 |
| Groovy 脚本 | 执行 Groovy 脚本 |
| Python 脚本 | 执行 Python 脚本 |
| Shell 脚本 | 执行 Shell 脚本 |
| HTTP 任务 | 调用 HTTP 接口 |
| SQL 任务 | 执行 SQL 语句 |

### 2. 调度策略

| 策略 | 说明 |
|------|------|
| 固定间隔 | 固定时间间隔执行 |
| Cron 表达式 | 基于 Cron 表达式调度 |
| 一次性 | 只执行一次 |
| 手动触发 | 手动触发执行 |

### 3. 执行策略

- **串行执行**：任务按顺序执行
- **并行执行**：任务并发执行
- **故障转移**：节点故障时自动转移

### 4. 路由策略

任务路由到哪个节点执行：

| 策略 | 说明 |
|------|------|
| 第一个 | 固定路由到第一个节点 |
| 最后一个 | 固定路由到最后一个节点 |
| 轮询 | 轮询路由到节点 |
| 随机 | 随机路由到节点 |
| 一致性哈希 | 基于参数一致性哈希 |
| 不执行 | 不执行任务 |
| 故障转移 | 故障时转移到其他节点 |

### 5. 阻塞策略

任务重复触发时的处理策略：

| 策略 | 说明 |
|------|------|
| 单机串行 | 同节点串行执行 |
| 单机并行 | 同节点并行执行 |
| 丢弃 | 丢弃新任务 |
| 覆盖 | 覆盖旧任务 |

## 任务配置

### 基础配置

| 配置项 | 说明 |
|--------|------|
| 任务名称 | 任务唯一标识 |
| 任务分组 | 任务所属分组 |
| 任务描述 | 任务描述信息 |
| 调用目标 | Bean 名称和方法或脚本内容 |
| Cron 表达式 | 调度时间表达式 |

### 高级配置

| 配置项 | 说明 |
|--------|------|
| 执行策略 | 并行/串行执行 |
| 路由策略 | 任务路由策略 |
| 阻塞策略 | 阻塞处理策略 |
| 超时时间 | 任务执行超时时间 |
| 重试次数 | 失败重试次数 |
| 重试间隔 | 重试间隔时间 |
| 任务参数 | 任务执行参数 |

## 客户端配置

接入 SnailJob 的服务需要添加依赖和配置：

### 依赖

```xml
<dependency>
    <groupId>com.aizuda</groupId>
    <artifactId>snail-job-client-starter</artifactId>
    <version>1.5.0</version>
</dependency>
<dependency>
    <groupId>com.aizuda</groupId>
    <artifactId>snail-job-client-job-core</artifactId>
    <version>1.5.0</version>
</dependency>
```

### 配置

```yaml
snail-job:
  # 调度中心地址
  server:
    address: http://localhost:9100
  # 应用名称
  application-name: ${spring.application.name}
  # 客户端鉴权 token
  token: xxx
  # 日志级别
  log-level: debug
```

## 使用示例

### 定义任务

```java
@Component("demoTask")
public class DemoTask {

    public void execute(String params) {
        // 任务执行逻辑
        System.out.println("执行任务，参数：" + params);
    }
}
```

### 配置任务

在 SnailJob 控制台配置：
- 任务名称：demoTask
- 任务分组：default
- 调用目标：demoTask.execute
- Cron 表达式：0 */5 * * * ?
- 执行策略：并行执行

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-visual/d3code-snailjob-server/target/d3code-snailjob-server.jar
```

## 访问地址

启动后通过浏览器访问：

```
http://localhost:9100
```

默认账号密码：
- 用户名：snailjob
- 密码：snailjob

## 注意事项

1. 任务执行时间不要过长
2. 长时间任务建议使用异步处理
3. 任务参数大小控制在合理范围
4. 定期清理任务日志
5. 生产环境任务需要做好监控告警
6. 集群部署时注意路由策略配置
7. 任务依赖关系不要形成循环依赖

---

*模块文档生成时间: 2026-02-12*
