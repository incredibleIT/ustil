<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-card animate-fade-in-up">
      <div class="login-header">
        <h2>用户登录</h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="formData.rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <AppButton
            type="submit"
            variant="primary"
            :loading="loading"
            class="w-full"
          >
            {{ loading ? '登录中...' : '登录' }}
          </AppButton>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>没有账号？</span>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import AppButton from '@/components/common/AppButton.vue'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive({
  email: '',
  password: '',
  rememberMe: false
})

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    await authStore.login(formData.email, formData.password, formData.rememberMe)
    
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error: any) {
    // 错误消息已由 request.ts 的拦截器处理
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 注意：restoreFromStorage() 已在 main.ts 应用启动时调用，此处无需重复调用
// 路由守卫已处理已登录用户的重定向逻辑
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
  z-index: 1;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: var(--radius-xl);
  padding: 40px;
  box-shadow: var(--shadow-card);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.register-link {
  margin-left: 8px;
  color: var(--color-primary-600);
  font-weight: 500;
  transition: color 0.2s;
}

.register-link:hover {
  color: var(--color-primary-700);
}
</style>
