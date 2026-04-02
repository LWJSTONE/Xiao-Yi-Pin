<template>
  <div class="job-list-page">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索职位名称"
            clearable
            :prefix-icon="Search"
            style="width: 220px;"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.categoryId"
            placeholder="选择分类"
            clearable
            style="width: 160px;"
          >
            <el-option
              v-for="cat in categoryOptions"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.location"
            placeholder="工作地点"
            clearable
            :prefix-icon="Location"
            style="width: 160px;"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.salaryType"
            placeholder="薪资类型"
            clearable
            style="width: 120px;"
          >
            <el-option label="时薪" value="0" />
            <el-option label="日薪" value="1" />
            <el-option label="周薪" value="2" />
            <el-option label="月薪" value="3" />
            <el-option label="次薪" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="job-list-container">
      <div v-loading="loading" class="job-grid">
        <el-empty v-if="!loading && jobList.length === 0" description="暂无匹配的兼职信息" />
        <JobCard
          v-for="job in jobList"
          :key="job.id"
          :job="job"
        />
      </div>

      <Pagination
        v-if="total > 0"
        :total="total"
        v-model:page="page"
        v-model:size="size"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useJobStore } from '@/store/job'
import { getCategories } from '@/api/job'
import { Search, Location, Refresh } from '@element-plus/icons-vue'
import JobCard from '@/components/JobCard.vue'
import Pagination from '@/components/Pagination.vue'

const jobStore = useJobStore()
const loading = ref(false)
const jobList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const categoryOptions = ref([])

const searchForm = reactive({
  keyword: '',
  categoryId: '',
  location: '',
  salaryType: ''
})

onMounted(() => {
  loadCategories()
  loadJobs()
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    // 展平分类树
    const flatList = []
    const flatten = (list) => {
      if (!list) return
      list.forEach(item => {
        flatList.push(item)
        if (item.children && item.children.length > 0) {
          flatten(item.children)
        }
      })
    }
    flatten(res.data)
    categoryOptions.value = flatList
  } catch (e) {
    // 忽略
  }
}

const loadJobs = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      ...searchForm
    }
    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    const res = await jobStore.fetchJobs(params)
    jobList.value = jobStore.jobList
    total.value = jobStore.total
  } catch (e) {
    console.error('加载职位列表失败：', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadJobs()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = ''
  searchForm.location = ''
  searchForm.salaryType = ''
  page.value = 1
  loadJobs()
}

watch([page, size], () => {
  loadJobs()
})
</script>

<style scoped>
.job-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.job-list-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.job-grid {
  min-height: 200px;
}
</style>
