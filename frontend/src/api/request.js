import axios from 'axios'
import JSONBig from 'json-bigint'
import { ElMessage } from 'element-plus'
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken } from '@/utils/token'
import router from '@/router'

// 使用 json-bigint 处理后端返回的大数字（如雪花ID）
// 防止 JavaScript Number 精度丢失（MAX_SAFE_INTEGER = 9007199254740991）
const jsonBig = JSONBig({ storeAsString: true })

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  },
  // 自定义响应转换：用 json-bigint 解析 JSON，大数字自动转为字符串
  transformResponse: [function (data) {
    if (typeof data === 'string') {
      try {
        return jsonBig.parse(data)
      } catch (e) {
        // 解析失败时降级为普通 JSON
        return JSON.parse(data)
      }
    }
    return data
  }]
})

// 是否正在刷新token
let isRefreshing = false
// 刷新token期间的请求队列
let refreshSubscribers = []

/**
 * 将请求加入队列，等待token刷新后重试
 */
function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb)
}

/**
 * token刷新成功后，重试队列中的所有请求
 */
function onTokenRefreshed(newToken) {
  refreshSubscribers.forEach(cb => cb(newToken))
  refreshSubscribers = []
}

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
      handleUnauthorized()
      return Promise.reject(new Error(res.message || '未授权'))
    }
    // 其他错误
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    console.error('响应错误：', error)
    const originalRequest = error.config
    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '登录已过期，请重新登录'
          // 尝试使用refreshToken刷新
          const refreshTokenVal = getRefreshToken()
          if (refreshTokenVal && !originalRequest._retry) {
            if (isRefreshing) {
              // 正在刷新token，将请求加入队列
              return new Promise((resolve) => {
                subscribeTokenRefresh((newToken) => {
                  originalRequest.headers['Authorization'] = `Bearer ${newToken}`
                  resolve(service(originalRequest))
                })
              })
            }
            originalRequest._retry = true
            isRefreshing = true
            return axios.post(`${import.meta.env.VITE_API_BASE_URL}/v1/auth/refresh`, {
              refreshToken: refreshTokenVal
            }).then((res) => {
              const data = res.data
              if (data.code === 200 && data.data) {
                const newAccessToken = data.data.accessToken
                const newRefreshTokenVal = data.data.refreshToken
                setToken(newAccessToken)
                setRefreshToken(newRefreshTokenVal)
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`
                onTokenRefreshed(newAccessToken)
                return service(originalRequest)
              } else {
                handleUnauthorized()
                return Promise.reject(error)
              }
            }).catch((refreshError) => {
              handleUnauthorized()
              return Promise.reject(refreshError)
            }).finally(() => {
              isRefreshing = false
            })
          }
          handleUnauthorized()
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
    if (error.response?.status !== 401) {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

/**
 * 处理未授权：清除token并跳转登录页
 */
function handleUnauthorized() {
  removeToken()
  router.push('/login')
  ElMessage.error('登录已过期，请重新登录')
}

export default service
