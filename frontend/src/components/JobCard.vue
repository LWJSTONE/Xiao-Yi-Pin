<template>
  <el-card class="job-card" shadow="hover" @click="handleClick">
    <div class="job-card-body">
      <div class="job-header">
        <h3 class="job-title">{{ job.title || '暂无标题' }}</h3>
        <el-tag v-if="job.categoryName" size="small" type="info" class="job-category">
          {{ job.categoryName }}
        </el-tag>
      </div>
      <div class="job-info">
        <div class="info-item">
          <el-icon><Location /></el-icon>
          <span>{{ job.location || '不限' }}</span>
        </div>
        <div class="info-item salary">
          <el-icon><Money /></el-icon>
          <span class="salary-text">{{ formattedSalary }}</span>
        </div>
      </div>
      <div class="job-meta">
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ job.publisherName || '匿名' }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span>{{ formattedTime }}</span>
        </div>
      </div>
      <div class="job-progress" v-if="job.recruitNum">
        <div class="progress-info">
          <span>招聘进度</span>
          <span>{{ job.hiredNum || 0 }} / {{ job.recruitNum }}</span>
        </div>
        <el-progress
          :percentage="progressPercentage"
          :stroke-width="8"
          :color="progressColor"
          :show-text="false"
        />
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatSalary, formatDate } from '@/utils/format'

const props = defineProps({
  job: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const router = useRouter()

const formattedSalary = computed(() => {
  return formatSalary(props.job.salaryAmount, props.job.salaryType)
})

const formattedTime = computed(() => {
  return formatDate(props.job.createTime)
})

const progressPercentage = computed(() => {
  if (!props.job.recruitNum) return 0
  const hired = props.job.hiredNum || 0
  return Math.min(Math.round((hired / props.job.recruitNum) * 100), 100)
})

const progressColor = computed(() => {
  const p = progressPercentage.value
  if (p >= 80) return '#F56C6C'
  if (p >= 50) return '#E6A23C'
  return '#409EFF'
})

const handleClick = () => {
  if (props.job.id) {
    router.push(`/student/jobs/${props.job.id}`)
  }
}
</script>

<style scoped>
.job-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin-bottom: 16px;
}

.job-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.job-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.job-title {
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 8px;
}

.job-category {
  flex-shrink: 0;
}

.job-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.info-item .el-icon {
  font-size: 16px;
}

.salary-text {
  color: #F56C6C;
  font-weight: 600;
  font-size: 16px;
}

.job-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 13px;
}

.meta-item .el-icon {
  font-size: 14px;
}

.job-progress {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 10px 12px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}
</style>
