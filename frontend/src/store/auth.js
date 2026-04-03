import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/auth'
import { getToken, setToken, removeToken, setRefreshToken } from '@/utils/token'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(getToken() || '')
  const userInfo = ref({
    username: '',
    roleType: null,
    userId: null
  })

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isStudent = computed(() => userInfo.value.roleType === 'STUDENT')
  const isEmployer = computed(() => userInfo.value.roleType === 'EMPLOYER')
  const isAdmin = computed(() => userInfo.value.roleType === 'ADMIN')

  /**
   * 登录
   */
  async function login(loginForm) {
    try {
      const res = await loginApi(loginForm)
      const data = res.data
      token.value = data.accessToken
      setToken(data.accessToken)
      // 存储refreshToken用于自动刷新
      if (data.refreshToken) {
        setRefreshToken(data.refreshToken)
      }
      userInfo.value = {
        username: data.username || loginForm.username,
        roleType: data.roleType,
        userId: data.userId
      }
      return data
    } catch (error) {
      throw error
    }
  }

  /**
   * 加载用户信息
   */
  async function loadUser() {
    try {
      const res = await getCurrentUser()
      const data = res.data
      userInfo.value = {
        username: data.username,
        roleType: data.roleType,
        userId: data.id
      }
      return data
    } catch (error) {
      // 获取用户信息失败，清除 token
      token.value = ''
      removeToken()
      throw error
    }
  }

  /**
   * 退出登录
   */
  async function logout() {
    try {
      await logoutApi()
    } catch (e) {
      // 忽略退出登录的错误
    } finally {
      token.value = ''
      userInfo.value = {
        username: '',
        roleType: null,
        userId: null
      }
      removeToken()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isStudent,
    isEmployer,
    isAdmin,
    login,
    loadUser,
    logout
  }
})
