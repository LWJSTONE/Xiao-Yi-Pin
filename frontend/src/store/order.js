import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMyApplications, getMyOrders } from '@/api/order'

export const useOrderStore = defineStore('order', () => {
  // 我的申请列表
  const applications = ref([])
  const applicationsTotal = ref(0)

  // 我的订单列表
  const orders = ref([])
  const ordersTotal = ref(0)

  /**
   * 获取我的申请列表
   * @param {Object} params - 查询参数
   */
  async function fetchApplications(params) {
    try {
      const res = await getMyApplications(params)
      if (res.data) {
        applications.value = res.data.records || []
        applicationsTotal.value = res.data.total || 0
      }
      return res.data
    } catch (error) {
      console.error('获取申请列表失败：', error)
      throw error
    }
  }

  /**
   * 获取我的订单列表
   * @param {Object} params - 查询参数
   */
  async function fetchOrders(params) {
    try {
      const res = await getMyOrders(params)
      if (res.data) {
        orders.value = res.data.records || []
        ordersTotal.value = res.data.total || 0
      }
      return res.data
    } catch (error) {
      console.error('获取订单列表失败：', error)
      throw error
    }
  }

  return {
    applications,
    applicationsTotal,
    orders,
    ordersTotal,
    fetchApplications,
    fetchOrders
  }
})
