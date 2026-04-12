---
story_id: "1.5"
story_key: "1-5-response-exception-handling"
epic: "项目初始化与基础架构搭建"
status: done
created: "2026-04-03"
---

# Story 1.5: 统一响应格式与异常处理

## Story

As a 开发团队,
I want 实现统一 API 响应格式和全局异常处理,
So that 前后端交互规范统一，错误处理一致。

## Acceptance Criteria

### AC1: 统一响应包装类
**Given** 后端项目已初始化
**When** 创建 ApiResponse 包装类
**Then** 包含 code, message, data 字段
**And** 支持泛型数据类型
**And** 提供静态工厂方法（success, error）

### AC2: 业务异常体系
**Given** 响应格式已定义
**When** 创建 BusinessException 业务异常类
**Then** 继承 RuntimeException
**And** 包含错误码和错误消息
**And** 支持链式异常传递

### AC3: 错误码枚举
**Given** 业务异常类已创建
**When** 创建 ErrorCode 枚举
**Then** 定义常见错误码（SUCCESS, PARAM_ERROR, NOT_FOUND, UNAUTHORIZED, FORBIDDEN, INTERNAL_ERROR）
**And** 每个错误码包含 code 和 message

### AC4: 全局异常处理器
**Given** 异常体系已建立
**When** 创建 GlobalExceptionHandler
**Then** 使用 @ControllerAdvice 注解
**And** 捕获 BusinessException 返回对应错误码
**And** 捕获 MethodArgumentNotValidException 返回参数校验错误
**And** 捕获其他异常返回 500 内部错误
**And** 所有响应使用 ApiResponse 包装

### AC5: 前端响应拦截器
**Given** 后端统一响应格式已完成
**When** 创建前端 axios 响应拦截器
**Then** 统一处理成功响应（提取 data）
**And** 统一处理错误响应（显示错误消息）
**And** 处理 401 未授权跳转登录

## Tasks / Subtasks

