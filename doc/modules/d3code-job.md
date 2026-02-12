# d3code-job 定时任务模块

## 模块概述

d3code-job 是系统的定时任务模块，集成 SnailJob 分布式任务调度框架，提供可视化的任务管理和调度功能。

## 启动信息

- **模块名称**: d3code-job
- **启动类**: com.alphay.boot.job.D3codeJobApplication
- **端口**: 由 Nacos 配置文件指定

## 功能用途

1. **任务管理**：定时任务的增删改查
2. **任务调度**：基于 Cron 表达式的任务调度
3. **任务执行**：分布式任务执行
4. **任务监控**：任务执行日志与状态监控
5. **失败重试**：任务失败自动重试
6. **任务依赖**：任务之间的依赖关系
7. **任务分组**：任务分组管理
8. **动态调度**：支持动态修改调度规则

## 目录结构

```
d3code-job/
├── src/main/java/com/alphay/boot/job/
│   ├── D3codeJobApplication.java      # 启动类
│   ├── controller/                     # 控制器层
│   │   ├── JobController.java        # 任务控制器
│   │   └── JobLogController.java     # 任务日志控制器
│   ├── domain/                        # 实体类对象
│   │   ├── SysJob.java              # 任务实体
│   │   ├── SysJobLog.java            # 任务日志实体
│   │   └── ...
│   ├── service/                       # 业务逻辑层
│   │   ├── ISysJobService.java      # 任务服务
│   │   ├── ISysJobLogService.java   # 任务日志服务
│   │   └── impl/                    # 服务实现
│   ├── task/                          # 任务执行类
│   │   ├── CleanTask.java           # 清理任务
│   │   ├── BackupTask.java          # 备份任务
│   │   └── ...
│   └── handler/                       # 任务处理器
└── src/main/resources/
    ├── application.yml                # 应用配置
    ├── bootstrap.yml                 # 引导配置
    └── mapper/                        # MyBatis Mapper XML
        ├── SysJobMapper.xml
        └── SysJobLogMapper.xml
```

## 依赖模块

### 内部依赖

| 依赖模块 | 用途 |
|---------|------|
| d3code-common-nacos | Nacos 服务注册与配置中心 |
| d3code-common-log | 日志组件 |
| d3code-common-web | Web 相关组件 |
| d3code-common-dubbo | Dubbo RPC 支持 |
| d3code-common-mybatis | MyBatis 数据访问 |
| d3code-common-job | SnailJob 任务调度 |
| d3code-common-tenant | 多租户支持（排除 mybatis） |
| d3code-common-security | 安全认证 |
| d3code-api-system | 系统 API 接口定义 |

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| SnailJob Client | 1.5.0 | 任务调度客户端 |

## 核心功能

### 任务类型

1. **Bean 任务**：调用 Spring Bean 的方法
2. **脚本任务**：执行 Groovy、Python 等脚本
3. **HTTP 任务**：调用 HTTP 接口

### 调度策略

- **固定间隔**：固定时间间隔执行
- **Cron 表达式**：基于 Cron 表达式调度
- **一次性**：只执行一次

### 执行策略

- **串行执行**：任务按顺序执行
- **并行执行**：任务并发执行
- **故障转移**：节点故障时自动转移

## 任务配置

### Cron 表达式示例

| Cron 表达式 | 说明 |
|------------|------|
| 0 0 0 * * ? | 每天凌晨执行 |
| 0 0 12 * * ? | 每天中午 12 点执行 |
| 0 */5 * * * ? | 每 5 分钟执行一次 |
| 0 0 2 * * ? | 每天凌晨 2 点执行 |

### 任务参数

- **任务名称**：任务的唯一标识
- **任务分组**：任务所属分组
- **调用目标**：Bean 名称和方法
- **Cron 表达式**：调度时间表达式
- **执行策略**：并行/串行执行
- **超时时间**：任务执行超时时间
- **重试次数**：失败重试次数
- **重试间隔**：重试间隔时间

## 数据库表

| 表名 | 说明 |
|------|------|
| sys_job | 定时任务表 |
| sys_job_log | 定时任务日志表 |

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-modules/d3code-job/target/d3code-job.jar
```

## 使用示例

### 定义任务类

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

在任务管理页面配置：
- 任务名称：demoTask
- 调用目标：demoTask.execute
- Cron 表达式：0 */5 * * * ?
- 执行策略：并行执行

## 注意事项

1. 定时任务执行时间不要过长
2. 避免任务执行重叠（加分布式锁）
3. 长时间任务建议使用异步处理
4. 任务参数大小控制在合理范围
5. 定期清理任务日志
6. 生产环境任务需要做好监控告警

---

*模块文档生成时间: 2026-02-12*
