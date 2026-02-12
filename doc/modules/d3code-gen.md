# d3code-gen 代码生成模块

## 模块概述

d3code-gen 是代码生成模块，支持通过可视化界面生成代码，提高开发效率。支持多数据库、自定义模板、一键生成前后端代码。

## 启动信息

- **模块名称**: d3code-gen
- **启动类**: com.alphay.boot.gen.D3codeGenApplication
- **端口**: 由 Nacos 配置文件指定

## 功能用途

1. **代码生成**：基于数据库表结构生成代码
2. **多数据库支持**：支持 MySQL、PostgreSQL、Oracle 等多种数据库
3. **模板定制**：支持自定义 Velocity 模板
4. **表管理**：数据库表导入与管理
5. **字段配置**：字段类型映射、显示配置
6. **代码预览**：生成前预览代码
7. **一键下载**：打包下载生成的代码
8. **代码生成历史**：记录生成历史

## 目录结构

```
d3code-gen/
├── src/main/java/com/alphay/boot/gen/
│   ├── D3codeGenApplication.java      # 启动类
│   ├── controller/                     # 控制器层
│   │   ├── GenController.java       # 代码生成控制器
│   │   └── GenTableController.java   # 表管理控制器
│   ├── domain/                        # 实体类对象
│   │   ├── GenTable.java            # 表信息实体
│   │   ├── GenTableColumn.java      # 表字段实体
│   │   └── ...
│   ├── service/                       # 业务逻辑层
│   │   ├── IGenTableService.java   # 表管理服务
│   │   ├── IGenCodeService.java    # 代码生成服务
│   │   └── impl/                   # 服务实现
│   │       ├── GenTableServiceImpl.java
│   │       └── GenCodeServiceImpl.java
│   ├── generator/                     # 代码生成器
│   │   ├── AbstractCodeGenerator.java
│   │   ├── JavaCodeGenerator.java
│   │   └── VueCodeGenerator.java
│   └── utils/                         # 工具类
│       ├── GenUtils.java            # 生成工具
│       └── VelocityUtils.java       # Velocity 工具
└── src/main/resources/
    ├── application.yml                # 应用配置
    ├── bootstrap.yml                 # 引导配置
    └── templates/                    # 代码模板
        ├── java/                     # Java 模板
        │   ├── entity.vm            # 实体类模板
        │   ├── mapper.vm            # Mapper 模板
        │   ├── service.vm           # Service 模板
        │   ├── controller.vm        # Controller 模板
        │   └── ...
        └── vue/                     # Vue 模板
            ├── index.vm             # 页面模板
            └── api.vm               # API 模板
```

## 依赖模块

### 内部依赖

| 依赖模块 | 用途 |
|---------|------|
| d3code-common-nacos | Nacos 服务注册与配置中心 |
| d3code-common-log | 日志组件 |
| d3code-common-doc | API 文档 |
| d3code-common-web | Web 相关组件 |
| d3code-common-mybatis | MyBatis 数据访问 |
| d3code-common-dubbo | Dubbo RPC 支持 |
| d3code-common-tenant | 多租户支持 |
| d3code-common-security | 安全认证 |

### 外部依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| Apache Velocity | 2.3 | 模板引擎 |
| Anyline | 8.7.2-20250603 | 运行时数据库 ORM |
| Anyline MySQL | 8.7.2-20250603 | MySQL 适配器 |
| Anyline PostgreSQL | 8.7.2-20250603 | PostgreSQL 适配器 |

## 支持的数据库

| 数据库 | 支持状态 | 说明 |
|--------|---------|------|
| MySQL | ✅ | 默认支持 |
| PostgreSQL | ✅ | 默认支持 |
| Oracle | 🔧 | 需添加依赖 |
| SQL Server | 🔧 | 需添加依赖 |
| 达梦数据库 | 🔧 | 需添加依赖 |

## 代码生成配置

### 生成类型

支持生成的代码类型：

1. **后端代码**
   - Entity（实体类）
   - Mapper（Mapper 接口与 XML）
   - Service（Service 接口与实现）
   - Controller（控制器）

2. **前端代码**
   - Vue 页面（增删改查页面）
   - API 接口文件

### 字段类型映射

Java 类型与数据库字段类型的映射关系：

| Java 类型 | 数据库类型 | 备注 |
|-----------|-----------|------|
| String | varchar, char, text | - |
| Integer | int, tinyint, smallint | - |
| Long | bigint | - |
| Double | double, float | - |
| BigDecimal | decimal, numeric | - |
| Date | date, datetime, timestamp | - |
| Boolean | bit, boolean | - |

## 核心功能

### 表导入

- 支持从数据库导入表结构
- 支持同步表结构
- 支持批量导入

### 字段配置

- 字段类型配置
- 是否必填
- 是否查询条件
- 查询方式（等于、模糊、范围等）
- 显示类型（输入框、下拉框、日期选择器等）

### 模板定制

- 支持修改默认模板
- 支持添加自定义模板
- 模板使用 Velocity 语法

## 数据库表

| 表名 | 说明 |
|------|------|
| gen_table | 代码生成表信息 |
| gen_table_column | 代码生成字段信息 |

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-modules/d3code-gen/target/d3code-gen.jar
```

## 使用流程

1. 在代码生成页面导入数据库表
2. 配置表的生成信息（包名、模块名、功能名等）
3. 配置字段的显示和查询属性
4. 预览生成的代码
5. 确认无误后下载代码
6. 将下载的代码复制到对应模块

## 注意事项

1. 生成代码前建议先预览
2. 自定义模板时注意 Velocity 语法
3. 数据库连接信息配置在 Nacos
4. 生成的代码需要根据实际业务调整
5. 建议定期同步数据库表结构

---

*模块文档生成时间: 2026-02-12*
