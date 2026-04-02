import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCategories, getJobList } from '@/api/job'

export const useJobStore = defineStore('job', () => {
  // 分类列表
  const categories = ref([])

  // 职位列表
  const jobList = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * 获取分类列表
   */
  async function fetchCategories() {
    try {
      const res = await getCategories()
      categories.value = res.data || []
      return res.data
    } catch (error) {
      console.error('获取分类失败：', error)
      throw error
    }
  }

  /**
   * 获取职位列表
   * @param {Object} params - 查询参数
   */
  async function fetchJobs(params) {
    loading.value = true
    try {
      const res = await getJobList(params)
      if (res.data) {
        jobList.value = res.data.records || []
        total.value = res.data.total || 0
      }
      return res.data
    } catch (error) {
      console.error('获取职位列表失败：', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    categories,
    jobList,
    total,
    loading,
    fetchCategories,
    fetchJobs
  }
})
