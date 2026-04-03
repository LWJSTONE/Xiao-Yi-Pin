<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #79bbff);">
            <el-icon :size="28"><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #95d475);">
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
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #eebe77);">
            <el-icon :size="28"><Document /></el-icon>
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
            <el-icon :size="28"><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalOrders }}</div>
            <div class="stat-label">总订单数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-row">
      <el-col :span="12">
        <el-card class="quick-card" shadow="hover" @click="$router.push('/admin/audit')">
          <div class="quick-content">
            <el-icon :size="40" color="#E6A23C"><CircleCheck /></el-icon>
            <div class="quick-info">
              <h3>职位审核</h3>
              <p>{{ stats.pendingAudit }} 个职位待审核</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="quick-card" shadow="hover" @click="$router.push('/admin/users')">
          <div class="quick-content">
            <el-icon :size="40" color="#409EFF"><UserFilled /></el-icon>
            <div class="quick-info">
              <h3>用户管理</h3>
              <p>{{ stats.totalUsers }} 个注册用户</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 平台概览 -->
    <el-card shadow="never" class="overview-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">📊 平台概览</span>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学生用户">{{ stats.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="雇主用户">{{ stats.employerCount }}</el-descriptions-item>
        <el-descriptions-item label="管理员">{{ stats.adminCount }}</el-descriptions-item>
        <el-descriptions-item label="招聘中职位">{{ stats.activeJobs }}</el-descriptions-item>
        <el-descriptions-item label="草稿职位">{{ stats.draftJobs }}</el-descriptions-item>
        <el-descriptions-item label="待审核职位">{{ stats.pendingAudit }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listUsers } from '@/api/user'
import { getJobList } from '@/api/job'
import { getAllApplications, getAllOrders } from '@/api/order'

const stats = reactive({
  totalUsers: 0,
  totalJobs: 0,
  totalApplications: 0,
  totalOrders: 0,
  studentCount: 0,
  employerCount: 0,
  adminCount: 0,
  activeJobs: 0,
  draftJobs: 0,
  pendingAudit: 0
})

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  try {
    // 加载用户统计 - 获取所有用户以统计角色分布
    const userRes = await listUsers({ page: 1, size: 1000 })
    if (userRes.data) {
      const users = userRes.data.records || []
      stats.totalUsers = userRes.data.total || 0
      stats.studentCount = users.filter(u => u.roleType === 'STUDENT').length
      stats.employerCount = users.filter(u => u.roleType === 'EMPLOYER').length
      stats.adminCount = users.filter(u => u.roleType === 'ADMIN').length
    }

    // 加载职位统计 - 分别查询不同状态的职位
    // 已发布职位（默认查询：status=2）
    try {
      const publishedRes = await getJobList({ page: 1, size: 1 })
      if (publishedRes.data) {
        stats.activeJobs = publishedRes.data.total || 0
      }
    } catch (e) {
      // 忽略
    }
    // 待审核职位（auditStatus=0 返回 audit_status=0 且 status=1 的职位）
    try {
      const pendingRes = await getJobList({ page: 1, size: 1, auditStatus: 0 })
      if (pendingRes.data) {
        stats.pendingAudit = pendingRes.data.total || 0
      }
    } catch (e) {
      // 忽略
    }
    // 草稿职位（status=0）
    try {
      const draftRes = await getJobList({ page: 1, size: 1, status: 0 })
      if (draftRes.data) {
        stats.draftJobs = draftRes.data.total || 0
      }
    } catch (e) {
      // 忽略
    }
    // 已下线职位（status=4）
    try {
      const offlineRes = await getJobList({ page: 1, size: 1, status: 4 })
      if (offlineRes.data) {
        stats.totalJobs = stats.activeJobs + stats.pendingAudit + stats.draftJobs + (offlineRes.data.total || 0)
      }
    } catch (e) {
      // 忽略，使用已有数据估算
      stats.totalJobs = stats.activeJobs + stats.pendingAudit + stats.draftJobs
    }

    // 加载报名总数统计
    try {
      const appRes = await getAllApplications({ page: 1, size: 1 })
      if (appRes.data) {
        stats.totalApplications = appRes.data.total || 0
      }
    } catch (e) {
      // 接口可能因权限限制而失败，使用0作为默认值
    }

    // 加载订单总数统计
    try {
      const orderRes = await getAllOrders({ page: 1, size: 1 })
      if (orderRes.data) {
        stats.totalOrders = orderRes.data.total || 0
      }
    } catch (e) {
      // 接口可能因权限限制而失败，使用0作为默认值
    }
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
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

.quick-row {
  margin-bottom: 24px;
}

.quick-card {
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s;
}

.quick-card:hover {
  transform: translateY(-2px);
}

.quick-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 0;
}

.quick-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px;
}

.quick-info p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.overview-card {
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
