---
project: syit-cpc
document: architecture
created: 2026-04-03
stepsCompleted: [1, 2, 3, 4, 5, 6, 7, 8]
workflowType: 'architecture'
lastStep: 8
status: 'complete'
completedAt: '2026-04-03'
---

# 沈阳工业大学计算机程序设计社团 - 架构设计文档

## 文档信息

- **项目名称**: 沈阳工业大学计算机程序设计社团官方网站
- **文档类型**: 架构设计
- **创建日期**: 2026-04-03
- **当前阶段**: 核心架构决策 (Step 4)

---

## 输入文档摘要

### 产品简报要点
- **目标**: 替代微信群，建立社团专属的信息发布与交流平台
- **核心用户**: 预备成员、正式成员、负责人
- **关键功能**: 成员管理、内容发布、审核机制

### PRD 关键要求
- **后端**: Spring Boot 3.x + Java 17 + Maven
- **前端**: Vue.js 3 + TypeScript + Vite
- **数据库**: MySQL 8.0
- **审核机制**: 20% 通过制

### UX 设计方向
- **设计模式**: 内容优先布局 (Content-first)
- **UI 框架**: Element Plus + TailwindCSS
- **核心体验**: 浏览最新资讯和博客

---

## 项目上下文分析

### 技术栈约束
- **后端框架**: Spring Boot 3.x (已确定)
- **前端框架**: Vue.js 3 + TypeScript (已确定)
- **构建工具**: Maven (后端), Vite (前端)
- **数据库**: MySQL 8.0 (已确定)

### 团队特征
- 学生社团开发团队
- 需要简单易维护的架构
- 学习成本需要考虑

### 部署环境
- 学校服务器或云主机
- Docker 容器化部署

---

## 启动模板评估

### 后端启动模板: Spring Initializr

**选择理由**:
- 官方标准项目结构
- 与 Spring Boot 3.x 完全兼容
- Maven 构建工具支持

**初始化命令**:
```bash
curl https://start.spring.io/starter.zip \
  -d dependencies=web,mysql,security,validation,lombok,devtools \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.5.0 \
  -d baseDir=backend \
  -o backend.zip
```

**注意**: 实际项目使用 Spring Boot 3.5.0（最新稳定版，包含安全补丁），而非文档中最初指定的 3.2.x。

**手动添加依赖** (pom.xml):
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

<!-- Flyway -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

**版本兼容性说明：**
- Spring Boot 3.5.0 需要 MyBatis-Plus 3.5.9+ 和 mybatis-spring 3.0.4+
- 原 MyBatis-Plus 3.5.5 与 Spring Boot 3.5.0 存在兼容性问题

**已确定的架构决策**:
- Java 17 运行时
- Spring Web MVC
- MyBatis-Plus 3.5.9 (替代 Spring Data JPA)
- Spring Security
- Validation
- Lombok
- Spring Boot DevTools
- Flyway 数据库迁移

### 前端启动模板: Vite + Vue 3

**选择理由**:
- 快速冷启动
- 原生 TypeScript 支持
- 现代化的开发体验

**初始化命令**:
```bash
npm create vue@latest frontend
# 选择: TypeScript, Router, Pinia, ESLint, Prettier
```

**已确定的架构决策**:
- Vue 3 Composition API
- TypeScript
- Vue Router
- Pinia 状态管理
- Vite 构建工具

---

## 核心架构决策

### 已确定决策 (来自启动模板)

**后端**:
- 框架: Spring Boot 3.5.0
- 语言: Java 17
- 构建: Maven
- ORM: MyBatis-Plus 3.5.9
- 安全: Spring Security
- 迁移: Flyway

**前端**:
- 框架: Vue.js 3.4.x
- 语言: TypeScript 5.x
- 构建: Vite 5.x
- 路由: Vue Router 4.x
- 状态: Pinia 2.x

---

## 核心架构决策详情

### 决策优先级分析

**关键决策 (阻塞实施)**:
- 数据架构策略
- 认证与授权机制
- API 设计规范
- 前端状态管理

**重要决策 (塑造架构)**:
- 缓存策略
- 部署架构
- 错误处理模式

**延后决策 (MVP 之后)**:
- 性能监控方案（Micrometer + Prometheus）
- 缓存策略实施（Caffeine/Redis，基于实际性能数据）
- 自动化测试覆盖率目标
- 日志聚合方案（ELK Stack）

---

### 数据架构

#### 1. 数据库 Schema 设计策略
**决策**: 采用 MyBatis-Plus + 手写 SQL/XML 模式

**方案**:
- 使用 MyBatis-Plus 作为 ORM 框架
- 手写 DDL 创建数据库表结构
- 使用 XML 或注解方式编写 SQL
- 利用 MyBatis-Plus 提供的 BaseMapper 简化 CRUD

**理由**:
- SQL 可控，便于复杂查询优化
- MyBatis-Plus 提供代码生成器，减少重复代码
- 社区活跃，文档丰富
- 适合有一定 SQL 基础的团队

