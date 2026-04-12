<template>
  <div class="project-submit-container">
    <div class="project-submit-header animate-fade-in-up">
      <h1>提交项目作品</h1>
      <el-button text @click="$router.push({ name: 'promotion' })">
        <el-icon><ArrowLeft /></el-icon>
        返回转正页面
      </el-button>
    </div>

    <el-card v-loading="loading" class="animate-fade-in-up" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="text-lg font-bold">项目信息</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="project-form"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="form.projectName"
            placeholder="请输入项目名称（最多200字符）"
            :maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="项目描述" prop="projectDescription">
          <el-input
            v-model="form.projectDescription"
            type="textarea"
            placeholder="请详细描述项目的功能、技术栈、亮点等（最多2000字符）"
            :rows="8"
            :maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="项目链接" prop="projectUrl">
          <el-input
            v-model="form.projectUrl"
            placeholder="可选，如 GitHub 仓库链接或在线演示链接（最多500字符）"
            :maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-alert
            title="提示"
            type="info"
            :closable="false"
            description="提交后项目将进入评审流程，负责人预计在3-5天内完成评审。"
            class="mb-4"
          />
        </el-form-item>

        <el-form-item>
          <AppButton variant="primary" size="lg" :loading="submitting" @click="handleSubmit">
            提交项目
          </AppButton>
          <AppButton variant="outline" size="lg" @click="$router.push({ name: 'promotion' })">
            取消
          </AppButton>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { submitProject } from '@/api/promotion'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import AppButton from '@/components/common/AppButton.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  projectName: '',
  projectDescription: '',
  projectUrl: ''
})

const rules = reactive<FormRules>({
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { max: 200, message: '项目名称不能超过200个字符', trigger: 'blur' }
  ],
  projectDescription: [
    { required: true, message: '请输入项目描述', trigger: 'blur' },
    { max: 2000, message: '项目描述不能超过2000个字符', trigger: 'blur' }
  ],
  projectUrl: [
    { max: 500, message: '项目链接不能超过500个字符', trigger: 'blur' },
    { 
      pattern: /^$|^(https?:\/\/)(localhost|[\w\-]+(\.[\w\-]+)+|\d{1,3}(\.\d{1,3}){3})(:\d+)?([/?#].*)?$/, 
      message: '项目链接格式不正确，需以 http:// 或 https:// 开头', 
      trigger: 'blur' 
    }
  ]
})

async function handleSubmit() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      submitting.value = true
      await submitProject(form)
      ElMessage.success('项目提交成功，等待负责人评审')
      router.push({ name: 'promotion' })
    } catch (error) {
      console.error('提交项目失败:', error)
      // 错误消息已由 request.ts 拦截器处理
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.project-submit-container {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.project-submit-header {
  margin-bottom: var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.project-submit-header h1 {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Card Header 标题样式 */
.card-header :deep(span) {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.project-form {
  max-width: 600px;
  margin: 0 auto;
}

.mb-4 {
  margin-bottom: var(--spacing-lg);
}

/* 响应式 */
@media (max-width: 768px) {
  .project-submit-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .project-submit-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .project-submit-header h1 {
    font-size: var(--font-size-2xl);
  }

  .project-form {
    max-width: 100%;
  }
}
</style>
