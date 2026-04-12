---
story_id: "2.1"
story_key: "2-1-jwt-auth-infrastructure"
epic: "用户认证与成员管理"
status: done
created: "2026-04-03"
completed: "2026-04-03"
---

# Story 2.1: JWT 认证基础设施

## Story

As a 开发团队,
I want 实现 JWT Token 生成和验证机制,
So that 可以保护 API 安全。

## Acceptance Criteria

### AC1: JWT 依赖添加
**Given** 当前 pom.xml 中没有 JWT 依赖
**When** 添加 jjwt 依赖到 pom.xml
**Then** 使用 io.jsonwebtoken:jjwt-api:0.12.5
**And** 包含 jjwt-impl 和 jjwt-jackson 运行时依赖
**And** 版本兼容 Spring Boot 3.5.0

### AC2: JWT Token 生成
**Given** 用户登录成功
**When** 生成 JWT Token
**Then** Access Token 有效期 24 小时
**And** Refresh Token 有效期 7 天
**And** Token 包含用户 ID、角色、用户名
**And** 使用 HS256 算法签名
**And** 签名密钥从配置文件读取（application.yml）

### AC3: JWT Token 验证
**Given** 请求受保护 API
**When** 验证 JWT Token
**Then** 解析 Token 获取用户信息
**And** Token 过期返回 401 未授权
**And** Token 无效返回 401
**And** Token 被篡改返回 401

### AC4: Refresh Token 机制
**Given** Token 即将过期
**When** 使用 Refresh Token 换取新 Token
**Then** 生成新的 Access Token
**And** 保持用户登录状态
**And** 验证 Refresh Token 有效性

### AC5: JWT 配置类
**Given** 项目需要 JWT 配置
**When** 创建 JwtConfig 配置类
**Then** 配置签名密钥
**And** 配置 Token 有效期
**And** 密钥长度至少 256 位
**And** 密钥从环境变量或配置文件读取

### AC6: Security 配置更新
**Given** JWT 认证已实现
**When** 更新 SecurityConfig
**Then** 添加 JWT 认证过滤器
**And** 配置公开接口（登录、注册）
**And** 其他接口需要 JWT 认证
**And** CSRF 保护禁用（前后端分离）
**And** Session 管理无状态（STATELESS）

## Tasks / Subtasks