**影响组件**: 所有数据访问层、Mapper 接口、XML 文件

#### 2. 数据验证策略
**决策**: 分层验证 - Bean Validation + 业务验证

**方案**:
- 使用 Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email` 等)
- Service 层补充业务规则验证
- Controller 层使用 `@Valid` 注解

**理由**:
- 声明式验证简洁清晰
- 分层验证职责明确
- 与 Spring Boot 集成良好

**影响组件**: DTO、Entity、Service

#### 3. 数据库迁移方案
**决策**: 使用 Flyway 进行版本化迁移

**方案**:
- 手写 DDL 创建初始 schema
- 所有变更使用 Flyway SQL 脚本
- 脚本存放于 `src/main/resources/db/migration`
- MyBatis-Plus 仅负责数据操作，不负责 schema 生成

**理由**:
- 数据库结构完全可控
- 版本化管理便于回滚
- 团队协作冲突少
- 与 MyBatis-Plus 配合良好

**影响组件**: 数据库部署流程

#### 4. MyBatis-Plus 使用策略
**决策**: BaseMapper + XML/注解混合模式

**方案**:
- 简单 CRUD: 继承 BaseMapper，使用内置方法
- 复杂查询: 使用 XML 映射文件或 `@Select` 注解
- 分页查询: 使用 MyBatis-Plus 分页插件
- 代码生成: 使用 MyBatis-Plus Generator 生成基础代码

**Mapper 接口示例**:
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 复杂查询使用 XML 或注解
    @Select("SELECT * FROM users WHERE status = #{status}")
    List<User> selectByStatus(@Param("status") Integer status);
}
```

**影响组件**: Mapper 接口、XML 文件、Service 层

---

#### 5. 缓存策略
**决策**: MVP 无缓存 + 预留 Spring Cache 扩展接口

**方案**:
- **MVP 阶段**: 无缓存，直接数据库查询
- **代码预留**: Service 层添加 `@Cacheable` 注解（暂不配置缓存实现）
- **Phase 2 触发条件** (满足任一):
- 慢查询 > 200ms (平均响应时间)
- QPS > 100 (每秒查询数)
- 日活跃用户 > 500
- 数据库 CPU 使用率 > 70% (持续5分钟)
- **技术选型**: 优先 Caffeine（单机），必要时迁移 Redis（分布式）

**代码预留示例**:
```java
@Service
public class ContentServiceImpl implements ContentService {
    // 现在：直接查数据库
    // 未来：添加缓存配置即可生效，业务代码零改动
    @Cacheable(value = "content", key = "#id")
    @Override
    public Content getContentById(Long id) {
        return contentMapper.selectById(id);
    }
}
```

**配置预留**:
```yaml
# application.yml - MVP 阶段关闭缓存
spring:
  cache:
    type: none  # 后期改为 caffeine 或 redis
```

**理由**:
- 保持 MVP 简单，降低学习成本
- 社团系统初期数据量小，MySQL 查询性能足够
- 预留扩展接口，未来升级零业务代码改动
- 基于实际性能数据决策，避免过早优化

**影响组件**: Service 层、配置文件

**Phase 2 升级路径**:
1. 监控慢查询日志，确认性能瓶颈
2. 添加 Caffeine 依赖和配置
3. 如需要分布式缓存，迁移至 Redis

---

### 认证与安全

#### 1. 认证方式
**决策**: JWT (JSON Web Token) + Bearer Token

**方案**:
- 登录成功后颁发 JWT access token (有效期 24 小时)
- 同时颁发 Refresh Token (有效期 7 天) 用于 Token 续期
- 使用 Spring Security OAuth2 Resource Server
- Token 过期前自动续期机制 (可选实现)

**理由**:
- 无状态，适合前后端分离
- 扩展性好，支持移动端
- 社区支持完善

**影响组件**: Security 配置、登录接口

#### 2. 授权模式
**决策**: 基于角色的访问控制 (RBAC)

**方案**:
- 角色定义: `ROLE_PROBATION` (预备), `ROLE_MEMBER` (正式), `ROLE_ADMIN` (负责人)
- 使用 Spring Security `@PreAuthorize` 注解
- 权限粒度：API 级别

**权限矩阵** (示例):
| API | 预备成员 | 正式成员 | 负责人 |
|-----|---------|---------|--------|
| GET /api/content | ✅ | ✅ | ✅ |
| POST /api/content | ❌ | ✅ | ✅ |
| POST /api/review/vote | ❌ | ✅ | ✅ |
| GET /api/admin/users | ❌ | ❌ | ✅ |

**理由**:
- 符合社团三级成员结构
- 实现简单，易于理解
- Spring Security 原生支持

**影响组件**: Controller、Security 配置

#### 3. API 安全策略
**决策**: 标准安全头部 + CORS + 输入验证

**方案**:
- 启用 Spring Security 默认安全头部
- 配置 CORS 允许前端域名
- 全局输入验证和 SQL 注入防护

**影响组件**: Security 配置、Web 配置

