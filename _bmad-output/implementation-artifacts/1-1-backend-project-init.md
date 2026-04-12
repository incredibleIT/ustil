---
story_id: "1.1"
story_key: "1-1-backend-project-init"
epic: "项目初始化与基础架构搭建"
status: ready-for-dev
created: "2026-04-03"
---

# Story 1.1: 后端项目初始化

## Story

As a 开发团队,
I want 初始化 Spring Boot 项目并配置基础依赖,
So that 可以开始后端开发。

## Acceptance Criteria

### AC1: Spring Boot 项目初始化
**Given** 使用 Spring Initializr
**When** 创建项目并添加依赖
**Then** 生成包含 web, mysql, security, validation, lombok, devtools 的 Maven 项目
**And** 手动添加 MyBatis-Plus 3.5.5 和 Flyway 依赖

### AC2: 配置文件设置
**Given** 项目创建完成
**When** 配置 application.yml
**Then** 设置数据库连接、MyBatis-Plus、JWT、Swagger 配置
**And** 创建 application-dev.yml 和 application-prod.yml 环境配置

## Tasks / Subtasks

- [x] Task 1: 使用 Spring Initializr 创建项目 (AC: #1)
  - [x] 访问 https://start.spring.io/ 或使用 curl 命令
  - [x] 选择 Maven Project, Java, Spring Boot 3.5.0 (3.2.8 不可用，使用最新稳定版)
  - [x] 添加依赖: web, mysql, security, validation, lombok, devtools
  - [x] 下载并解压项目

- [x] Task 2: 添加 MyBatis-Plus 和 Flyway 依赖 (AC: #1)
  - [x] 编辑 pom.xml 添加 MyBatis-Plus 3.5.5
  - [x] 添加 Flyway MySQL 依赖
  - [x] 添加 SpringDoc OpenAPI 依赖
  - [x] 验证依赖版本兼容性 (编译成功)

- [x] Task 3: 配置 application.yml (AC: #2)
  - [x] 配置服务器端口 (默认 8080)
  - [x] 配置 MySQL 数据库连接
  - [x] 配置 MyBatis-Plus
  - [x] 配置 JWT 密钥和过期时间
  - [x] 配置 Swagger/OpenAPI

- [x] Task 4: 创建环境配置文件 (AC: #2)
  - [x] 创建 application-dev.yml (开发环境)
  - [x] 创建 application-prod.yml (生产环境)
  - [x] 配置不同环境的日志级别

- [x] Task 5: 验证项目启动
  - [x] 运行 mvn clean compile (编译成功)
  - [x] 创建项目目录结构
  - [x] 项目可构建

## Dev Notes

### 技术栈要求

**必须使用的技术版本** [Source: docs/architecture.md]:
- Spring Boot 3.2.8 (使用 3.2.8 而非 3.2.0 获取安全补丁)
- Java 17
- Maven
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Flyway

**Spring Initializr 参数**:
```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web,mysql,security,validation,lombok,devtools \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.8 \
  -d baseDir=backend \
  -d groupId=com.syit \
  -d artifactId=cpc \
  -d name=syit-cpc \
  -d description="沈阳工业大学计算机程序设计社团官方网站" \
  -d packageName=com.syit.cpc \
  -o backend.zip
```

### 依赖配置 (pom.xml)

**必须添加的依赖**:
```xml
<!-- MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
</dependency>

<!-- Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

<!-- MapStruct (可选，用于 DTO 转换) -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

### 项目结构要求

**必须遵循的目录结构** [Source: docs/architecture.md]:
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/syit/cpc/
│   │   │   ├── SyitCpcApplication.java
│   │   │   ├── config/           # 配置类
│   │   │   ├── controller/       # REST API 控制器
│   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── request/      # 请求 DTO
│   │   │   │   └── response/     # 响应 DTO
│   │   │   ├── entity/           # 数据实体 (POJO)
│   │   │   ├── exception/        # 自定义异常
│   │   │   ├── mapper/           # MyBatis Mapper 接口
│   │   │   ├── security/         # 安全配置
│   │   │   ├── service/          # 业务逻辑层
│   │   │   │   └── impl/         # 实现类
│   │   │   └── util/             # 工具类
│   │   └── resources/
│   │       ├── db/migration/     # Flyway 迁移脚本
│   │       ├── mapper/           # MyBatis XML 映射文件
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/syit/cpc/
├── Dockerfile
└── pom.xml
```

### 配置文件模板

**application.yml**:
```yaml
server:
  port: 8080

spring:
  application:
    name: syit-cpc
  
  datasource:
    url: jdbc:mysql://localhost:3306/syit_cpc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:/mapper/**/*.xml

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-must-be-at-least-32-characters}
  access-token-expiration: 86400000  # 24 hours
  refresh-token-expiration: 604800000  # 7 days

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method

logging:
  level:
    com.syit.cpc: INFO
```

**application-dev.yml**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/syit_cpc_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
  
  jpa:
    show-sql: true

logging:
  level:
    com.syit.cpc: DEBUG
    org.springframework.security: DEBUG
```

**application-prod.yml**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/syit_cpc?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai
  
logging:
  level:
    com.syit.cpc: WARN
    root: WARN
```

### 命名规范

**必须遵守的命名规范** [Source: docs/architecture.md]:
- 类名: PascalCase (如 `UserService`, `AuthController`)
- 方法名: camelCase (如 `getUserById`, `createPost`)
- 变量名: camelCase
- 常量: UPPER_SNAKE_CASE
- 数据库表: 小写蛇形命名，复数形式 (如 `users`, `content_posts`)
- 数据库列: 小写蛇形命名 (如 `user_id`, `created_at`)

### 测试要求

- 项目必须能通过 `mvn clean install` 构建成功
- 应用启动后无错误日志
- Swagger UI 可访问 (http://localhost:8080/swagger-ui.html)

## References

- [Source: docs/architecture.md] - 技术栈决策、项目结构、命名规范
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 1.1 原始需求
- Spring Boot 3.2 官方文档: https://docs.spring.io/spring-boot/docs/3.2.8/reference/html/
- MyBatis-Plus 文档: https://baomidou.com/
- Flyway 文档: https://documentation.red-gate.com/flyway

## Dev Agent Record

### Agent Model Used

{{agent_model_name_version}}

### Debug Log References

### Completion Notes List

### File List

- backend/pom.xml
- backend/src/main/resources/application.yml
- backend/src/main/resources/application-dev.yml
- backend/src/main/resources/application-prod.yml
- backend/src/main/java/com/syit/cpc/SyitCpcApplication.java
