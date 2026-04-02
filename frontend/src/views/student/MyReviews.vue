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
        <el-table-column prop="targetName" label="职位/雇主" min-width="180" show-overflow-tooltip />
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

      <Pagination
        v-if="total > 0"
        :total="total"
        v-model:page="page"
        v-model:size="size"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getMyOrders } from '@/api/order'
import { formatDate } from '@/utils/format'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const reviews = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getMyOrders({ page: page.value, size: size.value })
    if (res.data) {
      // 从订单中提取评价信息
      reviews.value = (res.data.records || []).filter(order => order.review)
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载评价列表失败：', error)
  } finally {
    loading.value = false
  }
}

watch([page, size], () => {
  loadReviews()
})
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
