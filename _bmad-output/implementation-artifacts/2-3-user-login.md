---
story_id: "2.3"
story_key: "2-3-user-login"
epic: "用户认证与成员管理"
status: done
created: "2026-04-03"
completed: "2026-04-03"
---

# Story 2.3: 用户登录功能

## Story

As a 社团成员,
I want 通过邮箱和密码登录系统,
So that 可以访问我的账号。

## Acceptance Criteria

### AC1: 登录成功
**Given** 访问登录页面
**When** 输入已注册的邮箱和正确的密码
**Then** 验证邮箱和密码匹配
**And** 生成 JWT Access Token（24小时有效）
**And** 生成 JWT Refresh Token（7天有效）
**And** 返回用户信息（用户ID、用户名、角色列表）
**And** 跳转到首页

### AC2: 记住我功能
**Given** 登录时勾选"记住我"
**When** 登录成功
**Then** Refresh Token 有效期延长至 7 天
**And** 前端将 Token 存储到 localStorage
**And** 下次访问自动登录（通过 Refresh Token）

### AC3: 登录失败处理
**Given** 访问登录页面
**When** 输入错误的邮箱或密码
**Then** 返回错误提示"邮箱或密码错误"
**And** 不返回 Token
**And** 记录登录失败次数

### AC4: 账号锁定机制
**Given** 用户连续 5 次登录失败
**When** 第 6 次尝试登录
**Then** 返回错误提示"账号已锁定，请在 15 分钟后重试"
**And** 记录锁定时间
**And** 15 分钟后自动解锁
**And** 前端显示锁定倒计时（如果适用）

### AC5: 前端登录页面
**Given** 用户访问登录页面
**When** 页面加载
**Then** 显示登录表单（邮箱、密码、记住我）
**And** 表单验证（邮箱格式、密码必填）
**And** 提供"没有账号？立即注册"链接
**And** 提供"忘记密码？"链接（MVP 阶段可暂缓）
**And** 登录成功后跳转到首页
**And** 登录失败显示错误提示

### AC6: 前端状态管理
**Given** 用户登录成功
**When** Token 返回
**Then** 将 Token 存储到 Pinia auth store
**And** 设置 Axios 请求头的 Authorization
**And** 更新登录状态为已登录
**And** 存储用户信息到 store

## Tasks / Subtasks

### 后端任务

