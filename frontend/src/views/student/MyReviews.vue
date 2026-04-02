<template>
  <div class="my-reviews-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">⭐ 我的评价</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="reviews"
        stripe
        style="width: 100%"
        class="review-table"
      >
        <el-table-column prop="targetName" label="被评价人" min-width="180" show-overflow-tooltip />
        <el-table-column label="评分" width="160" align="center">
          <template #default="{ row }">
            <el-rate
              :model-value="row.rating"
              disabled
              show-score
              score-template="{value}分"
            />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容" min-width="250" show-overflow-tooltip />
        <el-table-column label="评价时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && reviews.length === 0" description="暂无评价记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyReviews } from '@/api/order'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const reviews = ref([])

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getMyReviews()
    if (res.data) {
      reviews.value = res.data || []
    }
  } catch (error) {
    console.error('加载评价列表失败：', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.my-reviews-page {
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

.review-table {
  border-radius: 8px;
}
</style>