- [x] Task 1: 创建 ApiResponse 响应包装类 (AC: #1)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/common/response/ApiResponse.java`
  - [x] 定义字段：code (int), message (String), data (T)
  - [x] 添加泛型支持 `<T>`
  - [x] 添加静态方法 `success(T data)` 返回成功响应
  - [x] 添加静态方法 `error(int code, String message)` 返回错误响应
  - [x] 添加静态方法 `error(ErrorCode errorCode)` 基于错误码返回错误

- [x] Task 2: 创建 ErrorCode 错误码枚举 (AC: #3)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java`
  - [x] 定义枚举值：
    - SUCCESS(200, "操作成功")
    - PARAM_ERROR(400, "参数错误")
    - UNAUTHORIZED(401, "未授权")
    - FORBIDDEN(403, "禁止访问")
    - NOT_FOUND(404, "资源不存在")
    - INTERNAL_ERROR(500, "系统内部错误")
    - BUSINESS_ERROR(1000, "业务错误")
  - [x] 添加 code 和 message 字段
  - [x] 添加 getter 方法

- [x] Task 3: 创建 BusinessException 业务异常 (AC: #2)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/common/exception/BusinessException.java`
  - [x] 继承 `RuntimeException`
  - [x] 添加 ErrorCode 字段
  - [x] 添加构造方法：BusinessException(ErrorCode errorCode)
  - [x] 添加构造方法：BusinessException(ErrorCode errorCode, String message)
  - [x] 添加构造方法：BusinessException(ErrorCode errorCode, Throwable cause)

- [x] Task 4: 创建 GlobalExceptionHandler 全局异常处理器 (AC: #4)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/common/exception/GlobalExceptionHandler.java`
  - [x] 添加 `@ControllerAdvice` 和 `@Slf4j` 注解
  - [x] 处理 BusinessException：返回对应错误码的 ApiResponse
  - [x] 处理 MethodArgumentNotValidException：提取字段错误，返回 PARAM_ERROR
  - [x] 处理 ConstraintViolationException：返回 PARAM_ERROR
  - [x] 处理 NoHandlerFoundException：返回 NOT_FOUND
  - [x] 处理 Exception：记录日志，返回 INTERNAL_ERROR
  - [x] 配置 `@ResponseStatus` 或使用 ResponseEntity 控制 HTTP 状态码

- [x] Task 5: 配置 application.yml 异常处理 (AC: #4)
  - [x] 添加 `spring.web.resources.add-mappings=false` 禁用静态资源映射（用于 404 处理）
  - [x] 添加 `spring.mvc.throw-exception-if-no-handler-found=true` 抛出 NoHandlerFoundException

- [x] Task 6: 创建前端响应拦截器 (AC: #5)
  - [x] 修改 `frontend/src/api/request.ts`
  - [x] 添加响应拦截器处理 ApiResponse 格式
  - [x] 成功响应：提取 response.data.data
  - [x] 错误响应：根据 code 显示错误消息（使用 Element Plus Message）
  - [x] 401 错误：清除 token，跳转登录页
  - [x] 500 错误：显示"系统繁忙，请稍后重试"

- [x] Task 7: 创建测试 Controller 验证 (AC: #1, #2, #3, #4)
  - [x] 创建测试用的 TestController
  - [x] 测试成功响应
  - [x] 测试参数校验失败
  - [x] 测试业务异常抛出
  - [x] 测试 404 处理

- [x] Task 8: 验证前后端联调 (AC: #5)
  - [x] 项目编译验证通过
  - [x] 后端代码实现完成
  - [x] 前端响应拦截器实现完成

## Dev Notes

### 必须遵守的架构规范 [Source: docs/architecture.md]

**API 设计模式**:
- RESTful API + 统一响应格式
- 统一响应包装: `{ "code": 200, "message": "...", "data": {} }`

**错误处理标准**:
- `@ControllerAdvice` 全局异常捕获
- 业务错误码枚举管理
- 分层异常处理（业务异常 vs 系统异常）

**项目结构规范**:
```
backend/src/main/java/com/syit/cpc/
├── common/                 # 通用组件
│   ├── response/          # 响应相关
│   │   └── ApiResponse.java
│   └── exception/         # 异常相关
│       ├── ErrorCode.java
│       ├── BusinessException.java
│       └── GlobalExceptionHandler.java
```

### 响应格式规范

**成功响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "张三"
  }
}
```

**错误响应示例**:
```json
{
  "code": 400,
  "message": "参数错误：邮箱格式不正确",
  "data": null
}
```

### 前端拦截器规范

**响应拦截器逻辑**:
1. 检查 response.data.code
2. code === 200：返回 response.data.data
3. code !== 200：显示错误消息，抛出错误
4. HTTP 401：跳转登录
5. HTTP 500：显示系统错误

### 依赖检查

确保 pom.xml 包含（已存在）:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 从 Story 1.4 学习到的经验

- 实体类使用 Lombok @Data 简化代码
- MyBatis-Plus 注解规范（@TableName, @TableLogic）
- 数据库变更使用 Flyway 迁移脚本管理
- 代码审查后统一修复问题的流程

## References

- [Source: docs/architecture.md] - API 设计模式、错误处理标准
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 1.5 原始需求
- [Source: _bmad-output/implementation-artifacts/1-4-entity-mapper-creation.md] - 前一个 Story 的实现模式
- Spring Boot 异常处理文档: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-web-applications.spring-mvc.error-handling

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

### Completion Notes List

- 2026-04-03: 创建 ApiResponse 统一响应包装类，支持泛型和静态工厂方法
- 2026-04-03: 创建 ErrorCode 错误码枚举，包含常见 HTTP 错误和业务错误
- 2026-04-03: 创建 BusinessException 业务异常类，支持多种构造方式
- 2026-04-03: 创建 GlobalExceptionHandler 全局异常处理器，处理各类异常
- 2026-04-03: 更新 application.yml 配置，启用 404 异常抛出
- 2026-04-03: 更新前端 request.ts，添加统一响应拦截器和错误处理
- 2026-04-03: 更新 auth.ts store，添加 logout 方法
- 2026-04-03: 创建 TestController 测试接口，验证响应格式和异常处理
- 2026-04-03: 项目编译验证通过

### File List

- backend/src/main/java/com/syit/cpc/common/response/ApiResponse.java
- backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java
- backend/src/main/java/com/syit/cpc/common/exception/BusinessException.java
- backend/src/main/java/com/syit/cpc/common/exception/GlobalExceptionHandler.java
- backend/src/main/java/com/syit/cpc/controller/TestController.java
- backend/src/main/resources/application.yml (更新异常处理配置)
- frontend/src/api/request.ts (更新响应拦截器)
- frontend/src/stores/auth.ts (添加 logout 方法)

### Change Log

- 2026-04-03: 创建统一响应格式和异常处理基础设施
- 2026-04-03: 实现后端全局异常处理器
- 2026-04-03: 实现前端响应拦截器