---

### API 与通信

#### 1. API 设计模式
**决策**: RESTful API + 统一响应格式

**方案**:
- 遵循 REST 资源命名规范
- HTTP 方法语义化 (GET/POST/PUT/DELETE)
- 统一响应包装: `{ "code": 200, "message": "...", "data": {} }`

**理由**:
- 行业标准，易于理解和调试
- 与前端框架配合良好
- 文档生成工具支持好

**影响组件**: 所有 Controller

#### 2. API 文档方案
**决策**: SpringDoc OpenAPI (Swagger UI)

**方案**:
- 使用注解描述 API 接口
- 自动生成 Swagger UI
- 支持导出 OpenAPI 规范

**理由**:
- 与 Spring Boot 3.x 兼容
- 自动生成文档减少维护成本
- 支持在线调试

**影响组件**: Controller 注解、配置

#### 3. 错误处理标准
**决策**: 全局异常处理 + 业务错误码

**方案**:
- `@ControllerAdvice` 全局异常捕获
- 自定义 `BusinessException` 业务异常
- 错误码格式: `ERR_{MODULE}_{CODE}` (如 `ERR_AUTH_001`)

**理由**:
- 统一错误响应格式
- 便于前端统一处理
- 错误定位清晰

**影响组件**: Exception Handler、Service

---

### 前端架构

#### 1. 状态管理策略
**决策**: Pinia 分模块管理

**方案**:
- `auth`: 用户认证状态
- `user`: 用户信息
- `content`: 资讯/博客内容
- `review`: 审核状态

**理由**:
- Vue 3 官方推荐
- TypeScript 支持好
- 模块化管理清晰

**影响组件**: Store 模块

#### 2. 组件架构模式
**决策**: 按功能模块组织 + 原子设计

**方案**:
```
src/components/
  ├── common/          # 通用组件
  ├── layout/          # 布局组件
  ├── auth/            # 认证相关
  ├── member/          # 成员管理
  ├── content/         # 内容相关
  └── review/          # 审核相关
```

**理由**:
- 与业务模块对应
- 便于团队协作
- 符合 UX 设计的功能划分

**影响组件**: 组件目录结构

#### 3. 性能优化方案
**决策**: 按需加载 + 图片优化

**方案**:
- 路由懒加载
- Element Plus 组件按需引入
- 图片使用 WebP 格式 + 懒加载

**理由**:
- 首屏加载速度快
- 符合内容优先的 UX 设计
- 实现成本低

**影响组件**: 路由配置、组件引入

---

### 基础设施与部署

#### 1. 容器化方案
**决策**: Docker + Docker Compose

**方案**:
- 后端服务容器化
- MySQL 使用官方镜像
- 使用 Docker Compose 编排

**理由**:
- 部署简单，环境一致
- 适合学校服务器部署
- 便于后续迁移

**影响组件**: Dockerfile、docker-compose.yml

#### 2. CI/CD 流程
**决策**: 基础 CI/CD (GitHub Actions)

**方案**:
- PR 时自动构建和测试
- 合并到 main 分支自动部署到测试环境
- 手动触发生产部署

**理由**:
- 学生社团项目复杂度适中
- GitHub Actions 免费且易用
- 避免过度工程化

**影响组件**: `.github/workflows/`

#### 3. 环境配置管理
**决策**: Spring Profiles + 环境变量

**方案**:
- `application.yml`: 通用配置
- `application-dev.yml`: 开发环境
- `application-prod.yml`: 生产环境
- 敏感信息使用环境变量注入

**理由**:
- Spring Boot 原生支持
- 配置分离清晰
- 安全信息不外泄

**影响组件**: 配置文件

---

### 决策影响分析

**实施顺序**:
1. 数据库 Schema 设计 (Flyway 迁移脚本)
2. 数据实体与 Mapper 接口设计 (MyBatis-Plus)
3. 认证接口实现 (依赖: JWT 决策)
4. 业务 API 开发 (依赖: REST 规范，预留 `@Cacheable` 注解)
5. 前端状态管理 (依赖: Pinia 模块划分)
6. 部署配置 (依赖: Docker 方案)

**Phase 2 实施（性能优化阶段）**:
- 性能监控与慢查询分析
- 缓存策略实施（Caffeine/Redis）
- 数据库索引优化

**跨组件依赖**:
- 认证方式影响所有受保护 API
- API 响应格式影响前后端所有接口
- 错误处理策略影响异常抛出方式
- 缓存注解预留影响 Service 层实现模式

---

## 实现模式与一致性规则

### 潜在冲突点识别

**命名冲突**:
- 数据库表/列命名规范
- API 端点命名模式
- 文件和目录命名
- 组件/函数/变量命名

**结构冲突**:
- 测试文件位置
- 组件组织方式
- 工具类存放位置
- 配置文件组织

**格式冲突**:
- API 响应包装格式
- 错误响应结构
- 日期时间格式
- JSON 字段命名

**通信冲突**:
- 事件命名规范
- 状态更新模式
- 日志格式和级别

