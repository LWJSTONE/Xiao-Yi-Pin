<template>
  <div class="student-dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>👋 你好，{{ authStore.userInfo.username || '同学' }}</h2>
          <p>欢迎来到校园兼职平台，祝你找到心仪的兼职工作！</p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" size="large" @click="$router.push('/student/jobs')">
            <el-icon><Search /></el-icon>
            浏览兼职
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #79bbff);">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.applications }}</div>
            <div class="stat-label">我的报名</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95d475);">
            <el-icon :size="28"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.hired }}</div>
            <div class="stat-label">已录用</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #eebe77);">
            <el-icon :size="28"><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orders }}</div>
            <div class="stat-label">我的订单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新兼职 -->
    <el-card class="recent-jobs-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">🔥 最新兼职</span>
          <el-button type="primary" link @click="$router.push('/student/jobs')">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-loading="loading" class="jobs-list">
        <el-empty v-if="!loading && recentJobs.length === 0" description="暂无兼职信息" />
        <div v-for="job in recentJobs" :key="job.id" class="job-item" @click="$router.push(`/student/jobs/${job.id}`)">
          <div class="job-item-info">
            <h4 class="job-item-title">{{ job.title }}</h4>
            <div class="job-item-meta">
              <el-tag size="small" type="info">{{ job.categoryName || '未分类' }}</el-tag>
              <span class="job-item-location">
                <el-icon><Location /></el-icon>
                {{ job.location || '不限' }}
              </span>
            </div>
          </div>
          <div class="job-item-salary">
            {{ formatSalary(job.salaryAmount, job.salaryType) }}
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'
import { getJobList } from '@/api/job'
import { getMyApplications, getMyOrders } from '@/api/order'
import { formatSalary } from '@/utils/format'

const authStore = useAuthStore()
const loading = ref(false)
const recentJobs = ref([])
const stats = reactive({
  applications: 0,
  hired: 0,
  orders: 0
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    // 加载最新兼职（前5条）
    const jobRes = await getJobList({ page: 1, size: 5 })
    if (jobRes.data) {
      recentJobs.value = jobRes.data.records || []
    }

    // 加载我的报名统计
    try {
      const appRes = await getMyApplications({ page: 1, size: 100 })
      if (appRes.data) {
        const applications = appRes.data.records || []
        stats.applications = appRes.data.total || 0
        stats.hired = applications.filter(a => a.status === 3).length
      }
    } catch (e) {
      ElMessage.warning('加载报名统计失败')
    }

    // 加载我的订单统计
    try {
      const orderRes = await getMyOrders({ page: 1, size: 100 })
      if (orderRes.data) {
        stats.orders = orderRes.data.total || 0
      }
    } catch (e) {
      // 订单接口可能因权限限制而失败
    }
  } catch (error) {
    console.error('加载数据失败：', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.student-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 24px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome-card :deep(.el-card__body) {
  padding: 32px;
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-text h2 {
  color: #fff;
  font-size: 24px;
  margin: 0 0 8px;
}

.welcome-text p {
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
  margin: 0;
}

.welcome-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: #fff;
}

.welcome-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.35);
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.recent-jobs-card {
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

.jobs-list {
  max-height: 400px;
  overflow-y: auto;
}

.job-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.job-item:last-child {
  border-bottom: none;
}

.job-item:hover {
  background-color: #f5f7fa;
  margin: 0 -16px;
  padding: 16px;
  border-radius: 8px;
}

.job-item-info {
  flex: 1;
}

.job-item-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 6px;
}

.job-item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.job-item-location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}

.job-item-salary {
  font-size: 16px;
  font-weight: 600;
  color: #F56C6C;
  flex-shrink: 0;
}
</style>
