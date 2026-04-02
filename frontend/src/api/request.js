import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/token'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 如果返回的状态码为 200，说明接口请求成功
    if (res.code === 200) {
      return res
    }
    // 401: Token 过期或未授权
    if (res.code === 401) {
      removeToken()
      ElMessage.error(res.message || '登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error(res.message || '未授权'))
    }
    // 其他错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('响应错误：', error)
    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '登录已过期，请重新登录'
          removeToken()
          router.push('/login')
          break
        case 403:
          message = '没有权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败 (${error.response.status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
