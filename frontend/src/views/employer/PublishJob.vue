<template>
  <div class="publish-job-page">
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">{{ isEdit ? '✏️ 编辑兼职' : '📝 发布兼职' }}</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="publish-form"
        v-loading="loading"
      >
        <el-form-item label="职位标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入职位标题" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="职位分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'name', children: 'children', emitPath: false }"
            placeholder="请选择分类"
            clearable
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="薪资类型" prop="salaryType">
          <el-radio-group v-model="form.salaryType">
            <el-radio label="0">时薪</el-radio>
            <el-radio label="1">日薪</el-radio>
            <el-radio label="2">周薪</el-radio>
            <el-radio label="3">月薪</el-radio>
            <el-radio label="4">次薪</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="薪资金额" prop="salaryAmount">
          <el-input-number
            v-model="form.salaryAmount"
            :min="0"
            :precision="2"
            :step="10"
            placeholder="请输入金额"
            style="width: 200px;"
          />
          <span class="form-tip">（单位：元）</span>
        </el-form-item>

        <el-form-item label="工作地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入工作地点" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="请选择开始日期"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="请选择结束日期"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="招聘人数" prop="recruitNum">
          <el-input-number v-model="form.recruitNum" :min="1" :max="999" style="width: 200px;" />
          <span class="form-tip">（人）</span>
        </el-form-item>

        <el-form-item label="职位描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请详细描述工作内容、要求和待遇等信息"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitLoading" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '发布职位' }}
          </el-button>
          <el-button v-if="!isEdit" size="large" @click="handleSaveDraft">保存草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCategories, publishJob, updateJob, getJobDetail } from '@/api/job'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const submitLoading = ref(false)
const categoryTree = ref([])

const isEdit = computed(() => !!route.query.editId)

const form = reactive({
  title: '',
  categoryId: '',
  salaryType: '0',
  salaryAmount: 0,
  location: '',
  startTime: '',
  endTime: '',
  recruitNum: 1,
  description: ''
})

const rules = {
  title: [
    { required: true, message: '请输入职位标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度为 2 到 50 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择职位分类', trigger: 'change' }
  ],
  salaryType: [
    { required: true, message: '请选择薪资类型', trigger: 'change' }
  ],
  salaryAmount: [
    { required: true, message: '请输入薪资金额', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入工作地点', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  recruitNum: [
    { required: true, message: '请输入招聘人数', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入职位描述', trigger: 'blur' },
    { min: 10, message: '描述至少 10 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadCategories()
  if (isEdit.value) {
    loadJobDetail()
  }
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categoryTree.value = res.data || []
  } catch (e) {
    console.error('加载分类失败：', e)
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

const loadJobDetail = async () => {
  loading.value = true
  try {
    const res = await getJobDetail(route.query.editId)
    const job = res.data
    if (job) {
      form.title = job.title || ''
      form.categoryId = job.categoryId || ''
      form.salaryType = job.salaryType || '0'
      form.salaryAmount = job.salaryAmount || 0
      form.location = job.location || ''
      form.startTime = job.startTime || ''
      form.endTime = job.endTime || ''
      form.recruitNum = job.recruitNum || 1
      form.description = job.description || ''
    }
  } catch (error) {
    console.error('加载职位详情失败：', error)
    ElMessage.error('加载职位详情失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateJob(route.query.editId, { ...form })
        ElMessage.success('职位修改成功')
      } else {
        await publishJob({ ...form })
        ElMessage.success('职位发布成功，等待审核')
      }
      router.push('/employer/jobs')
    } catch (error) {
      // 错误已在拦截器中处理
    } finally {
      submitLoading.value = false
    }
  })
}

const handleSaveDraft = async () => {
  submitLoading.value = true
  try {
    await publishJob({ ...form })
    ElMessage.success('草稿保存成功')
    router.push('/employer/jobs')
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.publish-job-page {
  max-width: 800px;
  margin: 0 auto;
}

.form-card {
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

.publish-form {
  padding: 20px 0;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 13px;
}
</style>
