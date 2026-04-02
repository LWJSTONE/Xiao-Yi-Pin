<template>
  <div class="audit-jobs-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">✅ 职位审核</span>
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 140px;" @change="handleSearch">
            <el-option label="待审核" value="1" />
            <el-option label="已通过" value="2" />
            <el-option label="已拒绝" value="3" />
          </el-select>
        </div>
      </template>

      <el-table v-loading="loading" :data="jobs" stripe style="width: 100%">
        <el-table-column prop="title" label="职位名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column label="分类" width="100" align="center">
          <template #default="{ row }">
            {{ row.categoryName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="薪资" width="130" align="center">
          <template #default="{ row }">
            <span class="salary-text">{{ formatSalary(row.salaryAmount, row.salaryType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="地点" width="120" align="center">
          <template #default="{ row }">
            {{ row.location || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusColorMap[row.status] || 'info'" size="small">
              {{ statusMap[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 1">
              <el-button type="success" link size="small" @click="handleAudit(row, 2)">通过</el-button>
              <el-button type="danger" link size="small" @click="handleReject(row)">拒绝</el-button>
            </template>
            <span v-else class="no-action">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && jobs.length === 0" description="暂无待审核职位" />

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
          <el-input v-model="rejectForm.auditRemark" type="textarea" :rows="4" placeholder="请输入拒绝原因" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditLoading" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getJobList, auditJob } from '@/api/job'
import { formatSalary, formatDate, jobStatusMap, jobStatusColorMap } from '@/utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const jobs = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const statusFilter = ref('1')

const rejectDialogVisible = ref(false)
const auditLoading = ref(false)
const currentJobId = ref('')
const rejectForm = reactive({ auditRemark: '' })

const statusMap = jobStatusMap
const statusColorMap = jobStatusColorMap

onMounted(() => {
  loadJobs()
})

const loadJobs = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (statusFilter.value) {
      params.auditStatus = statusFilter.value
    }
    const res = await getJobList(params)
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

const handleSearch = () => {
  page.value = 1
  loadJobs()
}

const handleAudit = async (row, status) => {
  try {
    await ElMessageBox.confirm('确定要通过该职位的审核吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    auditLoading.value = true
    await auditJob(row.id, { auditStatus: status, auditRemark: '' })
    ElMessage.success('审核通过')
    loadJobs()
  } catch (e) {
    // 用户取消
  } finally {
    auditLoading.value = false
  }
}

const handleReject = (row) => {
  currentJobId.value = row.id
  rejectForm.auditRemark = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.auditRemark) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  auditLoading.value = true
  try {
    await auditJob(currentJobId.value, { auditStatus: 3, auditRemark: rejectForm.auditRemark })
    ElMessage.success('已拒绝该职位')
    rejectDialogVisible.value = false
    loadJobs()
  } catch (error) {
    // 错误已处理
  } finally {
    auditLoading.value = false
  }
}

watch([page, size], () => {
  loadJobs()
})
</script>

<style scoped>
.audit-jobs-page {
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

.no-action {
  color: #c0c4cc;
  font-size: 13px;
}
</style>
