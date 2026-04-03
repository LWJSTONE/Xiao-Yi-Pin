import request from './request'

/**
 * 获取职位分类列表
 */
export function getCategories() {
  return request.get('/v1/job/categories')
}

/**
 * 发布职位
 * @param {Object} data - 职位信息
 */
export function publishJob(data) {
  return request.post('/v1/job/posts', data)
}

/**
 * 更新职位
 * @param {string} id - 职位ID
 * @param {Object} data - 职位信息
 */
export function updateJob(id, data) {
  return request.put(`/v1/job/posts/${id}`, data)
}

/**
 * 获取职位列表（分页搜索）
 * @param {Object} params - { keyword, categoryId, location, salaryType, page, size }
 */
export function getJobList(params) {
  return request.get('/v1/job/list', { params })
}

/**
 * 获取职位详情
 * @param {string} id - 职位ID
 */
export function getJobDetail(id) {
  return request.get(`/v1/job/posts/${id}`)
}

/**
 * 审核职位（管理员）
 * @param {string} id - 职位ID
 * @param {Object} data - { auditStatus, auditRemark }
 */
export function auditJob(id, data) {
  return request.put(`/v1/job/posts/${id}/audit`, data)
}

/**
 * 获取我发布的职位列表
 * @param {Object} params - { page, size }
 */
export function getMyJobs(params) {
  return request.get('/v1/job/my/jobs', { params })
}

/**
 * 提交审核
 * @param {string} id - 职位ID
 */
export function submitAudit(id) {
  return request.put(`/v1/job/posts/${id}/submit`)
}

/**
 * 下架职位
 * @param {string} id - 职位ID
 */
export function offlineJob(id) {
  return request.put(`/v1/job/posts/${id}/offline`)
}
