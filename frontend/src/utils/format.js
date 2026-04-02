/**
 * 格式化日期时间
 * @param {string} dateStr 日期字符串
 * @returns {string} 格式化后的日期 YYYY-MM-DD HH:mm
 */
export function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

/**
 * 格式化日期
 * @param {string} dateStr 日期字符串
 * @returns {string} 格式化后的日期 YYYY-MM-DD
 */
export function formatDateOnly(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 格式化薪资
 * @param {number} amount 薪资金额
 * @param {string} type 薪资类型
 * @returns {string} 格式化后的薪资
 */
export function formatSalary(amount, type) {
  if (!amount && amount !== 0) return '-'
  const amountStr = Number(amount).toFixed(0)
  switch (type) {
    case '0':
      return `${amountStr} 元/小时`
    case '1':
      return `${amountStr} 元/天`
    case '2':
      return `${amountStr} 元/周`
    case '3':
      return `${amountStr} 元/月`
    case '4':
      return `${amountStr} 元/次`
    default:
      return `${amountStr} 元`
  }
}

/**
 * 薪资类型映射
 */
export const salaryTypeMap = {
  '0': '时薪',
  '1': '日薪',
  '2': '周薪',
  '3': '月薪',
  '4': '次薪'
}

/**
 * 职位状态映射
 */
export const jobStatusMap = {
  '0': '草稿',
  '1': '待审核',
  '2': '已通过',
  '3': '已拒绝',
  '4': '招聘中',
  '5': '已下架'
}

/**
 * 职位状态颜色映射
 */
export const jobStatusColorMap = {
  '0': 'info',
  '1': 'warning',
  '2': 'success',
  '3': 'danger',
  '4': 'primary',
  '5': 'info'
}

/**
 * 申请状态映射
 */
export const applyStatusMap = {
  '0': '待审核',
  '1': '已通过',
  '2': '已拒绝',
  '3': '已录用',
  '4': '已取消'
}

/**
 * 申请状态颜色映射
 */
export const applyStatusColorMap = {
  '0': 'warning',
  '1': 'success',
  '2': 'danger',
  '3': 'primary',
  '4': 'info'
}

/**
 * 角色类型映射
 */
export const roleTypeMap = {
  '0': '管理员',
  '1': '学生',
  '2': '雇主'
}

/**
 * 角色标签颜色映射
 */
export const roleColorMap = {
  '0': 'danger',
  '1': 'primary',
  '2': 'success'
}
