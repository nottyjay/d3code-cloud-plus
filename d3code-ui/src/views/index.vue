<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="24" style="padding-left: 20px">
        <h2>D3code-Boot-Plus多租户管理系统</h2>
        <p>
          D3code-Boot-Plus 是基于 Ruoyi-Vue 针对 分布式集群 场景升级(不兼容原框架)
          <br />
          * 前端开发框架 Vue3、TS、Element Plus
          <br />
          * 后端开发框架 Spring Boot
          <br />
          * 容器框架 Undertow 基于 Netty 的高性能容器
          <br />
          * 权限认证框架 Sa-Token 支持多终端认证系统
          <br />
          * 关系数据库 PostgreSQL 适配
          <br />
          * 缓存数据库 Redis 适配 6.X 最低 4.X
          <br />
          * 数据库框架 Mybatis-Plus 快速 CRUD 增加开发效率
          <br />
          * 数据库框架 p6spy 更强劲的 SQL 分析
          <br />
          * 多数据源框架 dynamic-datasource 支持主从与多种类数据库异构
          <br />
          * 序列化框架 Jackson 统一使用 jackson 高效可靠
          <br />
          * Redis客户端 Redisson 性能强劲、API丰富
          <br />
          * 分布式限流 Redisson 全局、请求IP、集群ID 多种限流
          <br />
          * 分布式锁 Lock4j 注解锁、工具锁 多种多样
          <br />
          * 分布式幂等 Lock4j 基于分布式锁实现
          <br />
          * 分布式链路追踪 SkyWalking 支持链路追踪、网格分析、度量聚合、可视化
          <br />
          * 分布式任务调度 XXLJob 高性能 高可靠 易扩展
          <br />
          * 文件存储 Minio 本地存储
          <br />
          * 文件存储 七牛、阿里、腾讯 云存储
          <br />
          * 监控框架 SpringBoot-Admin 全方位服务监控
          <br />
          * 校验框架 Validation 增强接口安全性 严谨性
          <br />
          * Excel框架 FastExcel(原Alibaba EasyExcel) 性能优异 扩展性强
          <br />
          * 文档框架 SpringDoc、javadoc 无注解零入侵基于java注释
          <br />
          * 工具类框架 Hutool、Lombok 减少代码冗余 增加安全性
          <br />
          * 代码生成器 适配MP、SpringDoc规范化代码 一键生成前后端代码
          <br />
          * 部署方式 Docker 容器编排 一键部署业务集群
          <br />
          * 国际化 SpringMessage Spring标准国际化方案
          <br />
        </p>
        <p>
          <b>当前版本:</b>
          <span>v1.0.0</span>
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
        <p>
          <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://gitee.com/anhui-infomation/d3code-boot-3')">访问码云</el-button>
          <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://github.com/nottyjay/d3code-boot-plus')">访问GitHub</el-button>
          <!--          <el-button type="primary" icon="Cloudy" plain @click="goTarget('https://plus-doc.dromara.org/#/D3code-Boot-Plus/changlog')">更新日志</el-button>-->
        </p>
      </el-col>
    </el-row>
    <el-divider />
  </div>
</template>

<script setup name="Index" lang="ts">
const goTarget = (url: string) => {
  window.open(url, '__blank')
}
</script>

<style lang="scss" scoped>
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: 'open sans', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
