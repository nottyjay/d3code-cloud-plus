# d3code-seata-server 分布式事务服务

## 模块概述

d3code-seata-server 是 Seata 分布式事务协调器（TC），用于管理全局事务、协调各分支事务的提交和回滚，保证分布式事务的一致性。

## 启动信息

- **模块名称**: d3code-seata-server
- **启动类**: org.apache.seata.server.SeataServerApplication
- **端口**：
  - RPC 端口：默认 7091
  - HTTP 端口：默认 7091

## 功能用途

1. **全局事务管理**：创建、提交、回滚全局事务
2. **分支事务注册**：注册、上报分支事务状态
3. **分布式锁**：全局锁管理
3. **事务协调**：协调各分支事务的两阶段提交
4. **重试机制**：事务失败自动重试
5. **事务存储**：事务信息持久化
6. **集群管理**：Seata 集群管理

## 目录结构

```
d3code-seata-server/
├── src/main/java/org/apache/seata/server/
│   └── SeataServerApplication.java      # 启动类
└── src/main/resources/
    ├── application.yml                  # 应用配置
    ├── registry.conf                    # 注册中心配置
    └── file.conf                        # 文件存储配置
```

## 依赖模块

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Seata Server | - | Seata 服务器 |
| Spring Cloud Alibaba Seata | - | Seata 集成 |

## 核心概念

### 1. 全局事务

全局事务是 Seata 管理的最高级别事务，包含多个分支事务。

- **XID**：全局事务唯一标识
- **状态**：Begin, Active, Committing, Rollbacking, Committed, Rolledback

### 2. 分支事务

分支事务是全局事务的一部分，对应单个数据库事务。

- **Branch ID**：分支事务唯一标识
- **Resource ID**：数据源标识
- **锁**：分支事务涉及的行锁

### 3. 两阶段提交

Seata 采用两阶段提交协议：

- **第一阶段**：Prepare 阶段，注册分支事务，执行本地事务
- **第二阶段**：Commit/Rollback 阶段，根据结果提交或回滚

## 事务模式

### AT 模式

默认模式，基于数据库本地事务和全局锁。

**工作流程**：
1. 解析 SQL，记录前镜像（Before Image）
2. 执行业务 SQL，记录后镜像（After Image）
3. 生成回滚日志
4. 提交本地事务
5. 上报分支事务状态
6. 全事务提交或回滚

**优点**：
- 无侵入，业务代码无需改动
- 性能好

**缺点**：
- 需要数据库支持
- 存在锁等待

### TCC 模式

Try-Confirm-Cancel 模式，业务侵入式模式。

**三个阶段**：
- **Try**：资源检查和预留
- **Confirm**：确认执行业务操作
- **Cancel**：取消业务操作，释放资源

**优点**：
- 灵活性高
- 性能好

**缺点**：
- 代码侵入性强
- 需要实现三个接口

### SAGA 模式

长事务模式，适用于长流程业务。

**优点**：
- 不依赖数据库
- 适用于长事务

**缺点**：
- 需要定义状态机
- 补偿逻辑复杂

### XA 模式

基于 XA 协议的分布式事务。

**优点**：
- 标准协议
- 强一致性

**缺点**：
- 性能差
- 资源锁定时间长

## 存储模式

Seata 支持多种事务信息存储方式：

### 1. File 存储（默认）

存储到本地文件。

```conf
store.mode = file
store.file.dir = "sessionStore"
```

**适用场景**：单机、开发环境

### 2. DB 存储

存储到数据库。

```conf
store.mode = db
store.db.datasource = druid
store.db.dbType = mysql
store.db.driverClassName = "com.mysql.jdbc.Driver"
store.db.url = "jdbc:mysql://127.0.0.1:3306/seata"
store.db.user = "root"
store.db.password = "123456"
```

**适用场景**：集群、生产环境

### 3. Redis 存储

存储到 Redis。

```conf
store.mode = redis
store.redis.host = "127.0.0.1"
store.redis.port = "6379"
store.redis.password = ""
```

## 客户端配置

接入 Seata 的服务需要添加依赖和配置：

### 依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
```

### 配置

```yaml
seata:
  enabled: true
  application-id: ${spring.application.name}
  tx-service-group: my_tx_group
  service:
    vgroup-mapping:
      my_tx_group: default
  registry:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: ""
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: ""
```

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-visual/d3code-seata-server/target/d3code-seata-server.jar

# 或指定端口运行
java -jar d3code-visual/d3code-seata-server/target/d3code-seata-server.jar --server.port=7091
```

## 注意事项

1. 生产环境必须使用 DB 存储模式
2. 全局事务超时时间根据业务场景设置
3. 避免跨服务调用链过长
4. 注意 AT 模式的全局锁超时问题
5. TCC 模式要保证幂等性
6. 定期清理已结束的全局事务

---

*模块文档生成时间: 2026-02-12*
