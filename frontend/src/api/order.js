import request from './request'

/**
 * 申请职位
 * @param {string} jobId - 职位ID
 * @param {Object} data - { resumeUrl }
 */
export function applyJob(jobId, data) {
  return request.post(`/v1/order/apply/${jobId}`, data)
}

/**
 * 审核申请（雇主）
 * @param {string} appId - 申请ID
 * @param {Object} params - { status }
 */
export function reviewApplication(appId, params) {
  return request.put(`/v1/order/apply/${appId}/review`, null, { params })
}

/**
 * 获取我的申请列表（学生）
 * @param {Object} params - { page, size }
 */
export function getMyApplications(params) {
  return request.get('/v1/order/my/applications', { params })
}

/**
 * 获取我发布的职位的申请列表（雇主）
 * @param {Object} params - { jobId, page, size }
 */
export function getJobApplications(params) {
  return request.get('/v1/order/my/jobs/applications', { params })
}

/**
 * 结算订单
 * @param {string} orderId - 订单ID
 * @param {Object} data - { amount }
 */
export function settleOrder(orderId, data) {
  return request.post(`/v1/order/${orderId}/settle`, data)
}

/**
 * 获取我的订单列表
 * @param {Object} params - { page, size }
 */
export function getMyOrders(params) {
  return request.get('/v1/order/my/orders', { params })
}

/**
 * 提交评价
 * @param {string} orderId - 订单ID
 * @param {Object} data - { rating, comment }
 */
export function submitReview(orderId, data) {
  return request.post(`/v1/order/${orderId}/review`, data)
}

/**
 * 获取我的评价列表
 */
export function getMyReviews() {
  return request.get('/v1/order/my/reviews')
}

/**
 * 取消申请（学生）
 * @param {string} appId - 申请ID
 */
export function cancelApplication(appId) {
  return request.put(`/v1/order/apply/${appId}/cancel`)
}

/**
 * 获取订单评价列表
 * @param {string} orderId - 订单ID
 */
export function getOrderReviews(orderId) {
  return request.get(`/v1/order/${orderId}/reviews`)
}

/**
 * 获取所有申请列表（管理员）
 * @param {Object} params - { page, size }
 */
export function getAllApplications(params) {
  return request.get('/v1/order/admin/applications', { params })
}

/**
 * 获取所有订单列表（管理员）
 * @param {Object} params - { page, size }
 */
export function getAllOrders(params) {
  return request.get('/v1/order/admin/orders', { params })
}
