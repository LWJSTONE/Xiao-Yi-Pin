<template>
  <div class="employer-dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>🏢 你好，{{ authStore.userInfo.username || '雇主' }}</h2>
          <p>欢迎回到校园兼职平台管理后台</p>
        </div>
        <el-button type="primary" size="large" @click="$router.push('/employer/publish')">
          <el-icon><EditPen /></el-icon>
          发布兼职
        </el-button>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #79bbff);">
            <el-icon :size="28"><Briefcase /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalJobs }}</div>
            <div class="stat-label">总职位数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95d475);">
            <el-icon :size="28"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.activeJobs }}</div>
            <div class="stat-label">招聘中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #eebe77);">
            <el-icon :size="28"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalApplications }}</div>
            <div class="stat-label">总报名数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #fab6b6);">
            <el-icon :size="28"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingReviews }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近报名 -->
    <el-card class="recent-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">📥 最近报名</span>
          <el-button type="primary" link @click="$router.push('/employer/applications')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-table :data="recentApplications" stripe style="width: 100%">
        <el-table-column prop="jobTitle" label="职位名称" min-width="180" show-overflow-tooltip />
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
      </el-table>
      <el-empty v-if="recentApplications.length === 0" description="暂无报名" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { getMyJobs } from '@/api/job'
import { getJobApplications } from '@/api/order'
import { formatDate, applyStatusMap, applyStatusColorMap } from '@/utils/format'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const recentApplications = ref([])
const stats = reactive({
  totalJobs: 0,
  activeJobs: 0,
  totalApplications: 0,
  pendingReviews: 0
})

const statusMap = applyStatusMap
const statusColorMap = applyStatusColorMap

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    // 加载职位统计 - 总数
    const jobRes = await getMyJobs({ page: 1, size: 1 })
    if (jobRes.data) {
      stats.totalJobs = jobRes.data.total || 0
    }

    // 加载招聘中的职位数（status=2）
    try {
      const activeRes = await getMyJobs({ page: 1, size: 1, status: 2 })
      if (activeRes.data) {
        stats.activeJobs = activeRes.data.total || 0
      }
    } catch (e) {
      // 忽略
    }

    // 加载最近报名（后端已修复：无jobId时只返回该雇主的申请）
    try {
      const appRes = await getJobApplications({ page: 1, size: 5 })
      if (appRes.data) {
        recentApplications.value = appRes.data.records || []
        stats.totalApplications = appRes.data.total || 0
      }
    } catch (e) {
      ElMessage.warning('加载报名数据失败')
    }

    // 从最近报名中统计待审核数量（仅从前5条中估算）
    stats.pendingReviews = recentApplications.value.filter(a => a.status === 0).length
  } catch (error) {
    console.error('加载数据失败：', error)
    ElMessage.error('加载数据失败')
  }
}
</script>

<style scoped>
.employer-dashboard {
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

.recent-card {
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
</style>
