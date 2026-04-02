<template>
  <div class="my-applications-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">📋 我的报名记录</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="applications"
        stripe
        style="width: 100%"
        class="app-table"
      >
        <el-table-column prop="jobTitle" label="职位名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColorMap[row.status] || 'info'" size="default">
              {{ statusMap[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              size="small"
              @click="handleCancel(row)"
            >
              取消申请
            </el-button>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && applications.length === 0" description="暂无报名记录" />

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
import { getMyApplications, reviewApplication } from '@/api/order'
import { formatDate, applyStatusMap, applyStatusColorMap } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const applications = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const statusMap = applyStatusMap
const statusColorMap = applyStatusColorMap

onMounted(() => {
  loadApplications()
})

const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getMyApplications({ page: page.value, size: size.value })
    if (res.data) {
      applications.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载报名列表失败：', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await reviewApplication(row.id, { status: 4 })
    ElMessage.success('已取消申请')
    loadApplications()
  } catch (e) {
    // 用户取消
  }
}

watch([page, size], () => {
  loadApplications()
})
</script>

<style scoped>
.my-applications-page {
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
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.app-table {
  border-radius: 8px;
}

.no-action {
  color: #c0c4cc;
}
</style>
