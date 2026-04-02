/**
 * 手机号校验
 * @param {string} phone
 * @returns {boolean}
 */
export function isPhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 邮箱校验
 * @param {string} email
 * @returns {boolean}
 */
export function isEmail(email) {
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)
}

/**
 * 密码强度校验（至少6位，包含字母和数字）
 * @param {string} password
 * @returns {boolean}
 */
export function isPassword(password) {
  return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/.test(password)
}