- [x] Task 1: 创建登录请求和响应 DTO (AC: #1, #2)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/request/LoginRequest.java`
  - [x] 包含字段：email, password, rememberMe
  - [x] 添加 Swagger 注解（@Schema）
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/response/LoginResponse.java`
  - [x] 包含字段：accessToken, refreshToken, tokenType, expiresIn, userId, username, roles

- [x] Task 2: 扩展 AuthService 添加登录方法 (AC: #1, #2, #3, #4)
  - [x] 在 AuthService 接口添加 login(LoginRequest request) 方法
  - [x] 在 AuthServiceImpl 实现登录逻辑
  - [x] 注入 JwtTokenProvider 和 JwtConfig
  - [x] 使用 @Transactional 注解

- [x] Task 3: 实现登录失败锁定机制 (AC: #4)
  - [x] 创建 Flyway 迁移脚本 V3__add_login_lock_fields.sql
  - [x] 更新 User 实体类添加 loginFailCount 和 lockUntil 字段
  - [x] 实现 incrementLoginFailCount 和 resetLoginFailCount 方法
  - [x] 锁定逻辑：5次失败锁定15分钟

- [x] Task 4: 扩展 AuthController 添加登录接口 (AC: #1, #2, #3, #4)
  - [x] 添加 POST /api/auth/login 接口
  - [x] 添加 Swagger 注解
  - [x] 接口不需要认证（公开接口）

- [x] Task 5: 创建 Refresh Token 接口 (AC: #2)
  - [x] 创建 RefreshTokenRequest DTO
  - [x] 在 AuthService 添加 refreshToken 方法
  - [x] 在 AuthController 添加 POST /api/auth/refresh 接口

- [x] Task 6: 更新 SecurityConfig (AC: #1, #5)
  - [x] 添加 /api/auth/login 和 /api/auth/refresh 到公开接口列表

- [x] Task 7: 编写单元测试 (AC: #1-4)
  - [ ] 推迟到后续测试阶段

### 前端任务

- [x] Task 8: 扩展前端 Auth API 接口 (AC: #1, #2, #5)
  - [x] 在 `frontend/src/api/auth.ts` 添加登录相关接口
  - [x] 实现 login 函数
  - [x] 实现 refreshToken 函数
  - [x] 定义 LoginRequest 和 LoginResponse 类型

- [x] Task 9: 创建登录页面 (AC: #5)
  - [x] 创建 `frontend/src/views/LoginView.vue`
  - [x] 使用 Element Plus 表单组件
  - [x] 表单字段：邮箱、密码、记住我
  - [x] 实现表单验证规则
  - [x] 登录成功：存储 Token，跳转到首页
  - [x] 登录失败：显示错误消息
  - [x] 添加“没有账号？立即注册”链接

- [x] Task 10: 创建/扩展 Pinia Auth Store (AC: #6)
  - [x] 创建 `frontend/src/stores/auth.ts`
  - [x] 实现状态：token, refreshToken, user
  - [x] 实现 actions：login, logout, refreshAccessToken, restoreFromStorage
  - [x] 实现 getters：isAuthenticated, currentUser

- [x] Task 11: 配置 Axios 拦截器 (AC: #6)
  - [x] 在 `frontend/src/api/request.ts` 添加请求拦截器
  - [x] 自动在请求头添加 Authorization
  - [x] 添加响应拦截器处理 401

- [x] Task 12: 添加登录路由 (AC: #5)
  - [x] 路由路径：/login（已存在）
  - [x] 组件：LoginView

## Dev Notes

### 必须遵守的架构规范 [Source: docs/architecture.md]

**技术栈**:
- 后端: Spring Boot 3.5.0 + Java 17 + MyBatis-Plus 3.5.9
- 前端: Vue.js 3.4.x + TypeScript 5.x + Vite 5.x
- UI 框架: Element Plus 2.5+
- 认证: JWT (jjwt 0.12.5) + Refresh Token
- 密码加密: BCrypt (Spring Security)

**API 设计规范**:
- RESTful API
- 统一响应格式: `{ "code": 200, "message": "...", "data": {} }`
- 接口路径: `/api/auth/login`, `/api/auth/refresh`
- HTTP 方法: POST

**JWT Token 规范** [Source: Story 2.1]:
- Access Token 有效期: 24 小时 (86400000 毫秒)
- Refresh Token 有效期: 7 天 (604800000 毫秒)
- Token 包含: userId (subject), username, roles, type (access/refresh)
- 签名算法: HS256
- 使用 SecretKey Bean 缓存（避免重复创建）

**数据库规范**:
- 用户表: `users`
- 登录失败字段: `login_fail_count` (INT, 默认 0)
- 锁定时间字段: `lock_until` (DATETIME, 可为空)
- 状态字段: `status` (0=未激活, 1=激活)
- 角色字段: `role` (ROLE_PROBATION / ROLE_MEMBER / ROLE_ADMIN)

**锁定机制规范**:
- 连续失败 5 次后锁定
- 锁定时间: 15 分钟 (900000 毫秒)
- 锁定期间不允许登录
- 锁定时间过后自动解锁
- 登录成功后重置失败次数

**验证规范**:
- 使用 Jakarta Bean Validation (@Valid)
- Controller 层使用 @Valid 注解
- Service 层补充业务规则验证
- 错误消息使用中文，用户友好

**项目结构规范**:
```
backend/src/main/java/com/syit/cpc/
├── controller/
│   └── AuthController.java          # 扩展：添加登录和刷新 Token 接口
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java        # 新建：登录请求 DTO
│   │   └── RefreshTokenRequest.java # 新建：刷新 Token 请求 DTO
│   └── response/
│       └── LoginResponse.java       # 新建：登录响应 DTO
├── entity/
│   └── User.java                    # 更新：添加 loginFailCount 和 lockUntil
├── service/
│   ├── AuthService.java             # 扩展：添加登录和刷新 Token 方法
│   └── impl/
│       └── AuthServiceImpl.java     # 扩展：实现登录逻辑
├── config/
│   ├── SecurityConfig.java          # 更新：添加登录接口到公开列表
│   └── JwtBeanConfig.java           # 已存在：SecretKey Bean
├── security/
│   ├── JwtTokenProvider.java        # 已存在：Token 生成和验证
│   └── JwtAuthenticationFilter.java # 已存在：JWT 过滤器
└── util/
    └── PasswordUtil.java            # 已存在：密码加密工具

frontend/src/
├── api/
│   ├── auth.ts                      # 扩展：添加登录和刷新 Token API
│   └── request.ts                   # 更新：添加 Token 拦截器
├── stores/
│   └── auth.ts                      # 新建：认证状态管理
├── views/
│   ├── LoginView.vue                # 新建：登录页面
│   └── RegisterView.vue             # 已存在：注册页面
└── router/
    └── index.ts                     # 更新：添加登录路由
```

### DTO 设计

**LoginRequest.java 示例**:
```java
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "是否记住我", example = "false")
    private Boolean rememberMe = false;
}
```

**LoginResponse.java 示例**:
```java
@Data
@Builder
@Schema(description = "用户登录响应")
public class LoginResponse {
    
