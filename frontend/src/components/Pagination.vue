<template>
  <div class="pagination-wrapper">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="currentSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  page: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['update:page', 'update:size'])

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

const currentSize = computed({
  get: () => props.size,
  set: (val) => emit('update:size', val)
})

const handleSizeChange = (val) => {
  emit('update:size', val)
  emit('update:page', 1)
}

const handleCurrentChange = (val) => {
  emit('update:page', val)
}
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px 0;
}
</style>
