<template>
  <div class="my-orders-page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">📋 我的订单</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="orders" stripe style="width: 100%">
        <el-table-column prop="jobTitle" label="职位名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column label="金额" width="120" align="center">
          <template #default="{ row }">
            <span class="amount-text">¥ {{ row.amount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结算状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="settleStatusColorMap[row.settlementStatus] || 'info'" size="small">
              {{ settleStatusMap[row.settlementStatus] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.settlementStatus === 0"
              type="warning"
              link
              size="small"
              @click="handleSettle(row)"
            >
              结算
            </el-button>
            <el-button
              v-if="row.settlementStatus === 1"
              type="primary"
              link
              size="small"
              @click="handleReview(row)"
            >
              评价
            </el-button>
            <el-button
              v-if="row.settlementStatus === 1"
              type="info"
              link
              size="small"
              @click="handleViewReviews(row)"
            >
              查看评价
            </el-button>
            <span v-if="row.settlementStatus === 2" class="no-action">已完成</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />

      <Pagination
        v-if="total > 0"
        :total="total"
        v-model:page="page"
        v-model:size="size"
      />
    </el-card>

    <!-- 结算对话框 -->
    <el-dialog v-model="settleDialogVisible" title="订单结算" width="480px">
      <el-form :model="settleForm" label-width="80px">
        <el-form-item label="结算金额">
          <el-input-number v-model="settleForm.amount" :min="0" :precision="2" style="width: 200px;" />
          <span class="form-tip">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="settleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="settleLoading" @click="confirmSettle">确认结算</el-button>
      </template>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="提交评价" width="480px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['很差','较差','一般','较好','很好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.comment" type="textarea" :rows="4" placeholder="请输入评价内容" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="confirmReview">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- 查看评价对话框 -->
    <el-dialog v-model="reviewsDialogVisible" title="订单评价" width="560px">
      <div v-loading="reviewsLoading">
        <el-empty v-if="!reviewsLoading && reviewsList.length === 0" description="暂无评价" />
        <div v-for="review in reviewsList" :key="review.id" class="review-item">
          <div class="review-header">
            <span class="reviewer-name">{{ review.reviewerName || '匿名用户' }}</span>
            <el-rate :model-value="review.rating" disabled show-score score-template="{value}分" />
          </div>
          <div class="review-comment">{{ review.comment || '无评价内容' }}</div>
          <div class="review-time">{{ formatDate(review.createTime) }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getMyOrders, settleOrder, submitReview, getOrderReviews } from '@/api/order'
import { formatDate, settleStatusMap, settleStatusColorMap } from '@/utils/format'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const settleDialogVisible = ref(false)
const settleLoading = ref(false)
const currentOrderId = ref('')
const settleForm = reactive({ amount: 0 })

const reviewDialogVisible = ref(false)
const reviewLoading = ref(false)
const reviewForm = reactive({ targetId: null, rating: 5, comment: '', type: '' })



onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getMyOrders({ page: page.value, size: size.value })
    if (res.data) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载订单列表失败：', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSettle = (row) => {
  currentOrderId.value = row.id
  settleForm.amount = row.amount || 0
  settleDialogVisible.value = true
}

const confirmSettle = async () => {
  if (!settleForm.amount || settleForm.amount <= 0) {
    ElMessage.warning('请输入有效的结算金额')
    return
  }
  settleLoading.value = true
  try {
    await settleOrder(currentOrderId.value, { amount: settleForm.amount })
    ElMessage.success('结算成功')
    settleDialogVisible.value = false
    loadOrders()
  } catch (error) {
    // 错误已处理
  } finally {
    settleLoading.value = false
  }
}

const handleReview = (row) => {
  currentOrderId.value = row.id
  reviewForm.targetId = row.studentId
  reviewForm.type = 'EMPLOYER'
  reviewForm.rating = 5
  reviewForm.comment = ''
  reviewDialogVisible.value = true
}

const confirmReview = async () => {
  if (!reviewForm.comment) {
    ElMessage.warning('请输入评价内容')
    return
  }
  reviewLoading.value = true
  try {
    await submitReview(currentOrderId.value, {
      targetId: reviewForm.targetId,
      rating: reviewForm.rating,
      comment: reviewForm.comment,
      type: reviewForm.type
    })
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    loadOrders()
  } catch (error) {
    // 错误已处理
  } finally {
    reviewLoading.value = false
  }
}

const reviewsDialogVisible = ref(false)
const reviewsList = ref([])
const reviewsLoading = ref(false)

const handleViewReviews = async (row) => {
  reviewsDialogVisible.value = true
  reviewsLoading.value = true
  try {
    const res = await getOrderReviews(row.id)
    reviewsList.value = res.data || []
  } catch (e) {
    console.error('获取评价失败：', e)
    ElMessage.error('获取评价失败')
  } finally {
    reviewsLoading.value = false
  }
}

watch([page, size], () => {
  loadOrders()
})
</script>

<style scoped>
.my-orders-page {
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

.amount-text {
  color: #F56C6C;
  font-weight: 600;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
}

.no-action {
  color: #c0c4cc;
  font-size: 13px;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child {
  border-bottom: none;
}
.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.reviewer-name {
  font-weight: 500;
  color: #303133;
}
.review-comment {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
.review-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 6px;
}
</style>
