# d3code-system 系统管理模块

## 模块概述

d3code-system 是系统的核心管理模块，提供用户管理、角色管理、菜单管理、部门管理、字典管理等基础系统功能。

## 启动信息

- **模块名称**: d3code-system
- **启动类**: com.alphay.boot.system.D3codeSystemApplication
- **端口**: 由 Nacos 配置文件指定

## 功能用途

1. **用户管理**：用户增删改查、密码重置、状态管理
2. **角色管理**：角色定义、权限分配、数据权限
3. **菜单管理**：菜单树结构、权限控制、图标管理
4. **部门管理**：组织架构管理、部门层级
5. **岗位管理**：岗位定义与分配
6. **字典管理**：系统字典配置、字典类型
7. **参数配置**：系统参数管理
8. **通知公告**：公告发布与管理
9. **操作日志**：用户操作日志记录
10. **登录日志**：用户登录日志记录
11. **在线用户**：在线用户监控
12. **敏感词过滤**：内容安全过滤
13. **字段加密**：敏感字段加密存储

## 目录结构

```
d3code-system/
├── src/main/java/com/alphay/boot/system/
│   ├── D3codeSystemApplication.java    # 启动类
│   ├── controller/                      # 控制器层
│   │   ├── SysUserController.java     # 用户控制器
│   │   ├── SysRoleController.java     # 角色控制器
│   │   ├── SysMenuController.java     # 菜单控制器
│   │   ├── SysDeptController.java     # 部门控制器
│   │   └── ...
│   ├── domain/                         # 实体类对象
│   │   ├── SysUser.java              # 用户实体
│   │   ├── SysRole.java              # 角色实体
│   │   ├── SysMenu.java              # 菜单实体
│   │   └── ...
│   ├── mapper/                         # 数据访问层
│   │   ├── SysUserMapper.java        # 用户 Mapper
│   │   ├── SysRoleMapper.java        # 角色 Mapper
│   │   └── ...
│   ├── service/                        # 业务逻辑层
│   │   ├── ISysUserService.java      # 用户服务
│   │   ├── ISysRoleService.java      # 角色服务
│   │   └── ...
│   │   └── impl/                      # 服务实现
│   ├── dubbo/                          # Dubbo 服务暴露
│   │   └── SysUserDubboService.java # 用户 Dubbo 服务
│   └── listener/                       # 事件监听器
└── src/main/resources/
    ├── application.yml                 # 应用配置
    ├── bootstrap.yml                  # 引导配置
    └── mapper/                         # MyBatis Mapper XML
        ├── SysUserMapper.xml
        ├── SysRoleMapper.xml
        └── ...
```

## 依赖模块

### 内部依赖

| 依赖模块 | 用途 |
|---------|------|
| d3code-common-nacos | Nacos 服务注册与配置中心 |
| d3code-common-sentinel | Sentinel 限流熔断 |
| d3code-common-log | 日志组件 |
| d3code-common-service-impl | 服务实现基础 |
| d3code-common-doc | API 文档 |
| d3code-common-web | Web 相关组件 |
| d3code-common-mybatis | MyBatis 数据访问 |
| d3code-common-dubbo | Dubbo RPC 支持 |
| d3code-common-seata | 分布式事务 |
| d3code-common-idempotent | 幂等性支持 |
| d3code-common-tenant | 多租户支持 |
| d3code-common-security | 安全认证 |
| d3code-common-translation | 翻译支持 |
| d3code-common-sensitive | 敏感词过滤 |
| d3code-common-encrypt | 字段加密 |
| d3code-api-system | 系统 API 接口定义 |
| d3code-api-resource | 资源 API 接口定义 |

## 核心功能模块

### 用户管理

- **用户信息管理**：新增、修改、删除、查询用户
- **密码管理**：密码修改、重置、强度校验
- **用户状态**：启用、禁用用户
- **用户角色**：分配用户角色
- **用户部门**：设置用户所属部门

### 角色管理

- **角色定义**：新增、修改、删除角色
- **权限分配**：为角色分配菜单权限
- **数据权限**：设置角色数据权限范围
- **角色状态**：启用、禁用角色

### 菜单管理

- **菜单树**：构建系统菜单树结构
- **菜单类型**：目录、菜单、按钮
- **权限标识**：菜单对应的权限标识
- **路由配置**：前端路由地址配置

### 部门管理

- **组织架构**：部门树形结构
- **部门排序**：部门显示顺序
- **部门状态**：启用、禁用部门

### 日志管理

- **操作日志**：记录用户关键操作
- **登录日志**：记录用户登录信息
- **日志查询**：按条件查询日志

## Dubbo 服务

系统模块通过 Dubbo 暴露以下服务供其他模块调用：

| 服务名称 | 说明 |
|---------|------|
| SysUserDubboService | 用户服务 |
| SysRoleDubboService | 角色服务 |
| SysDeptDubboService | 部门服务 |

## 数据库表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_menu | 菜单表 |
| sys_dept | 部门表 |
| sys_post | 岗位表 |
| sys_dict_type | 字典类型表 |
| sys_dict_data | 字典数据表 |
| sys_config | 参数配置表 |
| sys_notice | 通知公告表 |
| sys_oper_log | 操作日志表 |
| sys_logininfor | 登录日志表 |

## 运行方式

```bash
# 打包
mvn clean package -Pdev

# 运行
java -jar d3code-modules/d3code-system/target/d3code-system.jar
```

## 注意事项

1. 系统模块是核心模块，建议独立数据库
2. 用户密码加密存储，不可逆
3. 删除用户、角色等操作建议逻辑删除
4. 敏感操作需要记录操作日志
5. 多租户场景下需要隔离租户数据

---

*模块文档生成时间: 2026-02-12*
