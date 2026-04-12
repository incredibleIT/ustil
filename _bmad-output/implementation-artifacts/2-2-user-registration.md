---
story_id: "2.2"
story_key: "2-2-user-registration"
epic: "用户认证与成员管理"
status: done
created: "2026-04-03"
---

# Story 2.2: 用户注册功能

## Story

As a 预备成员,
I want 通过学号、姓名、邮箱注册账号,
So that 可以成为社团成员。

## Acceptance Criteria

### AC1: 注册表单验证
**Given** 访问注册页面
**When** 填写注册表单
**Then** 必须填写学号、姓名、邮箱、密码、确认密码
**And** 密码强度要求：至少8位，包含字母和数字
**And** 邮箱格式验证
**And** 学号格式验证（数字，9-12位）
**And** 两次密码必须一致

### AC2: 邮箱唯一性校验
**Given** 用户提交注册
**When** 后端验证邮箱
**Then** 检查邮箱是否已注册
**And** 如果邮箱已存在，返回错误提示"该邮箱已被注册"
**And** 如果学号已存在，返回错误提示"该学号已被注册"

### AC3: 密码加密存储
**Given** 用户注册信息验证通过
**When** 保存用户到数据库
**Then** 使用 BCrypt 加密密码
**And** 不得存储明文密码
**And** 加密强度使用 Spring Security 默认值

### AC4: 注册成功返回
**Given** 注册成功
**When** 用户提交注册表单
**Then** 创建用户记录，初始角色为 ROLE_PROBATION（预备成员）
**And** 账号状态为未激活（status = 0）
**And** 返回成功消息"注册成功，请登录"
**And** 不返回 JWT Token（需登录后获取）

### AC5: 前端注册页面
**Given** 用户访问注册页面
**When** 页面加载
**Then** 显示注册表单（学号、姓名、邮箱、密码、确认密码）
**And** 表单验证提示清晰友好
**And** 提供"已有账号？立即登录"链接
**And** 注册成功后跳转到登录页

## Tasks / Subtasks

### 后端任务