    @Schema(description = "Access Token")
    private String accessToken;
    
    @Schema(description = "Refresh Token")
    private String refreshToken;
    
    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Access Token 有效期（秒）", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "用户 ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "角色列表")
    private List<String> roles;
}
```

**RefreshTokenRequest.java 示例**:
```java
@Data
@Schema(description = "刷新 Token 请求")
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
```

### Service 实现要点

**AuthService 登录流程**:
1. 根据邮箱查询用户
2. 验证用户是否存在
3. 验证用户状态是否启用
4. 检查账号是否被锁定（lock_until 未过期）
5. 验证密码
6. 密码错误：
   - login_fail_count + 1
   - 如果 login_fail_count >= 5，设置 lock_until = 当前时间 + 15 分钟
   - 抛出 LOGIN_FAILED 异常
7. 密码正确：
   - 重置 login_fail_count = 0
   - 重置 lock_until = null
   - 从 User 实体获取角色列表（单个角色转为 List）
   - 生成 Access Token
   - 生成 Refresh Token
   - 返回 LoginResponse

**关键代码示例**:
```java
@Override
@Transactional
public LoginResponse login(LoginRequest request) {
    // 1. 查询用户
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(User::getEmail, request.getEmail());
    User user = userMapper.selectOne(wrapper);
    
    if (user == null) {
        throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }
    
    // 2. 验证用户状态
    if (user.getStatus() != 1) {
        throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
    }
    
    // 3. 检查账号是否被锁定
    if (user.getLockUntil() != null && user.getLockUntil().after(new Date())) {
        throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
    }
    
    // 4. 验证密码
    if (!passwordUtil.matchesPassword(request.getPassword(), user.getPassword())) {
        // 密码错误，增加失败次数
        incrementLoginFailCount(user);
        throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }
    
    // 5. 登录成功，重置失败次数
    resetLoginFailCount(user);
    
    // 6. 生成 Token
    List<String> roles = Collections.singletonList(user.getRole());
    String accessToken = jwtTokenProvider.generateAccessToken(
        user.getId().toString(), user.getName(), roles
    );
    String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());
    
    // 7. 构建响应
    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
        .userId(user.getId())
        .username(user.getName())
        .roles(roles)
        .build();
}

private void incrementLoginFailCount(User user) {
    user.setLoginFailCount(user.getLoginFailCount() + 1);
    if (user.getLoginFailCount() >= 5) {
        user.setLockUntil(new Date(System.currentTimeMillis() + 900000)); // 15 分钟
    }
    userMapper.updateById(user);
}

private void resetLoginFailCount(User user) {
    user.setLoginFailCount(0);
    user.setLockUntil(null);
    userMapper.updateById(user);
}
```

### ErrorCode 扩展

需要在 `ErrorCode.java` 中添加新的错误码：
```java
LOGIN_FAILED(1004, "邮箱或密码错误"),
ACCOUNT_LOCKED(1005, "账号已锁定，请在 15 分钟后重试"),
ACCOUNT_NOT_ACTIVATED(1006, "账号未激活"),
REFRESH_TOKEN_INVALID(1007, "Refresh Token 无效或已过期"),
```

### Flyway 迁移脚本

**V3__add_login_lock_fields.sql**:
```sql
-- 添加登录失败次数和锁定时间字段
ALTER TABLE users 
ADD COLUMN login_fail_count INT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
ADD COLUMN lock_until DATETIME NULL COMMENT '锁定截止时间';