**流程冲突**:
- 加载状态处理
- 错误恢复模式
- 认证流程模式

---

### 命名模式

#### 数据库命名规范

**表命名**:
- 使用小写蛇形命名: `users`, `content_posts`, `review_votes`
- 复数形式表示实体集合
- 关联表: `user_roles`, `post_tags`

**列命名**:
- 主键: `id`
- 外键: `{table}_id` (如 `user_id`, `post_id`)
- 时间戳: `created_at`, `updated_at`
- 布尔值: `is_{形容词}` (如 `is_active`, `is_published`)

**索引命名**:
- `idx_{table}_{column}` (如 `idx_users_email`)

**核心表索引策略**:
```sql
-- users 表
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- content 表
CREATE INDEX idx_content_status_created ON content(status, created_at);
CREATE INDEX idx_content_author ON content(author_id);

-- review_votes 表 (20%审核投票查询优化)
CREATE INDEX idx_review_content_user ON review_votes(content_id, user_id);
CREATE INDEX idx_review_content_status ON review_votes(content_id, status);
```

#### API 命名规范

**端点命名**:
- 资源使用复数: `/api/users`, `/api/posts`
- 嵌套资源: `/api/users/{id}/posts`
- 动作使用动词: `/api/auth/login`, `/api/auth/logout`

**路由参数**:
- 使用 `{id}` 格式
- 查询参数使用蛇形命名: `user_id`, `page_size`

#### 代码命名规范

**Java (后端)**:
- 类名: PascalCase (如 `UserService`, `PostController`)
- 方法名: camelCase (如 `getUserById`, `createPost`)
- 变量名: camelCase
- 常量: UPPER_SNAKE_CASE

**TypeScript (前端)**:
- 组件名: PascalCase (如 `UserCard.vue`)
- 函数名: camelCase
- 类型名: PascalCase (如 `UserDTO`, `PostType`)
- 接口名: PascalCase 前缀 I (如 `IUserService`)

---

### 结构模式

#### 后端项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/syit/cpc/
│   │   │   ├── config/           # 配置类
│   │   │   │   ├── MybatisPlusConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   └── SwaggerConfig.java
│   │   │   │
│   │   │   ├── controller/       # REST API 控制器
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── ContentController.java
│   │   │   │   ├── ReviewController.java
│   │   │   │   └── PromotionController.java
│   │   │   │
│   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── CreateContentRequest.java
│   │   │   │   │   └── SubmitPromotionRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       ├── UserResponse.java
│   │   │   │       ├── ContentResponse.java
│   │   │   │       └── ReviewStatusResponse.java
│   │   │   │
│   │   │   ├── entity/           # 数据实体 (POJO)
│   │   │   │   ├── User.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── Content.java
│   │   │   │   ├── ContentType.java
│   │   │   │   ├── ReviewVote.java
│   │   │   │   ├── PromotionApplication.java
│   │   │   │   └── BaseEntity.java
│   │   │   │
│   │   │   ├── exception/        # 自定义异常
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ErrorCode.java
│   │   │   │
│   │   │   ├── mapper/           # MyBatis Mapper 接口
│   │   │   │   ├── UserMapper.java
│   │   │   │   ├── ContentMapper.java
│   │   │   │   ├── ReviewVoteMapper.java
│   │   │   │   └── PromotionMapper.java
│   │   │   │
│   │   │   ├── security/         # 安全配置
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── UserDetailsServiceImpl.java
│   │   │   │   └── UserPrincipal.java
│   │   │   │
│   │   │   ├── service/          # 业务逻辑层
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── ContentService.java
│   │   │   │   ├── ReviewService.java
│   │   │   │   ├── PromotionService.java
│   │   │   │   └── impl/
│   │   │   │       ├── AuthServiceImpl.java
│   │   │   │       ├── UserServiceImpl.java
│   │   │   │       ├── ContentServiceImpl.java
│   │   │   │       ├── ReviewServiceImpl.java
│   │   │   │       └── PromotionServiceImpl.java
│   │   │   │
│   │   │   └── util/             # 工具类
│   │   │       ├── DateUtil.java
│   │   │       └── ValidationUtil.java
│   │   │
│   │   └── resources/
│   │       ├── db/migration/     # Flyway 迁移脚本
│   │       │   ├── V1__init_schema.sql
│   │       │   ├── V2__seed_data.sql
│   │       │   └── V3__add_deleted_column.sql
│   │       ├── mapper/           # MyBatis XML 映射文件
│   │       │   ├── UserMapper.xml
│   │       │   ├── ContentMapper.xml
│   │       │   ├── ReviewVoteMapper.xml
│   │       │   └── PromotionMapper.xml
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/com/syit/cpc/
├── Dockerfile
└── pom.xml
```

#### 前端项目结构

```
frontend/
├── src/
│   ├── api/                # API 请求封装
│   ├── assets/             # 静态资源
│   ├── components/         # 组件
│   │   ├── common/         # 通用组件
│   │   ├── layout/         # 布局组件
│   │   ├── auth/           # 认证相关
│   │   ├── member/         # 成员管理
│   │   ├── content/        # 内容相关
│   │   └── review/         # 审核相关
│   ├── composables/        # 组合式函数
│   ├── router/             # 路由配置
│   ├── stores/             # Pinia 状态管理
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── content.ts
│   │   └── review.ts
│   ├── styles/             # 全局样式
│   ├── types/              # TypeScript 类型
│   ├── utils/              # 工具函数
│   └── views/              # 页面视图
├── public/
├── Dockerfile
└── package.json
```

---

### 格式模式

#### API 响应格式

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "error": {
    "code": "ERR_AUTH_001",
    "detail": "用户名或密码错误"
  }
}
```

