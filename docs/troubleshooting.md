---
document: troubleshooting
created: 2026-04-03
---

# 故障排除指南

本文档记录项目开发过程中遇到的问题及其解决方案。

---

## 后端启动问题

### 问题 1: MyBatis-Plus 与 Spring Boot 3.5.0 兼容性错误

**错误信息：**
```
Invalid bean definition with name 'contentMapper' defined in file [...]:
Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

**问题原因：**
Spring Boot 3.5.0 与 MyBatis-Plus 3.5.5 不兼容。MyBatis-Plus 3.5.5 依赖的 mybatis-spring 2.1.2 不支持 Spring Boot 3.5.0。

**解决方案：**

1. 升级 MyBatis-Plus 到 3.5.9
2. 排除 mybatis-spring 2.1.2，显式引入 mybatis-spring 3.0.4

**pom.xml 修改：**
```xml
<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.9</version>
    <exclusions>
        <exclusion>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>3.0.4</version>
</dependency>
```

**参考：**
- Epic 1 回顾报告中的详细记录
- 架构文档中的版本兼容性说明

---

### 问题 2: Flyway 迁移脚本版本冲突

**错误信息：**
```
Found more than one migration with version 2
Offenders:
-> .../V2__seed_data.sql
-> .../V2__add_deleted_column.sql
```

**问题原因：**
存在两个相同版本号（V2）的 Flyway 迁移脚本。

**解决方案：**
将后创建的迁移脚本重命名为下一个可用版本号：
```bash
mv V2__add_deleted_column.sql V3__add_deleted_column.sql
```

**预防措施：**
- 每次新增迁移脚本前检查现有版本号
- 使用递增的版本号（V1, V2, V3...）
- 团队开发时协调迁移脚本的版本号分配

---

## 版本兼容性速查表

| 组件 | 版本 | 兼容性说明 |
|------|------|------------|
| Spring Boot | 3.5.0 | 基础框架 |
| Java | 17 | 运行时 |
| MyBatis-Plus | 3.5.9+ | 必须 >= 3.5.9 以支持 Spring Boot 3.5.0 |
| mybatis-spring | 3.0.4+ | 必须 >= 3.0.4 以支持 Spring Boot 3.5.0 |
| MySQL | 8.0 | 数据库 |
| Flyway | 10.x (Spring Boot 管理) | 数据库迁移 |

---

## 更新历史

| 日期 | 问题 | 解决方案 |
|------|------|----------|
| 2026-04-03 | MyBatis-Plus 3.5.5 与 Spring Boot 3.5.0 不兼容 | 升级到 MyBatis-Plus 3.5.9 和 mybatis-spring 3.0.4 |
| 2026-04-03 | Flyway 迁移脚本版本冲突 | 重命名脚本为 V3__add_deleted_column.sql |