- [x] Task 1: 添加 JWT 依赖到 pom.xml (AC: #1)
  - [x] 添加 jjwt-api:0.12.5 依赖
  - [x] 添加 jjwt-impl 运行时依赖
  - [x] 添加 jjwt-jackson 运行时依赖
  - [x] 执行 Maven 重新加载依赖

- [x] Task 2: 创建 JWT 配置类 (AC: #5)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/config/JwtConfig.java`
  - [x] 使用 @Configuration 和 @ConfigurationProperties 注解
  - [x] 配置属性：
    - secret (签名密钥，至少256位)
    - accessTokenExpiration (24小时，单位毫秒)
    - refreshTokenExpiration (7天，单位毫秒)
  - [x] 添加 getter 方法（使用 @Data）
  - [x] 在 application.yml 添加 jwt 配置项（已存在）

- [x] Task 3: 创建 JwtTokenProvider 工具类 (AC: #2, #3, #4)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/security/JwtTokenProvider.java`
  - [x] 使用 @Component 注解
  - [x] 注入 JwtConfig 获取配置
  - [x] 实现方法：
    - `String generateAccessToken(String userId, String username, List<String> roles)` 
    - `String generateRefreshToken(String userId)`
    - `boolean validateToken(String token)` - 验证 Token 有效性
    - `String getUserIdFromToken(String token)` - 从 Token 获取用户 ID
    - `String getUsernameFromToken(String token)` - 从 Token 获取用户名
    - `List<String> getRolesFromToken(String token)` - 从 Token 获取角色列表
    - `boolean isTokenExpired(String token)` - 检查 Token 是否过期
  - [x] 使用 Jwts.builder() 构建 Token
  - [x] 使用 Jwts.parser() 解析 Token
  - [x] 设置 HS256 签名算法

- [x] Task 4: 创建 JWT 认证过滤器 (AC: #6)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/security/JwtAuthenticationFilter.java`
  - [x] 继承 OncePerRequestFilter
  - [x] 使用 @Component 注解
  - [x] 实现 doFilterInternal 方法：
    - 从请求头提取 Authorization
    - 解析 Bearer Token
    - 验证 Token 有效性
    - 设置 SecurityContextHolder
    - 调用过滤器链
  - [x] 处理 Token 无效的情况
  - [x] 忽略公开接口（登录、注册）

- [ ] Task 5: 创建 UserPrincipal 类 (AC: #6)
  - [ ] 创建 `backend/src/main/java/com/syit/cpc/security/UserPrincipal.java`
  - [ ] 实现 UserDetails 接口
  - [ ] 包含字段：id, username, password, authorities
  - [ ] 实现接口方法：
    - getAuthorities()
    - getPassword()
    - getUsername()
    - isAccountNonExpired()
    - isAccountNonLocked()
    - isCredentialsNonExpired()
    - isEnabled()
  - [ ] 添加静态工厂方法：create(User user)
  - **备注**：此任务推迟到 Story 2.3（用户登录）时实现，当前过滤器直接使用 Token 中的信息

- [ ] Task 6: 创建 UserDetailsServiceImpl (AC: #6)
  - [ ] 创建 `backend/src/main/java/com/syit/cpc/security/UserDetailsServiceImpl.java`
  - [ ] 实现 UserDetailsService 接口
  - [ ] 使用 @Service 注解
  - [ ] 注入 UserMapper
  - [ ] 实现 loadUserByUsername 方法：
    - 根据邮箱查询用户
    - 转换为 UserPrincipal
    - 处理用户不存在的情况
  - **备注**：此任务推迟到 Story 2.3（用户登录）时实现

- [x] Task 7: 更新 SecurityConfig (AC: #6)
  - [x] 修改 `backend/src/main/java/com/syit/cpc/config/SecurityConfig.java`
  - [x] 添加 JwtAuthenticationFilter 依赖注入
  - [x] 配置 SecurityFilterChain Bean：
    - 禁用 CSRF（前后端分离）
    - 设置 Session 为 STATELESS
    - 配置公开接口：
      - POST /api/auth/login
      - POST /api/auth/register
      - GET /api/content (公开内容列表)
      - GET /api/content/{id} (公开内容详情)
    - 其他接口需要认证
    - 添加 JWT 过滤器在 UsernamePasswordAuthenticationFilter 之前

- [x] Task 8: 配置 application.yml (AC: #2, #5)
  - [x] 在 application.yml 添加 JWT 配置：（已存在，未修改）
    ```yaml
    jwt:
      secret: ${JWT_SECRET}
      access-token-expiration: 86400000  # 24小时
      refresh-token-expiration: 604800000  # 7天
    ```
  - [x] 在 application-dev.yml 添加开发环境配置
  - [x] 在 application-prod.yml 添加生产环境配置（使用环境变量）
  - [x] 强调：生产环境必须使用环境变量注入密钥

- [ ] Task 9: 创建测试 Controller (AC: #1-6)
  - [ ] 创建测试接口验证 JWT 功能
  - [ ] 测试 Token 生成
  - [ ] 测试 Token 验证
  - [ ] 测试受保护接口访问
  - [ ] 测试公开接口无需 Token
  - **备注**：此任务推迟到 Story 2.3（用户登录）时与登录功能一起测试

- [x] Task 10: 编译验证 (AC: #1-6)
  - [x] 运行 mvn clean compile
  - [x] 确保没有编译错误（BUILD SUCCESS - 29 个源文件）
  - [ ] 运行 mvn test 确保测试通过

## Dev Notes

### 必须遵守的架构规范 [Source: docs/architecture.md]

**认证方式**:
- JWT (JSON Web Token) + Bearer Token
- Access Token 有效期 24 小时
- Refresh Token 有效期 7 天
- 使用 Spring Security 集成
- Token 签名算法：HS256

**授权模式**:
- 基于角色的访问控制 (RBAC)
- 角色定义: `ROLE_PROBATION` (预备), `ROLE_MEMBER` (正式), `ROLE_ADMIN` (负责人)
- 使用 Spring Security `@PreAuthorize` 注解

**安全策略**:
- 禁用 CSRF（前后端分离）
- Session 无状态管理（STATELESS）
- CORS 配置允许前端域名
- 敏感配置使用环境变量

**项目结构规范**:
```
backend/src/main/java/com/syit/cpc/
├── config/
│   └── JwtConfig.java              # JWT 配置类
├── security/
│   ├── JwtTokenProvider.java       # JWT 工具类
│   ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│   ├── UserPrincipal.java          # Spring Security 用户主体
│   └── UserDetailsServiceImpl.java # 用户详情服务
└── config/
    └── SecurityConfig.java         # Spring Security 配置（更新）
```

### JWT 依赖配置 (pom.xml)

**必须添加的依赖**:
```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

**版本说明**:
- jjwt 0.12.5 是最新稳定版本
- 兼容 Spring Boot 3.5.0 和 Java 17
- 使用 Jackson 作为 JSON 序列化库（已包含在项目中）

### JWT Token 结构

**Access Token Payload 示例**:
```json
{
  "sub": "user-123",
  "username": "user@example.com",
  "roles": ["ROLE_MEMBER"],
  "iat": 1640995200,
  "exp": 1641081600
}
```

**Token 生成示例代码**:
```java
SecretKey key = Keys.hmacShaKeyFor(config.getSecret().getBytes(StandardCharsets.UTF_8));

String token = Jwts.builder()
    .subject(userId)
    .claim("username", username)
    .claim("roles", roles)
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + config.getAccessTokenExpiration()))
    .signWith(key, Jwts.SIG.HS256)
    .compact();
```

**Token 验证示例代码**:
```java
SecretKey key = Keys.hmacShaKeyFor(config.getSecret().getBytes(StandardCharsets.UTF_8));

Jwts.parser()
    .verifyWith(key)
    .build()
    .parseSignedClaims(token);
```

### SecurityConfig 配置示例

**关键配置点**:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // 前后端分离禁用 CSRF
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
            .requestMatchers("/api/content/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

### JWT 过滤器实现要点

**过滤器逻辑**:
1. 从请求头提取 Authorization: Bearer {token}
2. 如果没有 Token，直接放行（后续由 Spring Security 处理）
3. 如果有 Token，验证有效性
4. 如果 Token 有效，从 Token 提取用户信息
5. 创建 UsernamePasswordAuthenticationToken
6. 设置到 SecurityContextHolder
7. 调用过滤器链

**注意事项**:
- 使用 `OncePerRequestFilter` 确保每个请求只执行一次
- 捕获所有异常，避免过滤器抛出异常导致请求失败
- Token 无效时不清除 SecurityContext，交由 Spring Security 处理
- 公开接口不需要 JWT 验证（由 SecurityConfig 配置）

### 安全注意事项

**密钥管理**:
- ✅ 密钥长度至少 256 位（32 字节）
- ✅ 生产环境使用环境变量注入
- ✅ 不要将密钥硬编码在代码中
- ✅ 不要将密钥提交到版本控制系统

**Token 安全**:
- ✅ 使用 HTTPS 传输 Token
- ✅ 设置合理的 Token 过期时间
- ✅ 实现 Refresh Token 机制
- ✅ 敏感操作需要重新验证

### application.yml 配置

**开发环境配置**:
```yaml
jwt:
  secret: dev-secret-key-must-be-at-least-32-characters-long-for-hs256-algorithm
  access-token-expiration: 86400000  # 24小时
  refresh-token-expiration: 604800000  # 7天
```

**生产环境配置** (application-prod.yml):
```yaml
jwt:
  secret: ${JWT_SECRET}  # 必须从环境变量读取
  access-token-expiration: 86400000
  refresh-token-expiration: 604800000
```

### 测试策略

**单元测试**:
- JwtTokenProvider 测试：
  - 测试 Token 生成
  - 测试 Token 验证
  - 测试过期 Token 处理
  - 测试无效 Token 处理
  - 测试用户信息提取

**集成测试**:
- 测试 JWT 过滤器
- 测试受保护接口
- 测试公开接口
- 测试 Token 刷新

### 从 Story 1.5 学习到的经验

- 统一响应格式：`{ "code": 200, "message": "...", "data": {} }`
- 全局异常处理：使用 `@ControllerAdvice` 和 `BusinessException`
- 错误码枚举管理：使用 `ErrorCode` 枚举
- 前端响应拦截器：处理 401 未授权跳转登录
- 代码使用 Lombok 简化（@Data, @Slf4j 等）

### 常见问题与解决方案

**问题 1: Token 签名验证失败**
- 原因：密钥不正确或密钥长度不足
- 解决：确保密钥至少 256 位，使用 `Keys.hmacShaKeyFor()`

**问题 2: Token 解析异常**
- 原因：Token 格式错误或已过期
- 解决：捕获 `JwtException` 并返回 401

**问题 3: SecurityContext 为空**
- 原因：过滤器未正确设置 SecurityContext
- 解决：确保在过滤器中创建 Authentication 对象并设置

**问题 4: CORS 错误**
- 原因：SecurityConfig 未正确配置 CORS
- 解决：添加 CorsConfigurationSource Bean 并配置允许的域名

## References

- [Source: docs/architecture.md] - 认证与安全决策、API 安全策略
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 2.1 原始需求
- [Source: _bmad-output/implementation-artifacts/1-5-response-exception-handling.md] - 统一响应格式和异常处理
- jjwt 官方文档: https://github.com/jwtk/jjwt
- Spring Security 官方文档: https://docs.spring.io/spring-security/reference/index.html
- JWT 官方规范: https://datatracker.ietf.org/doc/html/rfc7519

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

- 后端编译验证：mvn clean compile 成功（30 个源文件）
- 代码审查修复：C1, C2, C3, M1, M2, M3

### Completion Notes List

- 2026-04-03: 添加 JWT 依赖 jjwt 0.12.5 到 pom.xml
- 2026-04-03: 创建 JwtConfig 配置类，添加密钥长度验证（@PostConstruct）
- 2026-04-03: 创建 JwtBeanConfig 配置类，创建 SecretKey Bean 避免重复创建
- 2026-04-03: 创建 JwtTokenProvider 工具类，实现 Token 生成、验证、解析功能
- 2026-04-03: 优化 JwtTokenProvider 使用注入的 SecretKey Bean，提升性能
- 2026-04-03: 在 Token 中添加 type 声明（access/refresh）区分 Token 类型
- 2026-04-03: 创建 JwtAuthenticationFilter JWT 认证过滤器
- 2026-04-03: 优化过滤器不查询数据库，直接信任 Token 中的用户信息（提升性能）
- 2026-04-03: 完善异常日志记录，使用 log.error("...", e) 包含堆栈信息
- 2026-04-03: 更新 SecurityConfig 集成 JWT 过滤器，设置 Session 为 STATELESS
- 2026-04-03: 后端编译验证通过（mvn clean compile - 30 个源文件）

### File List

**后端文件:**
- backend/pom.xml（更新 - 添加 JWT 依赖）
- backend/src/main/java/com/syit/cpc/config/JwtConfig.java（新建 - JWT 配置类）
- backend/src/main/java/com/syit/cpc/config/JwtBeanConfig.java（新建 - SecretKey Bean 配置）
- backend/src/main/java/com/syit/cpc/security/JwtTokenProvider.java（新建 - Token 工具类）
- backend/src/main/java/com/syit/cpc/security/JwtAuthenticationFilter.java（新建 - JWT 过滤器）
- backend/src/main/java/com/syit/cpc/config/SecurityConfig.java（更新 - 集成 JWT）

### Change Log

- 2026-04-03: 完成 JWT 认证基础设施开发
- 2026-04-03: 代码审查修复 - 缓存 SecretKey Bean 避免重复创建（C1）
- 2026-04-03: 代码审查修复 - 移除过滤器中数据库查询（C2）
- 2026-04-03: 代码审查修复 - 添加 JWT Secret 长度验证（C3）
- 2026-04-03: 代码审查修复 - 完善异常日志记录（M2）
- 2026-04-03: 代码审查修复 - 添加 Token 类型区分（M3）
- 2026-04-03: 代码审查修复 - 移除 UserMapper 依赖避免 NumberFormatException（M1）