#### 数据交换格式

**JSON 字段命名**:
- 后端: 蛇形命名 (`user_name`, `created_at`)
- 前端 DTO: 驼峰命名 (`userName`, `createdAt`)
- 转换层: 使用 MapStruct 1.5+ 或手动映射

**MapStruct 配置** (pom.xml):
```xml
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

**日期格式**:
- API: ISO 8601 (`2024-01-15T10:30:00Z`)
- 显示: `YYYY-MM-DD HH:mm`

---

### 通信模式

#### 事件命名规范

**后端事件** (如使用):
- 格式: `{Entity}{Action}Event`
- 示例: `UserRegisteredEvent`, `PostPublishedEvent`

**前端事件**:
- 使用 Pinia actions，不直接使用 EventBus
- Action 命名: `{verb}{Noun}` (如 `fetchUser`, `submitPost`)

#### 状态管理模式

**Pinia Store 结构**:
```typescript
// stores/auth.ts
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '',
    user: null as User | null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(credentials: LoginDTO) { }
    logout() { }
  }
})
```

---

### 流程模式

#### 错误处理模式

**后端**:
- 使用 `@ControllerAdvice` 全局处理
- 业务异常继承 `BusinessException`
- 系统异常记录日志后返回通用错误

**前端**:
- API 层统一封装错误处理
- 使用 Element Plus `ElMessage` 显示错误
- 全局错误边界捕获未处理异常

#### 加载状态模式

**前端**:
- 使用 `loading` 状态变量
- 按钮使用 `:loading` 属性
- 页面使用 Element Plus `v-loading` 指令

---

### 强制执行指南

**所有 AI 代理必须遵守**:

1. **命名规范**: 严格遵循上述命名模式，不得混用
2. **项目结构**: 新文件必须放在正确的目录中
3. **API 格式**: 所有响应必须符合统一格式
4. **错误处理**: 使用定义的错误码和异常类
5. **类型安全**: TypeScript 必须定义接口，Java 必须使用泛型

**模式验证**:
- 代码审查时检查命名规范
- 使用 ESLint/Checkstyle 自动检查
- 架构文档作为代码审查清单

---

## 项目结构与边界

### 完整项目目录结构

```
syit-cpc/
├── README.md
├── docker-compose.yml
├── .env.example
├── .gitignore
├── .github/
│   └── workflows/
│       ├── ci.yml              # CI 工作流
│       └── deploy.yml          # 部署工作流
│
├── backend/                    # 后端服务
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/com/syit/cpc/
│       │   │   ├── SyitCpcApplication.java
│       │   │   │
│       │   │   ├── config/           # 配置类
│       │   │   │   ├── MybatisPlusConfig.java
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   ├── WebConfig.java
│       │   │   │   ├── JwtConfig.java
│       │   │   │   └── SwaggerConfig.java
│       │   │   │
│       │   │   ├── controller/       # REST API 控制器
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── UserController.java
│       │   │   │   ├── ContentController.java
│       │   │   │   ├── ReviewController.java
│       │   │   │   └── PromotionController.java
│       │   │   │
│       │   │   ├── dto/              # 数据传输对象
│       │   │   │   ├── request/
│       │   │   │   │   ├── LoginRequest.java
│       │   │   │   │   ├── RegisterRequest.java
│       │   │   │   │   ├── CreateContentRequest.java
│       │   │   │   │   └── SubmitPromotionRequest.java
│       │   │   │   └── response/
│       │   │   │       ├── ApiResponse.java
│       │   │   │       ├── UserResponse.java
│       │   │   │       ├── ContentResponse.java
│       │   │   │       └── ReviewStatusResponse.java
│       │   │   │
│       │   │   ├── entity/           # 数据实体 (POJO)
│       │   │   │   ├── User.java
│       │   │   │   ├── UserRole.java
│       │   │   │   ├── Content.java
│       │   │   │   ├── ContentType.java
│       │   │   │   ├── ReviewVote.java
│       │   │   │   ├── PromotionApplication.java
│       │   │   │   └── BaseEntity.java
│       │   │   │
│       │   │   ├── exception/        # 自定义异常
│       │   │   │   ├── BusinessException.java
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   └── ErrorCode.java
│       │   │   │
│       │   │   ├── mapper/           # MyBatis Mapper 接口
│       │   │   │   ├── UserMapper.java
│       │   │   │   ├── ContentMapper.java
│       │   │   │   ├── ReviewVoteMapper.java
│       │   │   │   └── PromotionMapper.java
│       │   │   │
│       │   │   ├── security/         # 安全配置
│       │   │   │   ├── JwtTokenProvider.java
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   ├── UserDetailsServiceImpl.java
│       │   │   │   └── UserPrincipal.java
│       │   │   │
│       │   │   ├── service/          # 业务逻辑层
│       │   │   │   ├── AuthService.java
│       │   │   │   ├── UserService.java
│       │   │   │   ├── ContentService.java
│       │   │   │   ├── ReviewService.java
│       │   │   │   ├── PromotionService.java
│       │   │   │   └── impl/
│       │   │   │       ├── AuthServiceImpl.java
│       │   │   │       ├── UserServiceImpl.java
│       │   │   │       ├── ContentServiceImpl.java
│       │   │   │       ├── ReviewServiceImpl.java
│       │   │   │       └── PromotionServiceImpl.java
│       │   │   │
│       │   │   └── util/             # 工具类
│       │   │       ├── DateUtil.java
│       │   │       └── ValidationUtil.java
│       │   │
│       │   └── resources/
│       │       ├── db/migration/     # Flyway 迁移脚本
│       │       │   ├── V1__init_schema.sql
│       │       │   └── V2__seed_data.sql
│       │       ├── mapper/           # MyBatis XML 映射文件
│       │       │   ├── UserMapper.xml
│       │       │   ├── ContentMapper.xml
│       │       │   ├── ReviewVoteMapper.xml
│       │       │   └── PromotionMapper.xml
│       │       ├── application.yml
│       │       ├── application-dev.yml
│       │       └── application-prod.yml
│       │
│       └── test/
│           └── java/com/syit/cpc/
│               ├── controller/
│               ├── service/
│               └── mapper/
│
└── frontend/                   # 前端应用
    ├── Dockerfile
    ├── package.json
    ├── tsconfig.json
    ├── vite.config.ts
    ├── index.html
    ├── .env
    ├── .env.production
    └── src/
        ├── main.ts
        ├── App.vue
        │
        ├── api/                # API 请求封装
        │   ├── index.ts
        │   ├── auth.ts
        │   ├── user.ts
        │   ├── content.ts
        │   └── review.ts
        │
        ├── assets/             # 静态资源
        │   ├── logo.png
        │   └── styles/
        │       ├── main.css
        │       └── variables.css
        │
        ├── components/         # 组件
        │   ├── common/         # 通用组件
        │   │   ├── AppButton.vue
        │   │   ├── AppInput.vue
        │   │   ├── AppCard.vue
        │   │   └── AppLoading.vue
        │   │
        │   ├── layout/         # 布局组件
        │   │   ├── AppHeader.vue
        │   │   ├── AppSidebar.vue
        │   │   ├── AppFooter.vue
        │   │   └── MainLayout.vue
        │   │
        │   ├── auth/           # 认证相关
        │   │   ├── LoginForm.vue
        │   │   └── RegisterForm.vue
        │   │
        │   ├── member/         # 成员管理
        │   │   ├── MemberList.vue
        │   │   ├── MemberCard.vue
        │   │   └── MemberProfile.vue
        │   │
        │   ├── content/        # 内容相关
        │   │   ├── ContentList.vue
        │   │   ├── ContentCard.vue
        │   │   ├── ContentDetail.vue
        │   │   ├── ContentEditor.vue
        │   │   └── ContentFilters.vue
        │   │
        │   └── review/         # 审核相关
        │       ├── ReviewPanel.vue
        │       ├── VoteButton.vue
        │       └── ReviewStatus.vue
        │
        ├── composables/        # 组合式函数
        │   ├── useAuth.ts
        │   ├── useUser.ts
        │   └── useContent.ts
        │
        ├── router/             # 路由配置
        │   └── index.ts
        │
        ├── stores/             # Pinia 状态管理
        │   ├── index.ts
        │   ├── auth.ts
        │   ├── user.ts
        │   ├── content.ts
        │   └── review.ts
        │
        ├── types/              # TypeScript 类型
        │   ├── user.ts
        │   ├── content.ts
        │   ├── review.ts
        │   └── api.ts
        │
        ├── utils/              # 工具函数
        │   ├── request.ts      # Axios 封装
        │   ├── storage.ts      # 本地存储
        │   ├── format.ts       # 格式化
        │   └── validate.ts     # 验证
        │
        └── views/              # 页面视图
            ├── HomeView.vue
            ├── LoginView.vue
            ├── RegisterView.vue
            ├── ContentListView.vue
            ├── ContentDetailView.vue
            ├── ContentEditView.vue
            ├── MemberListView.vue
            ├── MemberProfileView.vue
            ├── ReviewPanelView.vue
            └── PromotionView.vue
