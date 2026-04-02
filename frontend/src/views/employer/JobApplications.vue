<template>
  <div class="job-applications-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">📥 报名管理</span>
          <div class="filter-section">
            <el-select
              v-model="selectedJobId"
              placeholder="选择职位"
              clearable
              style="width: 200px;"
              @change="handleFilter"
            >
              <el-option
                v-for="job in jobOptions"
                :key="job.id"
                :label="job.title"
                :value="job.id"
              />
            </el-select>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="applications" stripe style="width: 100%">
        <el-table-column prop="jobTitle" label="职位名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColorMap[row.status] || 'info'" size="small">
              {{ statusMap[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button
                type="success"
                link
                size="small"
                @click="handleReview(row, 1)"
              >
                通过
              </el-button>
              <el-button
                type="danger"
                link
                size="small"
                @click="handleReview(row, 2)"
              >
                拒绝
              </el-button>
            </template>
            <template v-if="row.status === 1">
              <el-button
                type="primary"
                link
                size="small"
                @click="handleReview(row, 3)"
              >
                录用
              </el-button>
              <el-button
                type="danger"
                link
                size="small"
                @click="handleReview(row, 2)"
              >
                拒绝
              </el-button>
            </template>
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
import { useRoute } from 'vue-router'
import { getMyJobs } from '@/api/job'
import { getJobApplications, reviewApplication } from '@/api/order'
import { formatDate, applyStatusMap, applyStatusColorMap } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const route = useRoute()
const loading = ref(false)
const applications = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const selectedJobId = ref('')
const jobOptions = ref([])

const statusMap = applyStatusMap
const statusColorMap = applyStatusColorMap

onMounted(() => {
  // 从路由参数获取 jobId
  if (route.query.jobId) {
    selectedJobId.value = route.query.jobId
  }
  loadJobOptions()
  loadApplications()
})

const loadJobOptions = async () => {
  try {
    const res = await getMyJobs({ page: 1, size: 100 })
    if (res.data) {
      jobOptions.value = res.data.records || []
    }
  } catch (e) {
    // 忽略
  }
}

const loadApplications = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (selectedJobId.value) {
      params.jobId = selectedJobId.value
    }
    const res = await getJobApplications(params)
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

const handleFilter = () => {
  page.value = 1
  loadApplications()
}

const handleReview = async (row, status) => {
  const statusText = status === 1 ? '通过' : status === 2 ? '拒绝' : '录用'
  try {
    await ElMessageBox.confirm(`确定要${statusText}该申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await reviewApplication(row.id, { status })
    ElMessage.success(`已${statusText}`)
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
.job-applications-page {
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

.no-action {
  color: #c0c4cc;
}
</style>
