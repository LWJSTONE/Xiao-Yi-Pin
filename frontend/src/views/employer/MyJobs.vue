<template>
  <div class="my-jobs-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">💼 我的职位</span>
          <el-button type="primary" :icon="EditPen" @click="$router.push('/employer/publish')">
            发布新职位
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="jobs" stripe style="width: 100%">
        <el-table-column prop="title" label="职位名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="120" align="center">
          <template #default="{ row }">
            {{ row.categoryName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="薪资" width="140" align="center">
          <template #default="{ row }">
            <span class="salary-text">{{ formatSalary(row.salaryAmount, row.salaryType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColorMap[row.status] || 'info'" size="small">
              {{ statusMap[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="招聘进度" width="120" align="center">
          <template #default="{ row }">
            {{ row.hiredNum || 0 }} / {{ row.recruitNum || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="primary"
              link
              size="small"
              @click="handleSubmitAudit(row)"
            >
              提交审核
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              @click="$router.push(`/employer/applications?jobId=${row.id}`)"
            >
              查看报名
            </el-button>
            <el-button
              v-if="row.status === 2"
              type="warning"
              link
              size="small"
              @click="handleOffline(row)"
            >
              下架
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="info"
              link
              size="small"
              @click="$router.push(`/employer/publish?editId=${row.id}`)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && jobs.length === 0" description="暂无发布的职位" />

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
import { getMyJobs, submitAudit, offlineJob } from '@/api/job'
import { formatSalary, jobStatusMap, jobStatusColorMap } from '@/utils/format'
import { EditPen } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const jobs = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const statusMap = jobStatusMap
const statusColorMap = jobStatusColorMap

onMounted(() => {
  loadJobs()
})

const loadJobs = async () => {
  loading.value = true
  try {
    const res = await getMyJobs({ page: page.value, size: size.value })
    if (res.data) {
      jobs.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载职位列表失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSubmitAudit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该职位进行审核吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await submitAudit(row.id)
    ElMessage.success('已提交审核')
    loadJobs()
  } catch (e) {
    // 用户取消
  }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架该职位吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await offlineJob(row.id)
    ElMessage.success('已下架')
    loadJobs()
  } catch (e) {
    // 用户取消
  }
}

watch([page, size], () => {
  loadJobs()
})
</script>

<style scoped>
.my-jobs-page {
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

.salary-text {
  color: #F56C6C;
  font-weight: 600;
}
</style>