```

---

### 架构边界

#### API 边界

**公开 API (无需认证)**:
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册
- `GET /api/content` - 获取内容列表
- `GET /api/content/{id}` - 获取内容详情

**受保护 API (需认证)**:
- `GET /api/user/me` - 获取当前用户
- `PUT /api/user/me` - 更新用户信息
- `POST /api/content` - 创建内容
- `PUT /api/content/{id}` - 更新内容
- `DELETE /api/content/{id}` - 删除内容
- `POST /api/review/vote` - 投票

**管理员 API (需管理员角色)**:
- `GET /api/admin/users` - 获取所有用户
- `PUT /api/admin/users/{id}/role` - 修改用户角色
- `GET /api/admin/review/pending` - 获取待审核内容

#### 组件边界

**前端组件通信**:
- 父子组件: Props / Emits
- 跨组件状态: Pinia Store
- 全局事件: 避免使用，优先使用 Store

**后端服务边界**:
- Controller: 处理 HTTP 请求/响应
- Service: 业务逻辑处理
- Mapper: 数据访问 (MyBatis)
- Entity: 数据模型

#### 数据边界

**数据库访问**:
- 所有数据访问通过 Mapper 层
- 禁止在 Controller 中直接操作数据库
- 复杂查询使用 XML 或注解 SQL

**缓存边界**:
- Phase 1: 无缓存
- Phase 2: Service 层添加缓存注解

**缓存数据一致性策略** (Phase 2):
- **写策略**: Cache-Aside (先写数据库，再删缓存)
- **读策略**: 先查缓存，未命中则查数据库并写入缓存
- **过期策略**: 默认 30 分钟，关键数据 10 分钟
- **主动失效**: 数据更新时主动删除相关缓存

---

### 需求到结构映射

#### 功能模块映射

**成员管理模块**:
- 后端: `UserController`, `UserService`, `UserMapper`, `User` entity
- 前端: `MemberListView`, `MemberProfileView`, `MemberList.vue`, `MemberCard.vue`
- Store: `user.ts`

**内容发布模块**:
- 后端: `ContentController`, `ContentService`, `ContentMapper`, `Content` entity
- 前端: `ContentListView`, `ContentDetailView`, `ContentEditView`, `ContentList.vue`, `ContentEditor.vue`
- Store: `content.ts`

**审核机制模块**:
- 后端: `ReviewController`, `ReviewService`, `ReviewVoteMapper`, `ReviewVote` entity
- 前端: `ReviewPanelView`, `ReviewPanel.vue`, `VoteButton.vue`
- Store: `review.ts`

**转正系统模块**:
- 后端: `PromotionController`, `PromotionService`, `PromotionMapper`, `PromotionApplication` entity
- 前端: `PromotionView.vue`

#### 横切关注点映射

**认证授权**:
- 后端: `AuthController`, `SecurityConfig`, `JwtTokenProvider`, `JwtAuthenticationFilter`
- 前端: `LoginView`, `RegisterView`, `useAuth.ts`, `auth.ts` store

**错误处理**:
- 后端: `GlobalExceptionHandler`, `BusinessException`
- 前端: `request.ts` 拦截器

**API 文档**:
- 后端: `SwaggerConfig`, Controller 注解

---

### 集成点

#### 内部通信

**前后端通信**:
- 协议: HTTP/REST
- 认证: JWT Bearer Token
- 格式: JSON

**前端内部通信**:
- 路由: Vue Router
- 状态: Pinia
- HTTP 客户端: Axios

**后端内部通信**:
- 依赖注入: Spring IoC
- 事务管理: Spring Transaction
- 安全: Spring Security

#### 外部集成

**数据库**:
- MySQL 8.0
- 连接池: HikariCP (Spring Boot 默认)

**容器化**:
- 后端: OpenJDK 17 基础镜像
- 前端: Nginx 静态文件服务
- 编排: Docker Compose

---

### 文件组织模式

#### 配置文件

**后端配置**:
- `application.yml`: 通用配置
- `application-dev.yml`: 开发环境
- `application-prod.yml`: 生产环境
- 环境变量注入敏感信息

**前端配置**:
- `.env`: 开发环境变量
- `.env.production`: 生产环境变量
- `vite.config.ts`: 构建配置

#### 测试组织

**后端测试**:
```
test/java/com/syit/cpc/
├── controller/       # 控制器测试
├── service/          # 服务层测试
└── mapper/           # 数据访问测试
```

**前端测试** (预留):
```
__tests__/
├── components/       # 组件测试
├── stores/           # Store 测试
└── utils/            # 工具函数测试
```

---


## 架构验证结果

### 一致性验证

**决策兼容性**:
- 所有技术选型相互兼容
- Spring Boot 3.5.0 与 Java 17 完全兼容
- MyBatis-Plus 3.5.9 与 Spring Boot 3.5.0 兼容（需要 mybatis-spring 3.0.4+）
- Vue.js 3.4.x 与 TypeScript 5.x 配合良好
- JWT 认证与 Spring Security 集成成熟

**版本兼容性注意事项：**
- Spring Boot 3.5+ 需要 MyBatis-Plus 3.5.9+ 和 mybatis-spring 3.0.4+
- 原 MyBatis-Plus 3.5.5 与 Spring Boot 3.5.0 存在已知兼容性问题

**模式一致性**:
- 命名规范前后端统一
- 项目结构符合各技术栈最佳实践
- API 响应格式前后端约定一致

**结构对齐**:
- 项目结构支持所有架构决策
- 组件边界清晰，职责分离明确
- 集成点已充分定义

---

### 需求覆盖验证

**功能需求覆盖**:
- 成员管理: User 模块完整支持
- 内容发布: Content 模块完整支持
- 审核机制: Review 模块完整支持
- 转正系统: Promotion 模块完整支持

**非功能需求覆盖**:
- 安全性: JWT + RBAC + Spring Security
- 性能: 阶段性缓存策略
- 可维护性: 分层架构 + 一致性规则
- 可部署性: Docker + Docker Compose

---

### 实施准备度验证

**决策完整性**:
- 所有关键决策已文档化
- 技术版本已确认
- 实施模式已定义

**结构完整性**:
- 完整项目目录结构已定义
- 所有文件位置已明确
- 组件边界已清晰划分

**模式完整性**:
- 命名规范覆盖所有场景
- 错误处理模式已定义
- 状态管理模式已明确

---

### 差距分析

**关键差距**: 无

**重要差距**: 无

**可选改进** (MVP 后):
- 添加性能监控 (Micrometer + Prometheus)
- 添加分布式日志聚合 (ELK Stack)
- 添加 API 网关 (Spring Cloud Gateway)
- 数据备份策略 (MySQL 定时dump或主从复制)
- MapStruct 对象映射 (减少 DTO 转换代码)

---

### 架构完整性检查清单

**需求分析**:
- [x] 项目上下文已充分分析
- [x] 技术约束已识别
- [x] 横切关注点已映射

**架构决策**:
- [x] 关键决策已文档化
- [x] 技术栈已完全确定
- [x] 集成模式已定义

**实施模式**:
- [x] 命名规范已建立
- [x] 结构模式已定义
- [x] 通信模式已指定

**项目结构**:
- [x] 完整目录结构已定义
- [x] 组件边界已建立
- [x] 需求到结构映射完成

---

### 架构准备度评估

**整体状态**: 准备就绪，可开始实施

**信心水平**: 高

**关键优势**:
- 技术栈成熟稳定，社区支持好
- 架构清晰，学习曲线适中
- 分层明确，便于团队协作
- 部署简单，适合学校环境

**未来增强方向**:
- 性能监控和告警 (Micrometer + Prometheus)
- 自动化测试覆盖
- 缓存策略优化
- 日志聚合分析 (ELK Stack)
- 数据备份策略 (定时dump或主从复制)

---

### 实施交接

**AI 代理实施指南**:

1. **严格遵循架构决策**: 所有技术选型和设计决策必须按文档执行
2. **使用实施模式**: 命名规范、代码结构、API 格式必须一致
3. **尊重项目结构**: 新文件必须放在正确的目录位置
4. **参考架构文档**: 任何架构问题以本文档为最终依据

**首批实施任务**:

1. 初始化后端项目 (Spring Initializr)
2. 初始化前端项目 (Vite + Vue)
3. 配置 Docker Compose 环境
4. 实现用户认证模块
5. 实现内容管理模块

**初始化命令**:

```bash
# 后端初始化 (不包含 data-jpa，使用 MyBatis-Plus 替代)
curl https://start.spring.io/starter.zip \
  -d dependencies=web,mysql,security,validation,lombok,devtools \
  -d type=maven-project -d language=java \
  -d bootVersion=3.5.0 -d baseDir=backend -o backend.zip

# 解压后手动添加 MyBatis-Plus 和 Flyway 依赖到 pom.xml
# 注意：MyBatis-Plus 需使用 3.5.9+ 版本以兼容 Spring Boot 3.5.0

# 前端初始化
npm create vue@latest frontend
# 选择: TypeScript, Router, Pinia, ESLint, Prettier
```

---

## 总结

### 架构设计完成

本文档为沈阳工业大学计算机程序设计社团官方网站定义了完整的技术架构:

**技术栈**:
- 后端: Spring Boot 3.5.0 + Java 17 + MySQL 8.0
- 前端: Vue.js 3.4.x + TypeScript 5.x + Vite
- 部署: Docker + Docker Compose

**核心特性**:
- JWT 认证 + RBAC 授权
- RESTful API + 统一响应格式
- MyBatis-Plus 3.5.9 数据访问架构
- Pinia 模块化状态管理

**架构特点**:
- 分层清晰，职责明确
- 易于学习，适合学生团队
- 部署简单，维护成本低
- 可扩展，支持未来增强

---

**文档状态**: 完成
**最后更新**: 2026-04-03
**版本**: 1.0

