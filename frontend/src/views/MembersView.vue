<template>
  <div class="members-container">
    <h2 class="page-title">成员管理</h2>

    <!-- 筛选和搜索 -->
    <div class="filter-bar">
      <el-select
        v-model="searchForm.role"
        placeholder="角色筛选"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="全部" value="" />
        <el-option label="预备成员" value="ROLE_PROBATION" />
        <el-option label="正式成员" value="ROLE_MEMBER" />
        <el-option label="负责人" value="ROLE_ADMIN" />
      </el-select>

      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索学号或姓名"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-button type="primary" @click="handleSearch" :loading="searching">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <!-- 成员列表 -->
    <el-table :data="memberList" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="userId" label="ID" width="80" />
      <el-table-column label="学号" width="120">
        <template #default="{ row }">
          {{ row.studentId }}
        </template>
      </el-table-column>
      <el-table-column label="姓名" width="120">
        <template #default="{ row }">
          {{ row.username }}
        </template>
      </el-table-column>
      <el-table-column label="邮箱" width="200">
        <template #default="{ row }">
          {{ row.email }}
        </template>
      </el-table-column>
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          <div class="badge" :class="getRoleBadgeClass(row.role)">
            {{ getRoleLabel(row.role) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <div class="badge" :class="row.status === 1 ? 'badge-success' : 'badge-danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="加入时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <AppButton variant="outline" size="sm" @click="handleUpdateRole(row)">修改角色</AppButton>
          <AppButton
            :variant="row.status === 1 ? 'danger' : 'success'"
            size="sm"
            @click="handleUpdateStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </AppButton>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无成员数据" />
      </template>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 修改角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="修改成员角色" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="成员">
          <span>{{ roleForm.username }}</span>
        </el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="roleForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="预备成员" value="ROLE_PROBATION" />
            <el-option label="正式成员" value="ROLE_MEMBER" />
            <el-option label="负责人" value="ROLE_ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <AppButton variant="outline" @click="roleDialogVisible = false">取消</AppButton>
        <AppButton variant="primary" @click="confirmUpdateRole" :loading="updating">确定</AppButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberList, updateMemberRole, updateMemberStatus } from '@/api/member'
import type { MemberInfo, UpdateUserRoleRequest, UpdateUserStatusRequest } from '@/types/member'
import AppButton from '@/components/common/AppButton.vue'

// 搜索表单
const searchForm = reactive({
  role: '',
  keyword: '',
})

// 成员列表
const memberList = ref<MemberInfo[]>([])
const loading = ref(false)
const searching = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
})

// 修改角色对话框
const roleDialogVisible = ref(false)
const roleForm = reactive({
  userId: 0,
  username: '',
  role: '',
})
const updating = ref(false)

/**
 * 获取成员列表
 */
async function fetchMemberList() {
  loading.value = true
  try {
    const res = await getMemberList({
      current: pagination.current,
      size: pagination.size,
      role: searchForm.role || undefined,
      keyword: searchForm.keyword || undefined,
    })
    memberList.value = res.records
    pagination.total = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '获取成员列表失败')
  } finally {
    loading.value = false
    searching.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  pagination.current = 1
  searching.value = true
  fetchMemberList()
}

/**
 * 分页大小变化
 */
function handleSizeChange() {
  pagination.current = 1
  fetchMemberList()
}

/**
 * 当前页变化
 */
function handleCurrentChange() {
  fetchMemberList()
}

/**
 * 修改角色
 */
function handleUpdateRole(row: MemberInfo) {
  roleForm.userId = row.userId
  roleForm.username = row.username
  roleForm.role = row.role
  roleDialogVisible.value = true
}

/**
 * 确认修改角色
 */
async function confirmUpdateRole() {
  if (!roleForm.role) {
    ElMessage.warning('请选择角色')
    return
  }

  updating.value = true
  try {
    const data: UpdateUserRoleRequest = { role: roleForm.role }
    await updateMemberRole(roleForm.userId, data)
    ElMessage.success('角色已更新')
    roleDialogVisible.value = false
    fetchMemberList()
  } catch (error: any) {
    ElMessage.error(error.message || '更新角色失败')
  } finally {
    updating.value = false
  }
}

/**
 * 更新状态（禁用/启用）
 */
async function handleUpdateStatus(row: MemberInfo) {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该成员吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const data: UpdateUserStatusRequest = { status: row.status === 1 ? 0 : 1 }
    await updateMemberStatus(row.userId, data)
    ElMessage.success(`账号已${action}`)
    fetchMemberList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '更新状态失败')
    }
  }
}

/**
 * 获取角色标签类型
 */
function getRoleTagType(role: string): 'info' | 'primary' | 'warning' {
  switch (role) {
    case 'ROLE_PROBATION':
      return 'info'
    case 'ROLE_MEMBER':
      return 'primary'
    case 'ROLE_ADMIN':
      return 'warning'
    default:
      return 'info'
  }
}

/**
 * 获取角色徽章类名
 */
function getRoleBadgeClass(role: string): string {
  const classMap: Record<string, string> = {
    ROLE_PROBATION: 'badge-info',
    ROLE_MEMBER: 'badge-primary',
    ROLE_ADMIN: 'badge-warning',
  }
  return classMap[role] || 'badge-info'
}

/**
 * 获取角色标签文本
 */
function getRoleLabel(role: string): string {
  switch (role) {
    case 'ROLE_PROBATION':
      return '预备成员'
    case 'ROLE_MEMBER':
      return '正式成员'
    case 'ROLE_ADMIN':
      return '负责人'
    default:
      return role
  }
}

/**
 * 格式化日期
 */
function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  fetchMemberList()
})
</script>

<style scoped>
.members-container {
  max-width: var(--max-width-wide);
  margin: 0 auto;
  padding: var(--spacing-2xl) var(--spacing-base);
}

.page-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-xl);
  color: var(--color-text-primary);
}

/* 徽章样式 */
.badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: white;
}

.badge-info {
  background-color: var(--color-role-probation);
}

.badge-primary {
  background-color: var(--color-role-member);
}

.badge-warning {
  background-color: var(--color-role-admin);
}

.badge-success {
  background-color: var(--color-status-published);
}

.badge-danger {
  background-color: var(--color-status-rejected);
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  align-items: center;
}

.filter-select {
  width: 150px;
}

.search-input {
  width: 250px;
}

.pagination-container {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background-color: var(--color-neutral-50);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-semibold);
}

:deep(.el-table__row:hover) {
  background-color: var(--color-primary-50);
}

/* 对话框样式优化 */
:deep(.el-dialog__header) {
  border-bottom: 2px solid var(--color-neutral-100);
  padding-bottom: var(--spacing-md);
}

:deep(.el-dialog__body) {
  padding: var(--spacing-xl);
}

/* 响应式 */
@media (max-width: 768px) {
  .members-container {
    padding: var(--spacing-xl) var(--spacing-sm);
  }

  .page-title {
    font-size: var(--font-size-2xl);
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-select,
  .search-input {
    width: 100%;
  }
}
</style>
