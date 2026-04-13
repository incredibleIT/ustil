<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="register-card animate-fade-in-up">
      <h2 class="register-title">用户注册</h2>
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="0"
        size="large"
        @submit.prevent="handleSubmit"
      >
        <el-form-item prop="studentId">
          <el-input
            v-model="formData.studentId"
            placeholder="请输入学号（9-12位数字）"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入姓名"
            prefix-icon="Avatar"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码（至少8位，包含字母和数字）"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item>
          <AppButton
            type="submit"
            variant="primary"
            :loading="loading"
            class="w-full"
          >
            注册
          </AppButton>
        </el-form-item>
      </el-form>

      <div class="login-link">
        <span>已有账号？</span>
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { register } from '@/api/auth'
import type { RegisterRequest } from '@/api/auth'
import AppButton from '@/components/common/AppButton.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const formData = reactive<RegisterRequest>({
  studentId: '',
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const rules = reactive<FormRules<RegisterRequest>>({
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{9,12}$/, message: '学号格式不正确，应为9-12位数字', trigger: 'blur' }
  ],
  username: [
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
      validator: (_rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    await register(formData)

    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error: any) {
    // 区分不同类型的错误
    if (error.response?.data?.message) {
      // 后端业务错误（如邮箱已存在、学号已存在等）
      ElMessage.error(error.response.data.message)
    } else if (error.message) {
      // 网络错误或 Axios 错误
      ElMessage.error(error.message)
    } else {
      // 未知错误
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
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

.register-card {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  position: relative;
  z-index: 1;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: var(--color-text-primary);
  font-size: 28px;
  font-weight: 600;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: var(--color-text-secondary);
}

.link {
  color: var(--color-primary-600);
  text-decoration: none;
  margin-left: 5px;
  font-weight: 500;
  transition: color 0.2s;
}

.link:hover {
  color: var(--color-primary-700);
  text-decoration: underline;
}

:deep(.el-input__wrapper) {
  padding: 12px 15px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
