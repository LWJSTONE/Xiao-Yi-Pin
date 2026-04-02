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

      <el-table :data="filteredDictList" stripe style="width: 100%" v-loading="loading">
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
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      </el-table>

      <el-empty v-if="!loading && filteredDictList.length === 0" description="暂无数据" />
    </el-card>

    <!-- 字典类型说明 -->
    <el-card shadow="never" class="info-card" style="margin-top: 20px;">
      <template #header>
        <span class="card-title">📖 字典说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="salary_type">薪资类型（0-时薪, 1-日薪, 2-周薪, 3-月薪, 4-次薪）</el-descriptions-item>
        <el-descriptions-item label="job_status">职位状态（0-草稿, 1-待审核, 2-已通过, 3-已拒绝, 4-招聘中, 5-已下架）</el-descriptions-item>
        <el-descriptions-item label="audit_status">审核状态（0-待审核, 1-已通过, 2-已拒绝）</el-descriptions-item>
        <el-descriptions-item label="apply_status">申请状态（0-待审核, 1-已通过, 2-已拒绝, 3-已录用, 4-已取消）</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(false)
const typeFilter = ref('')

// 本地字典数据（后续可对接后端接口）
const dictList = ref([
  // 薪资类型
  { dictType: 'salary_type', dictCode: '0', dictLabel: '时薪', sortOrder: 1, status: 1, remark: '按小时计费' },
  { dictType: 'salary_type', dictCode: '1', dictLabel: '日薪', sortOrder: 2, status: 1, remark: '按天计费' },
  { dictType: 'salary_type', dictCode: '2', dictLabel: '周薪', sortOrder: 3, status: 1, remark: '按周计费' },
  { dictType: 'salary_type', dictCode: '3', dictLabel: '月薪', sortOrder: 4, status: 1, remark: '按月计费' },
  { dictType: 'salary_type', dictCode: '4', dictLabel: '次薪', sortOrder: 5, status: 1, remark: '按次计费' },
  // 职位状态
  { dictType: 'job_status', dictCode: '0', dictLabel: '草稿', sortOrder: 1, status: 1, remark: '草稿状态' },
  { dictType: 'job_status', dictCode: '1', dictLabel: '待审核', sortOrder: 2, status: 1, remark: '已提交待审核' },
  { dictType: 'job_status', dictCode: '2', dictLabel: '已通过', sortOrder: 3, status: 1, remark: '审核通过' },
  { dictType: 'job_status', dictCode: '3', dictLabel: '已拒绝', sortOrder: 4, status: 1, remark: '审核拒绝' },
  { dictType: 'job_status', dictCode: '4', dictLabel: '招聘中', sortOrder: 5, status: 1, remark: '正在招聘' },
  { dictType: 'job_status', dictCode: '5', dictLabel: '已下架', sortOrder: 6, status: 1, remark: '已下架' },
  // 审核状态
  { dictType: 'audit_status', dictCode: '0', dictLabel: '待审核', sortOrder: 1, status: 1, remark: '待审核' },
  { dictType: 'audit_status', dictCode: '1', dictLabel: '已通过', sortOrder: 2, status: 1, remark: '审核通过' },
  { dictType: 'audit_status', dictCode: '2', dictLabel: '已拒绝', sortOrder: 3, status: 1, remark: '审核拒绝' },
  // 申请状态
  { dictType: 'apply_status', dictCode: '0', dictLabel: '待审核', sortOrder: 1, status: 1, remark: '待雇主审核' },
  { dictType: 'apply_status', dictCode: '1', dictLabel: '已通过', sortOrder: 2, status: 1, remark: '雇主已通过' },
  { dictType: 'apply_status', dictCode: '2', dictLabel: '已拒绝', sortOrder: 3, status: 1, remark: '雇主已拒绝' },
  { dictType: 'apply_status', dictCode: '3', dictLabel: '已录用', sortOrder: 4, status: 1, remark: '已录用' },
  { dictType: 'apply_status', dictCode: '4', dictLabel: '已取消', sortOrder: 5, status: 1, remark: '学生已取消' }
])

const filteredDictList = computed(() => {
  if (!typeFilter.value) return dictList.value
  return dictList.value.filter(item => item.dictType === typeFilter.value)
})

onMounted(() => {
  loadDictList()
})

const loadDictList = () => {
  // 后续可对接后端 API
  // const res = await getDictList({ dictType: typeFilter.value })
  loading.value = false
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
