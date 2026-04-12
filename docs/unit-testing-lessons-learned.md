# 单元测试开发经验总结

## 日期：2026-04-07
## 项目：ustil (沈阳工业大学计算机程序设计社团官方网站)
## 任务：为 AuthService 和 UserService 编写单元测试

---

## 问题与解决方案

### 问题 1: MyBatis-Plus 重载方法导致的 Mockito 歧义

#### 错误信息
```
reference to insert is ambiguous
  both method insert(T) in com.baomidou.mybatisplus.core.mapper.BaseMapper 
  and method insert(java.util.Collection<T>) in com.baomidou.mybatisplus.core.mapper.BaseMapper match
```

#### 问题原因
MyBatis-Plus 的 `BaseMapper` 接口存在方法重载：
- `insert(T entity)` - 插入单个对象
- `insert(Collection<T> entityList)` - 批量插入

使用 Mockito 的 `any()` 匹配器时，编译器无法确定应该匹配哪个重载方法。

#### 错误写法
```java
// ❌ 错误：any() 无法确定类型
when(userMapper.insert(any())).thenReturn(1);
verify(userMapper).insert(any());

// ❌ 错误：argThat 的 lambda 参数没有显式类型
verify(userMapper).insert(argThat(user -> 
    "ROLE_PROBATION".equals(user.getRole())
));
```

#### 正确写法
```java
// ✅ 正确：使用显式类型 any(User.class)
when(userMapper.insert(any(User.class))).thenReturn(1);
verify(userMapper).insert(any(User.class));

// ✅ 正确：argThat 的 lambda 参数显式声明类型
verify(userMapper).insert(argThat((User user) -> 
    "ROLE_PROBATION".equals(user.getRole())
));

// ✅ 正确：使用不同的参数名避免变量冲突
verify(userMapper).updateById(argThat((User u) -> 
    u.getLoginFailCount() == 0
));
```

#### 经验教训
1. **使用 Mockito 测试 MyBatis-Plus 时，必须显式指定类型**
2. `any()` → `any(ClassName.class)`
3. `argThat(obj -> ...)` → `argThat((ClassName obj) -> ...)`
4. 注意 lambda 参数名不要与方法内其他变量冲突

---

### 问题 2: 测试数据污染导致测试失败

#### 错误信息
```
com.syit.cpc.common.exception.BusinessException: 账号已锁定，请在15分钟后重试
```

#### 问题原因
在 `@BeforeEach` 中创建的 `testUser` 对象被多个测试方法共享。当一个测试方法修改了 `testUser` 的状态（如设置 `lockUntil`），后续的测试方法会使用被修改过的数据，导致意外失败。

#### 错误写法
```java
@BeforeEach
void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setLockUntil(null);
    // ... 其他字段
}

@Test
void login_ResetFailCountOnSuccess() {
    // ❌ 错误：修改了共享的 testUser 对象
    testUser.setLoginFailCount(3);
    testUser.setLockUntil(LocalDateTime.now().plusMinutes(5));
    when(userMapper.selectOne(any())).thenReturn(testUser);
    // ...
}
```

#### 正确写法
```java
@Test
void login_ResetFailCountOnSuccess() {
    // ✅ 正确：每个测试创建独立对象
    User user = new User();
    user.setId(1L);
    user.setStudentId("2021001234");
    user.setUsername("张三");
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");
    user.setRole("ROLE_PROBATION");
    user.setStatus(1);
    user.setLoginFailCount(3);
    user.setLockUntil(LocalDateTime.now().minusMinutes(10)); // 锁定已过期
    when(userMapper.selectOne(any())).thenReturn(user);
    // ...
}
```

#### 经验教训
1. **共享的测试对象不应该被修改**
2. 需要特殊状态的测试应该创建新对象
3. 使用 `@BeforeEach` 只初始化默认状态的对象
4. 每个测试应该是独立的，不依赖其他测试的状态

---

### 问题 3: 原子更新 vs 普通更新的 Mock 验证

