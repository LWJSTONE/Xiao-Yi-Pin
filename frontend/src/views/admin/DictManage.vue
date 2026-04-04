<template>
  <div class="dict-manage-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">🔧 数据字典管理</span>
          <el-select v-model="typeFilter" placeholder="字典类型" clearable style="width: 180px;" @change="loadDictList">
            <el-option label="薪资类型" value="salary_type" />
            <el-option label="职位状态" value="job_status" />
            <el-option label="审核状态" value="audit_status" />
            <el-option label="申请状态" value="apply_status" />
          </el-select>
        </div>
      </template>

      <el-table :data="dictList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="dictType" label="字典类型" width="140" />
        <el-table-column prop="dictCode" label="字典编码" width="120" align="center" />
        <el-table-column prop="dictLabel" label="字典标签" width="140" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && dictList.length === 0" description="暂无数据" />
    </el-card>

    <!-- 字典类型说明 -->
    <el-card shadow="never" class="info-card" style="margin-top: 20px;">
      <template #header>
        <span class="card-title">📖 字典说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="salary_type">薪资类型（0-时薪, 1-日薪, 2-周薪, 3-月薪, 4-次薪）</el-descriptions-item>
        <el-descriptions-item label="job_status">职位状态（0-草稿, 1-待审核, 2-已发布, 3-已拒绝, 4-已下架）</el-descriptions-item>
        <el-descriptions-item label="audit_status">审核状态（0-待审核, 1-已通过, 2-已拒绝）</el-descriptions-item>
        <el-descriptions-item label="apply_status">申请状态（0-待审核, 1-已通过, 2-已拒绝, 3-已录用, 4-已取消）</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDictList } from '@/api/user'

const loading = ref(false)
const typeFilter = ref('')
const dictList = ref([])

onMounted(() => {
  loadDictList()
})

const loadDictList = async () => {
  loading.value = true
  try {
    const params = {}
    if (typeFilter.value) {
      params.dictType = typeFilter.value
    }
    const res = await getDictList(params)
    if (res.data) {
      dictList.value = res.data || []
    }
  } catch (error) {
    console.error('加载字典列表失败：', error)
    ElMessage.error('加载字典列表失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dict-manage-page {
  max-width: 1200px;
  margin: 0 auto;
}

.table-card, .info-card {
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
