import request from './request'

/**
 * 用户登录
 * @param {Object} data - { username, password, captchaCode, captchaKey }
 */
export function login(data) {
  return request.post('/v1/auth/login', data)
}

/**
 * 刷新 Token
 * @param {Object} data - { refreshToken }
 */
export function refreshToken(data) {
  return request.post('/v1/auth/refresh', data)
}

/**
 * 退出登录
 */
export function logout() {
  return request.post('/v1/auth/logout')
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request.get('/v1/auth/current-user')
}

/**
 * 用户注册（调用登录接口的创建用户流程）
 * @param {Object} data - { username, password, phone, roleType }
 */
export function register(data) {
  return request.post('/v1/auth/register', data)
}
