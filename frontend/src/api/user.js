import request from './request'

/**
 * 获取个人信息
 */
export function getMyProfile() {
  return request.get('/v1/user/profile/me')
}

/**
 * 更新个人信息
 * @param {Object} data - 个人信息数据
 */
export function updateMyProfile(data) {
  return request.put('/v1/user/profile/me', data)
}

/**
 * 申请实名认证
 * @param {Object} data - { realName, idCard, idCardImage, verifyType }
 */
export function applyVerify(data) {
  return request.post('/v1/user/verify/apply', data)
}

/**
 * 获取实名认证状态
 */
export function getVerifyStatus() {
  return request.get('/v1/user/verify/status')
}

/**
 * 获取用户列表（管理员）
 * @param {Object} params - { keyword, status, page, size }
 */
export function listUsers(params) {
  return request.get('/v1/user/admin/users', { params })
}

/**
 * 更新用户状态（管理员）
 * @param {string} userId - 用户ID
 * @param {number} status - 状态值
 */
export function updateUserStatus(userId, status) {
  return request.put(`/v1/user/admin/users/${userId}/status`, null, { params: { status } })
}