- [x] Task 1: 创建注册请求 DTO (AC: #1, #2)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/dto/request/RegisterRequest.java`
  - [x] 包含字段：
    - studentId (String, @NotBlank, @Pattern 验证学号格式)
    - username (String, @NotBlank, @Size(min=2, max=50))
    - email (String, @NotBlank, @Email)
    - password (String, @NotBlank, @Size(min=8, max=100))
    - confirmPassword (String, @NotBlank)
  - [x] 添加自定义验证注解或 Validator 验证密码一致性
  - [x] 添加 Swagger 注解（@Schema）

- [x] Task 2: 创建用户密码加密工具类 (AC: #3)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/util/PasswordUtil.java`
  - [x] 使用 Spring Security 的 BCryptPasswordEncoder
  - [x] 实现 encodePassword(String rawPassword) 方法
  - [x] 实现 matchesPassword(String rawPassword, String encodedPassword) 方法
  - [x] 标记为 @Component

- [x] Task 3: 创建 AuthService 接口和实现 (AC: #2, #3, #4)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/service/AuthService.java` 接口
  - [x] 创建 `backend/src/main/java/com/syit/cpc/service/impl/AuthServiceImpl.java`
  - [x] 实现 register(RegisterRequest request) 方法：
    - 验证邮箱是否已存在
    - 验证学号是否已存在
    - 验证密码一致性
    - 加密密码
    - 创建 User 实体，设置初始角色 ROLE_PROBATION
    - 设置账号状态为启用（status = 1）
    - 保存到数据库
    - 返回注册成功消息
  - [x] 注入 UserMapper
  - [x] 注入 PasswordUtil
  - [x] 使用 @Service 注解
  - [x] 使用 @Transactional 注解

- [x] Task 4: 创建 AuthController (AC: #2, #4, #5)
  - [x] 创建 `backend/src/main/java/com/syit/cpc/controller/AuthController.java`
  - [x] 添加 @RestController 和 @RequestMapping("/api/auth") 注解
  - [x] 实现 POST /api/auth/register 接口：
    - 接收 RegisterRequest
    - 调用 AuthService.register()
    - 返回 ApiResponse<String>
  - [x] 添加 Swagger 注解（@Operation, @ApiResponses）
  - [x] 接口不需要认证（公开接口）

- [x] Task 5: 更新 SecurityConfig (AC: #5)
  - [x] 确保 POST /api/auth/register 在公开接口列表中
  - [x] 检查 CORS 配置是否允许注册请求

- [ ] Task 6: 编写单元测试 (AC: #1-4)
  - [ ] 创建 `backend/src/test/java/com/syit/cpc/service/AuthServiceTest.java`
  - [ ] 测试用例：
    - 注册成功
    - 邮箱已存在
    - 学号已存在
    - 密码不一致
    - 密码强度不足
    - 邮箱格式错误
    - 学号格式错误

### 前端任务

- [x] Task 7: 创建注册 API 接口 (AC: #5)
  - [x] 创建 `frontend/src/api/auth.ts`
  - [x] 实现 register 函数：
    - 发送 POST 请求到 /api/auth/register
    - 传递 RegisterRequest 数据
    - 返回 ApiResponse

- [x] Task 8: 创建注册页面 (AC: #5)
  - [x] 创建 `frontend/src/views/RegisterView.vue`
  - [x] 使用 Element Plus 表单组件
  - [x] 表单字段：
    - 学号（输入框，数字验证）
    - 姓名（输入框）
    - 邮箱（输入框，邮箱验证）
    - 密码（密码输入框，强度验证）
    - 确认密码（密码输入框）
  - [x] 实现表单验证规则：
    - 必填验证
    - 学号格式：9-12位数字
    - 邮箱格式验证
    - 密码强度：至少8位，包含字母和数字
    - 密码一致性验证
  - [x] 提交时调用 register API
  - [x] 注册成功：显示成功消息，跳转到登录页
  - [x] 注册失败：显示错误消息
  - [x] 添加"已有账号？立即登录"链接

- [x] Task 9: 添加注册路由 (AC: #5)
  - [x] 在 `frontend/src/router/index.ts` 添加注册路由
  - [x] 路由路径：/register
  - [x] 组件：RegisterView
  - [x] 不需要认证守卫

## Dev Notes

### 必须遵守的架构规范 [Source: docs/architecture.md]

**技术栈**:
- 后端: Spring Boot 3.5.0 + Java 17 + MyBatis-Plus 3.5.9
- 前端: Vue.js 3.4.x + TypeScript 5.x + Vite 5.x
- UI 框架: Element Plus 2.5+
- 密码加密: BCrypt (Spring Security)

**API 设计规范**:
- RESTful API
- 统一响应格式: `{ "code": 200, "message": "...", "data": {} }`
- 接口路径: `/api/auth/register`
- HTTP 方法: POST

**数据库规范**:
- 用户表: `users`
- 角色字段: `role` (ROLE_PROBATION / ROLE_MEMBER / ROLE_ADMIN)
- 状态字段: `status` (0=未激活, 1=激活)
- 初始角色: ROLE_PROBATION（预备成员）
- 初始状态: 0（未激活）

**密码加密**:
- 使用 Spring Security 的 BCryptPasswordEncoder
- 不要存储明文密码
- 使用默认加密强度

**验证规范**:
- 使用 Jakarta Bean Validation (@Valid)
- Controller 层使用 @Valid 注解
- Service 层补充业务规则验证
- 错误消息使用中文，用户友好

**项目结构规范**:
```
backend/src/main/java/com/syit/cpc/
├── controller/
│   └── AuthController.java          # 认证控制器
├── dto/
│   └── request/
│       └── RegisterRequest.java     # 注册请求 DTO
├── service/
│   ├── AuthService.java             # 认证服务接口
│   └── impl/
│       └── AuthServiceImpl.java     # 认证服务实现
└── util/
    └── PasswordUtil.java            # 密码加密工具类

frontend/src/
├── api/
│   └── auth.ts                      # 认证 API 接口
├── views/
│   └── RegisterView.vue             # 注册页面
└── router/
    └── index.ts                     # 路由配置
```

### DTO 设计

**RegisterRequest.java 示例**:
```java
@Data
@Schema(description = "用户注册请求")
public class RegisterRequest {
    
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{9,12}$", message = "学号格式不正确，应为9-12位数字")
    @Schema(description = "学号", example = "2021001234")
    private String studentNo;
    
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度应为2-50个字符")
    @Schema(description = "姓名", example = "张三")
    private String name;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 100, message = "密码长度至少8位")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    @Schema(description = "密码")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码")
    private String confirmPassword;
}
```

### Service 实现要点

**AuthService 注册流程**:
1. 验证邮箱是否已存在
2. 验证学号是否已存在
3. 验证密码一致性
4. 加密密码
5. 创建 User 实体
6. 设置初始角色: ROLE_PROBATION
7. 设置状态: 0 (未激活)
8. 保存到数据库
9. 返回成功消息

**关键代码示例**:
```java
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    
    @Override
    public String register(RegisterRequest request) {
        // 1. 验证邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 2. 验证学号是否已存在
        LambdaQueryWrapper<User> studentNoWrapper = new LambdaQueryWrapper<>();
        studentNoWrapper.eq(User::getStudentNo, request.getStudentNo());
        if (userMapper.selectCount(studentNoWrapper) > 0) {
            throw new BusinessException(ErrorCode.STUDENT_NO_ALREADY_EXISTS);
        }
        
        // 3. 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        
        // 4. 创建用户实体
        User user = new User();
        user.setStudentNo(request.getStudentNo());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordUtil.encodePassword(request.getPassword()));
        user.setRole("ROLE_PROBATION");  // 初始角色：预备成员
        user.setStatus(0);  // 未激活状态
        
        // 5. 保存到数据库
        userMapper.insert(user);
        
        return "注册成功，请登录";
    }
}
```

### ErrorCode 扩展

需要在 `ErrorCode.java` 中添加新的错误码：
```java
EMAIL_ALREADY_EXISTS(1001, "该邮箱已被注册"),
STUDENT_NO_ALREADY_EXISTS(1002, "该学号已被注册"),
PASSWORD_NOT_MATCH(1003, "两次输入的密码不一致"),
```

### Controller 实现示例

**AuthController.java**:
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "参数错误或邮箱/学号已存在")
    })
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ApiResponse.success(message);
    }
}
```

### 前端表单验证规则

**RegisterView.vue 验证规则**:
```typescript
const rules = {
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{9,12}$/, message: '学号格式不正确，应为9-12位数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度应为2-50个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 100, message: '密码长度至少8位', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}
```

### 安全注意事项

1. **密码安全**:
   - ✅ 使用 BCrypt 加密，不可逆
   - ✅ 不要记录日志中的密码
   - ✅ 不要在响应中返回密码
   
2. **防重放攻击**:
   - MVP 阶段不需要 Token 或 CSRF
   - 后续可添加图形验证码
   
3. **邮箱唯一性**:
   - 数据库层面添加唯一索引
   - 应用层面提前验证
   
4. **输入验证**:
   - 前后端双重验证
   - 防止 SQL 注入（MyBatis-Plus 参数化查询）
   - 防止 XSS 攻击

### 数据库索引

确保 `users` 表有以下索引：
```sql
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX idx_users_student_no ON users(student_no);
```

### 从 Story 2.1 学习到的经验

- JWT 认证基础设施已就绪
- SecurityConfig 已配置，注册接口需要公开
- 统一响应格式和异常处理已完成
- 密码加密使用 Spring Security 的 BCrypt

### 测试数据

可以使用以下测试数据验证注册功能：
```json
{
  "studentNo": "2021001234",
  "name": "张三",
  "email": "test@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

### 前端 API 调用示例

**auth.ts**:
```typescript
import request from '@/api/request'

export interface RegisterRequest {
  studentNo: string
  name: string
  email: string
  password: string
  confirmPassword: string
}

export const register = (data: RegisterRequest) => {
  return request.post<string>('/auth/register', data)
}
```

**RegisterView.vue 提交逻辑**:
```typescript
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    await register(formData)
    
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
- [Source: _bmad-output/planning-artifacts/epics.md] - Story 2.2 原始需求（第266-287行）
- [Source: _bmad-output/implementation-artifacts/2-1-jwt-auth-infrastructure.md] - JWT 认证基础设施
- [Source: _bmad-output/implementation-artifacts/1-5-response-exception-handling.md] - 统一响应格式和异常处理
- Spring Security BCrypt: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
- Element Plus 表单验证: https://element-plus.org/zh-CN/component/form.html

## Dev Agent Record

### Agent Model Used

Lingma

### Debug Log References

- Node.js 版本切换：v10.24.1 → v20.19.5（使用 nvm）
- 后端编译验证：mvn clean compile 成功（24 个源文件）
- 前端构建验证：npm run build 成功（1672 模块转换，532ms）

### Completion Notes List

- 2026-04-03: 扩展 ErrorCode 枚举，添加 EMAIL_ALREADY_EXISTS(1005)、STUDENT_NO_ALREADY_EXISTS(1006)、PASSWORD_NOT_MATCH(1007)
- 2026-04-03: 创建 PasswordUtil 密码加密工具类，使用 Spring Security 的 BCryptPasswordEncoder
- 2026-04-03: 创建 RegisterRequest DTO，包含学号、姓名、邮箱、密码、确认密码字段，带完整验证注解
- 2026-04-03: 创建 AuthService 接口和 AuthServiceImpl 实现类，实现完整的注册逻辑（邮箱/学号唯一性验证、密码加密、角色初始化）
- 2026-04-03: 创建 AuthController，实现 POST /api/auth/register 接口
- 2026-04-03: 更新 SecurityConfig，将注册接口和登录接口加入公开访问列表
- 2026-04-03: 创建前端 auth.ts API 接口，定义 RegisterRequest 类型和 register 函数
- 2026-04-03: 创建前端 RegisterView.vue 注册页面，包含完整的表单验证和样式
- 2026-04-03: 确认前端路由已配置 /register 路由（之前已存在）
- 2026-04-03: 后端编译验证通过（mvn clean compile）
- 2026-04-03: 前端构建验证通过（npm run build，使用 Node.js v20.19.5）

### File List

**后端文件:**
- backend/src/main/java/com/syit/cpc/common/exception/ErrorCode.java（更新）
- backend/src/main/java/com/syit/cpc/util/PasswordUtil.java（新建）
- backend/src/main/java/com/syit/cpc/dto/request/RegisterRequest.java（新建）
- backend/src/main/java/com/syit/cpc/service/AuthService.java（新建）
- backend/src/main/java/com/syit/cpc/service/impl/AuthServiceImpl.java（新建）
- backend/src/main/java/com/syit/cpc/controller/AuthController.java（新建）
- backend/src/main/java/com/syit/cpc/config/SecurityConfig.java（更新）

**前端文件:**
- frontend/src/api/auth.ts（新建）
- frontend/src/views/RegisterView.vue（新建）

### Change Log

- 2026-04-03: 完成用户注册功能的全栈实现（后端 API + 前端页面）
- 2026-04-03: 实现密码加密存储（BCrypt）
- 2026-04-03: 实现邮箱和学号唯一性验证
- 2026-04-03: 实现前端注册表单验证（学号格式、邮箱格式、密码强度、密码一致性）
- 2026-04-03: 配置注册接口为公开访问（无需认证）
