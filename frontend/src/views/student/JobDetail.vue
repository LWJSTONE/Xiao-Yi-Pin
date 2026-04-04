<template>
  <div class="job-detail-page">
    <div v-loading="loading" class="detail-container">
      <el-empty v-if="!loading && !job" description="职位不存在或已删除" />

      <template v-if="job">
        <el-card class="detail-card" shadow="never">
          <div class="detail-header">
            <div class="detail-title-section">
              <h1 class="detail-title">{{ job.title }}</h1>
              <div class="detail-tags">
                <el-tag :type="statusColor" size="large">{{ statusText }}</el-tag>
                <el-tag v-if="job.categoryName" type="info">{{ job.categoryName }}</el-tag>
              </div>
            </div>
            <div class="detail-salary">
              <span class="salary-amount">{{ formatSalary(job.salaryAmount, job.salaryType) }}</span>
            </div>
          </div>

          <el-divider />

          <div class="detail-info-grid">
            <div class="info-block">
              <div class="info-label">
                <el-icon><Location /></el-icon> 工作地点
              </div>
              <div class="info-value">{{ job.location || '不限' }}</div>
            </div>
            <div class="info-block">
              <div class="info-label">
                <el-icon><User /></el-icon> 发布者
              </div>
              <div class="info-value">{{ job.publisherName || '匿名' }}</div>
            </div>
            <div class="info-block">
              <div class="info-label">
                <el-icon><Calendar /></el-icon> 开始时间
              </div>
              <div class="info-value">{{ formatDateOnly(job.startTime) }}</div>
            </div>
            <div class="info-block">
              <div class="info-label">
                <el-icon><Clock /></el-icon> 结束时间
              </div>
              <div class="info-value">{{ formatDateOnly(job.endTime) }}</div>
            </div>
          </div>

          <el-divider />

          <div class="detail-section">
            <h3 class="section-title">📝 职位描述</h3>
            <p class="description-text">{{ job.description || '暂无描述' }}</p>
          </div>

          <el-divider />

          <div class="detail-section">
            <h3 class="section-title">👥 招聘进度</h3>
            <div class="recruit-progress">
              <div class="progress-text">
                已录用 <strong>{{ job.hiredNum || 0 }}</strong> 人 / 共招 <strong>{{ job.recruitNum || 0 }}</strong> 人
              </div>
              <el-progress
                :percentage="progressPercentage"
                :stroke-width="12"
                :color="progressColor"
                style="margin-top: 8px;"
              />
            </div>
          </div>
        </el-card>

        <!-- 申请区域（仅已发布状态可申请） -->
        <el-card v-if="job.status === 2" class="apply-card" shadow="never">
          <div class="apply-section">
            <div class="apply-info">
              <el-input
                v-model="resumeUrl"
                placeholder="请输入简历链接（选填）"
                clearable
              />
            </div>
            <el-button
              type="primary"
              size="large"
              :loading="applyLoading"
              @click="handleApply"
            >
              <el-icon><Position /></el-icon>
              立即申请
            </el-button>
          </div>
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getJobDetail } from '@/api/job'
import { applyJob } from '@/api/order'
import { formatSalary, formatDateOnly, jobStatusMap, jobStatusColorMap } from '@/utils/format'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const applyLoading = ref(false)
const job = ref(null)
const resumeUrl = ref('')

const statusText = computed(() => jobStatusMap[job.value?.status] || '未知')
const statusColor = computed(() => jobStatusColorMap[job.value?.status] || 'info')

const progressPercentage = computed(() => {
  if (!job.value?.recruitNum) return 0
  return Math.min(Math.round(((job.value.hiredNum || 0) / job.value.recruitNum) * 100), 100)
})

const progressColor = computed(() => {
  const p = progressPercentage.value
  if (p >= 80) return '#F56C6C'
  if (p >= 50) return '#E6A23C'
  return '#409EFF'
})

onMounted(() => {
  loadJobDetail()
})

const loadJobDetail = async () => {
  loading.value = true
  try {
    const res = await getJobDetail(route.params.id)
    job.value = res.data
  } catch (error) {
    console.error('加载职位详情失败：', error)
  } finally {
    loading.value = false
  }
}

const handleApply = async () => {
  applyLoading.value = true
  try {
    await applyJob(route.params.id, { resumeUrl: resumeUrl.value || '' })
    ElMessage.success('申请成功，请等待审核')
    router.push('/student/applications')
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    applyLoading.value = false
  }
}
</script>

<style scoped>
.job-detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.detail-container {
  min-height: 400px;
}

.detail-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
}

.detail-title-section {
  flex: 1;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.salary-amount {
  font-size: 28px;
  font-weight: 700;
  color: #F56C6C;
  white-space: nowrap;
}

.detail-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-block {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.info-value {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.detail-section {
  margin-bottom: 4px;
}

.section-title {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.description-text {
  font-size: 15px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}

.recruit-progress {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.progress-text {
  font-size: 15px;
  color: #606266;
}

.progress-text strong {
  color: #409EFF;
  font-size: 18px;
}

.apply-card {
  border-radius: 12px;
}

.apply-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.apply-info {
  flex: 1;
}
</style>