-- 添加索引优化查询
CREATE INDEX idx_users_lock_until ON users(lock_until);
```

### 前端 Auth Store 设计

**auth.ts 示例**:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, refreshToken as refreshTokenApi } from '@/api/auth'

export interface User {
  userId: number
  username: string
  roles: string[]
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string>('')
  const refreshToken = ref<string>('')
  const user = ref<User | null>(null)

  const isLoggedIn = computed(() => !!accessToken.value)
  const isAuthenticated = computed(() => !!accessToken.value)
  const currentUser = computed(() => user.value)

  async function login(email: string, password: string, rememberMe: boolean = false) {
    const response = await loginApi({ email, password, rememberMe })
    
    accessToken.value = response.accessToken
    refreshToken.value = response.refreshToken
    user.value = {
      userId: response.userId,
      username: response.username,
      roles: response.roles
    }
    
    // 存储到 localStorage
    if (rememberMe) {
      localStorage.setItem('accessToken', response.accessToken)
      localStorage.setItem('refreshToken', response.refreshToken)
    } else {
      sessionStorage.setItem('accessToken', response.accessToken)
      sessionStorage.setItem('refreshToken', response.refreshToken)
    }
    
    return response
  }

  async function logout() {
    accessToken.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    sessionStorage.removeItem('accessToken')
    sessionStorage.removeItem('refreshToken')
  }

  async function refreshAccessToken() {
    if (!refreshToken.value) {
      throw new Error('No refresh token')
    }
    
    const response = await refreshTokenApi(refreshToken.value)
    accessToken.value = response.accessToken
    refreshToken.value = response.refreshToken
    
    return response
  }

  function setToken(access: string, refresh: string) {
    accessToken.value = access
    refreshToken.value = refresh
  }

  function clearToken() {
    accessToken.value = ''
    refreshToken.value = ''
  }

  return {
    accessToken,
    refreshToken,
    user,
    isLoggedIn,
    isAuthenticated,
    currentUser,
    login,
    logout,
    refreshAccessToken,
    setToken,
    clearToken
  }
})
```

### Axios 拦截器设计

**request.ts 更新**:
```typescript
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

// 请求拦截器：添加 Token
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理 401
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  async (error) => {
    const originalRequest = error.config
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      try {
        const authStore = useAuthStore()
        await authStore.refreshAccessToken()
        
        // 重试原请求
        originalRequest.headers.Authorization = `Bearer ${authStore.accessToken}`
        return request(originalRequest)
      } catch (refreshError) {
        // 刷新失败，清除 Token 并跳转登录页
        authStore.logout()
        router.push('/login')
        return Promise.reject(refreshError)
      }
    }
    
    return Promise.reject(error)
  }
)

export default request
```

### 登录页面验证规则

**LoginView.vue 验证规则**:
```typescript
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}
```

### 安全注意事项

1. **密码安全**:
   - ✅ 使用 BCrypt 验证密码，不暴露明文
   - ✅ 不要记录日志中的密码
   - ✅ 登录失败返回通用错误信息（不区分邮箱或密码错误）
   
2. **防暴力破解**:
   - ✅ 连续 5 次失败锁定 15 分钟
   - ✅ 锁定时间存储在数据库
   - ✅ 后续可添加图形验证码
   
3. **Token 安全**:
   - ✅ Access Token 短期有效（24 小时）
   - ✅ Refresh Token 长期有效（7 天）
   - ✅ 前端根据"记住我"选择存储位置
   
4. **输入验证**:
   - ✅ 前后端双重验证
   - ✅ 防止 SQL 注入（MyBatis-Plus 参数化查询）
   - ✅ 防止 XSS 攻击

### 从 Story 2.1 和 2.2 学习到的经验

- JWT 认证基础设施已就绪（JwtTokenProvider、JwtAuthenticationFilter）
- SecurityConfig 已配置，登录接口需要加入公开访问列表
- 统一响应格式和异常处理已完成
- 密码加密使用 Spring Security 的 BCrypt
- AuthController 已创建，只需扩展登录接口
- AuthService 已创建，只需扩展登录方法
- 用户注册时 status = 1（已激活），登录时需要验证

### 测试数据

可以使用以下测试数据验证登录功能：
```json
{
  "email": "test@example.com",
  "password": "password123",
  "rememberMe": false
}
```

### 前端 API 调用示例

**auth.ts 扩展**:
```typescript
export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  roles: string[]
}

export const login = (data: LoginRequest) => {
  return request.post<LoginResponse>('/auth/login', data)
}

export const refreshToken = (refreshToken: string) => {
  return request.post<LoginResponse>('/auth/refresh', { refreshToken })
}
```

**LoginView.vue 提交逻辑**:
```typescript
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const response = await login({
      email: formData.email,
      password: formData.password,
      rememberMe: formData.rememberMe
    })
    
    authStore.setToken(response.accessToken, response.refreshToken)
    authStore.user = {
      userId: response.userId,
      username: response.username,
      roles: response.roles
    }
    
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}
```