#### 错误信息
```
Wanted but not invoked:
userMapper.updateById(<any com.syit.cpc.entity.User>);

However, there were exactly 3 interactions with this mock:
userMapper.selectOne(...)
userMapper.update(null, LambdaUpdateWrapper)  // ← 调用的是 update() 而不是 updateById()
userMapper.selectById(1L)
```

#### 问题原因
`AuthServiceImpl` 中的 `incrementLoginFailCount` 方法使用了 MyBatis-Plus 的原子更新：
```java
// 使用 UpdateWrapper 实现原子操作，避免并发竞态条件
LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.eq(User::getId, user.getId())
             .setSql("login_fail_count = COALESCE(login_fail_count, 0) + 1");
userMapper.update(null, updateWrapper);  // ← 调用 update() 而不是 updateById()
```

而测试中错误地验证了 `updateById()`：
```java
// ❌ 错误：验证了不存在的方法调用
verify(userMapper).updateById(any(User.class));
```

#### 正确写法
```java
// ✅ 正确：验证实际调用的方法
verify(userMapper, atLeastOnce()).update(any(), any());  // 验证原子更新
verify(userMapper, atLeastOnce()).selectById(anyLong()); // 验证重新查询
verify(userMapper, never()).updateById(any(User.class)); // 确认没有直接 updateById
```

#### 经验教训
1. **测试必须基于实际的实现逻辑**
2. 使用数据库原子操作时，调用的是 `update(null, wrapper)` 而不是 `updateById(entity)`
3. 查看实现代码，确认实际调用的方法
4. 理解并发安全的实现方式（原子操作 vs 普通更新）

---

### 问题 4: Mock 返回值未正确配置导致意外异常

#### 错误信息
```
com.syit.cpc.common.exception.BusinessException: 账号已锁定，请在15分钟后重试
```

#### 问题原因
测试中设置了 `lockUntil` 为未来时间，但没有正确理解业务逻辑：
```java
// ❌ 错误：设置了未来的锁定时间，导致登录检查失败
user.setLockUntil(LocalDateTime.now().plusMinutes(5));
```

业务逻辑在验证密码之前就会检查锁定状态：
```java
// AuthServiceImpl.login() 第 113 行
if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
    throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);  // ← 在这里就抛异常了
}
```

#### 正确写法
```java
// ✅ 正确：如果要测试登录成功后的重置逻辑，锁定时间应该已过期
user.setLockUntil(LocalDateTime.now().minusMinutes(10)); // 锁定已过期
```

#### 经验教训
1. **理解业务流程的执行顺序**
2. 设置测试数据时，要考虑业务逻辑的每个检查点
3. 如果想测试流程的后半部分，前面的检查点必须通过
4. 仔细阅读实现代码，了解每个条件判断的顺序

---

### 问题 5: 分页边界值保护的 Mock 配置

#### 错误信息
```
expected: <100> but was: <10>
```

#### 问题原因
同一个测试方法中多次调用 `getMemberList`，但 Mock 只配置了一次返回值：
```java
// ❌ 错误：只配置了一个 Page 对象
Page<User> page = new Page<>(1, 10);
page.setRecords(Collections.emptyList());
page.setTotal(0L);
when(userMapper.selectPage(any(Page.class), any())).thenReturn(page);

// 第一次调用：getMemberList(0L, 10L, null, null)
// 第二次调用：getMemberList(1L, 200L, null, null) - 仍然返回同一个 page 对象
```

#### 正确写法
```java
// ✅ 正确：为每次调用配置不同的返回值
Page<User> page1 = new Page<>(1, 10);
page1.setRecords(Collections.emptyList());
page1.setTotal(0L);

Page<User> page2 = new Page<>(1, 100);
page2.setRecords(Collections.emptyList());
page2.setTotal(0L);

when(userMapper.selectPage(any(Page.class), any()))
        .thenReturn(page1)  // 第一次调用返回 page1
        .thenReturn(page2); // 第二次调用返回 page2
```

#### 经验教训
1. **多次调用同一 Mock 方法时，需要配置多个返回值**
2. 使用链式 `.thenReturn().thenReturn()` 或者 `thenAnswer()`
3. 每个返回值应该是独立的对象，避免共享状态
4. 测试边界值时，要考虑多次调用的场景

