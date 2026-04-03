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
            {{ formatDate(row.applyTime) }}
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

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="480px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
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
    ElMessage.error('加载职位列表失败')
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

const rejectDialogVisible = ref(false)
const currentRejectAppId = ref('')
const rejectForm = reactive({ rejectReason: '' })

const handleReview = async (row, status) => {
  if (status === 2) {
    // 拒绝需要填写原因
    currentRejectAppId.value = row.id
    rejectForm.rejectReason = ''
    rejectDialogVisible.value = true
    return
  }
  const statusText = status === 1 ? '通过' : '录用'
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

const confirmReject = async () => {
  if (!rejectForm.rejectReason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    await reviewApplication(currentRejectAppId.value, { status: 2, rejectReason: rejectForm.rejectReason })
    ElMessage.success('已拒绝该申请')
    rejectDialogVisible.value = false
    loadApplications()
  } catch (error) {
    // 错误已处理
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