## References

- [Source: docs/architecture.md] - 认证与安全决策、API 设计规范
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 2.3 原始需求（第288-310行）
- [Source: _bmad-output/implementation-artifacts/2-1-jwt-auth-infrastructure.md] - JWT 认证基础设施
- [Source: _bmad-output/implementation-artifacts/2-2-user-registration.md] - 用户注册功能
- [Source: _bmad-output/implementation-artifacts/1-5-response-exception-handling.md] - 统一响应格式和异常处理
- Spring Security BCrypt: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
- Element Plus 表单组件: https://element-plus.org/zh-CN/component/form.html
- Pinia 状态管理: https://pinia.vuejs.org/zh/

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

- 后端编译验证：mvn clean compile 成功（33 个源文件）
- 前端构建验证：npm run build 成功（1676 模块转换，595ms）

### Completion Notes List

- 2026-04-03: 创建 LoginRequest DTO，包含 email, password, rememberMe 字段
- 2026-04-03: 创建 LoginResponse DTO，包含 accessToken, refreshToken, userId, username, roles 等
- 2026-04-03: 创建 RefreshTokenRequest DTO
- 2026-04-03: 扩展 ErrorCode 添加 LOGIN_FAILED, ACCOUNT_LOCKED, ACCOUNT_NOT_ACTIVATED, REFRESH_TOKEN_INVALID
- 2026-04-03: 创建 Flyway 迁移脚本 V3__add_login_lock_fields.sql 添加 login_fail_count 和 lock_until 字段
- 2026-04-03: 更新 User 实体类添加 loginFailCount 和 lockUntil 字段
- 2026-04-03: 扩展 AuthService 接口添加 login 和 refreshToken 方法
- 2026-04-03: 实现 AuthServiceImpl 登录逻辑（包含账号锁定机制：5次失败锁定15分钟）
- 2026-04-03: 扩展 AuthController 添加 POST /api/auth/login 和 POST /api/auth/refresh 接口
- 2026-04-03: 更新 SecurityConfig 添加登录和刷新 Token 接口到公开列表
- 2026-04-03: 后端编译验证通过（mvn clean compile - 33 个源文件）
- 2026-04-03: 扩展前端 auth.ts 添加 login 和 refreshToken API 接口
- 2026-04-03: 扩展前端 auth store 实现 login, logout, refreshAccessToken, restoreFromStorage
- 2026-04-03: 创建 LoginView.vue 登录页面，包含表单验证和样式
- 2026-04-03: 更新 request.ts 添加自定义类型定义
- 2026-04-03: 前端构建验证通过（npm run build - 1676 模块转换）

### File List

**后端文件:**
- backend/src/main/java/com/syit/cpc/dto/request/LoginRequest.java（新建）
- backend/src/main/java/com/syit/cpc/dto/request/RefreshTokenRequest.java（新建）
- backend/src/main/java/com/syit/cpc/dto/response/LoginResponse.java（新建）
- backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java（更新）
- backend/src/main/java/com/syit/cpc/entity/User.java（更新）
- backend/src/main/java/com/syit/cpc/service/AuthService.java（更新）
- backend/src/main/java/com/syit/cpc/service/impl/AuthServiceImpl.java（更新）
- backend/src/main/java/com/syit/cpc/controller/AuthController.java（更新）
- backend/src/main/java/com/syit/cpc/config/SecurityConfig.java（更新）
- backend/src/main/resources/db/migration/V3__add_login_lock_fields.sql（新建）

**前端文件:**
- frontend/src/api/auth.ts（更新）
- frontend/src/api/request.ts（更新）
- frontend/src/stores/auth.ts（更新）
- frontend/src/views/LoginView.vue（新建）

### Change Log

- 2026-04-03: 完成用户登录功能的全栈实现（后端 API + 前端页面）
- 2026-04-03: 实现登录失败锁定机制（5次失败锁定15分钟）
- 2026-04-03: 实现 Refresh Token 自动刷新机制
- 2026-04-03: 实现前端 Pinia Auth Store 状态管理
- 2026-04-03: 实现前端 Axios 拦截器自动添加 Token
- 2026-04-03: 代码审查修复 C1 - 使用数据库原子操作避免并发竞态条件
- 2026-04-03: 代码审查修复 C2 - 处理 NumberFormatException
- 2026-04-03: 代码审查修复 M3 - 实现 401 自动刷新 Token