---

## 通用最佳实践

### 1. 测试命名规范
```java
// ✅ 好的命名：清晰表达测试意图
@DisplayName("登录失败 - 账号已锁定")
void login_Fail_AccountLocked() { }

@DisplayName("注册成功 - 验证用户初始角色为预备成员")
void register_Success_VerifyInitialRole() { }
```

### 2. 测试组织结构
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 单元测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    @Nested
    @DisplayName("用户注册测试")
    class RegisterTests {
        @Test
        @DisplayName("注册成功 - 正常流程")
        void register_Success() { }
    }
    
    @Nested
    @DisplayName("用户登录测试")
    class LoginTests {
        @Test
        @DisplayName("登录失败 - 密码错误")
        void login_Fail_WrongPassword() { }
    }
}
```

### 3. Given-When-Then 模式
```java
@Test
@DisplayName("注册失败 - 邮箱已存在")
void register_Fail_EmailExists() {
    // Given - 准备测试数据
    when(userMapper.selectCount(any())).thenReturn(1L);
    
    // When & Then - 执行并验证
    BusinessException exception = assertThrows(BusinessException.class,
            () -> authService.register(validRegisterRequest));
    assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    verify(userMapper, times(1)).selectCount(any());
    verify(userMapper, never()).insert(any(User.class));
}
```

### 4. Mock 验证要点
```java
// ✅ 验证方法调用次数
verify(userMapper, times(2)).selectCount(any());

// ✅ 验证方法从未被调用
verify(userMapper, never()).insert(any(User.class));

// ✅ 验证至少调用一次
verify(userMapper, atLeastOnce()).update(any(), any());

// ✅ 验证参数内容
verify(userMapper).insert(argThat((User user) ->
    "ROLE_PROBATION".equals(user.getRole()) &&
    user.getStatus() == 1
));
```

### 5. 避免的常见陷阱

#### ❌ 不要这样做
```java
// 1. 使用 any() 而不指定类型
when(mapper.insert(any())).thenReturn(1);

// 2. 修改共享的测试对象
testUser.setStatus(0);

// 3. 忽略业务逻辑的执行顺序
// 设置一个会让前面检查失败的数据

// 4. 多次调用但只配置一个返回值
when(mapper.select(any())).thenReturn(result);
mapper.select(1);
mapper.select(2); // 仍然返回同一个 result
```

#### ✅ 应该这样做
```java
// 1. 显式指定类型
when(mapper.insert(any(Entity.class))).thenReturn(1);

// 2. 每个测试创建独立对象
User user = createTestUser();
user.setStatus(0);

// 3. 理解业务流程，设置合理的数据
user.setLockUntil(LocalDateTime.now().minusMinutes(10));

// 4. 为每次调用配置返回值
when(mapper.select(any()))
    .thenReturn(result1)
    .thenReturn(result2);
```

---

## 快速检查清单

编写单元测试时，检查以下项目：

- [ ] 所有 `any()` 都指定了具体类型 `any(ClassName.class)`
- [ ] `argThat()` 的 lambda 参数有显式类型声明
- [ ] 每个测试使用独立的测试对象，不修改共享对象
- [ ] Mock 配置的方法与实际实现调用的方法一致
- [ ] 测试数据符合业务逻辑的执行顺序
- [ ] 多次调用同一方法时配置了多个返回值
- [ ] 测试方法命名清晰，使用 Given-When-Then 结构
- [ ] 验证了关键方法的调用次数和参数
- [ ] 测试了正常流程和异常流程
- [ ] 测试了边界条件和特殊情况

---

## 参考资料

- [Mockito 官方文档](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [MyBatis-Plus BaseMapper API](https://baomidou.com/pages/49cc81/)
- [JUnit 5 用户指南](https://junit.org/junit5/docs/current/user-guide/)
- [测试最佳实践 - Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html)

---

**最后更新**: 2026-04-07  
**维护者**: Max (Project Lead)
