<template>
  <div class="user-manage-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">👥 用户管理</span>
          <div class="filter-section">
            <el-input
              v-model="keyword"
              placeholder="搜索用户名/手机号"
              clearable
              :prefix-icon="Search"
              style="width: 200px;"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            />
            <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px;" @change="handleSearch">
              <el-option label="正常" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
            <el-select v-model="roleFilter" placeholder="角色" clearable style="width: 120px;" @change="handleSearch">
              <el-option label="学生" value="STUDENT" />
              <el-option label="雇主" value="EMPLOYER" />
              <el-option label="管理员" value="ADMIN" />
            </el-select>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="users" stripe style="width: 100%">
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="120">
          <template #default="{ row }">
            {{ row.realName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="roleColorMap[row.roleType] || 'info'" size="small">
              {{ roleTypeMap[row.roleType] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              size="small"
              @click="handleToggleStatus(row, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              size="small"
              @click="handleToggleStatus(row, 1)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && users.length === 0" description="暂无用户数据" />

      <Pagination
        v-if="total > 0"
        :total="total"
        v-model:page="page"
        v-model:size="size"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { listUsers, updateUserStatus } from '@/api/user'
import { formatDate, roleTypeMap, roleColorMap } from '@/utils/format'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const statusFilter = ref('')
const roleFilter = ref('')

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (keyword.value) params.keyword = keyword.value
    if (statusFilter.value !== '') params.status = statusFilter.value
    if (roleFilter.value !== '') params.roleType = roleFilter.value
    const res = await listUsers(params)
    if (res.data) {
      users.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadUsers()
}

const handleToggleStatus = async (row, status) => {
  const statusText = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${statusText}用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateUserStatus(row.id, status)
    ElMessage.success(`已${statusText}`)
    loadUsers()
  } catch (e) {
    // 用户取消
  }
}

watch([page, size], () => {
  loadUsers()
})
</script>

<style scoped>
.user-manage-page {
  max-width: 1200px;
  margin: 0 auto;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
