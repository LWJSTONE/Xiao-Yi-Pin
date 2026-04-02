<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <el-icon :size="48" color="#409EFF"><Briefcase /></el-icon>
          <h1 class="login-title">校园兼职平台</h1>
          <p class="login-subtitle">Campus Part-Time Job Platform</p>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item prop="captchaCode">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="请输入验证码"
                size="large"
                :prefix-icon="Key"
                class="captcha-input"
              />
              <div class="captcha-img" @click="refreshCaptcha">
                <span>{{ captchaText }}</span>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loginFormRef = ref(null)
const loading = ref(false)
const captchaText = ref('ABCD')

const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaKey: 'web-captcha-key'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  captchaCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码为 4 位字符', trigger: 'blur' }
  ]
}

// 刷新验证码
const refreshCaptcha = () => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let code = ''
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  captchaText.value = code
  loginForm.captchaKey = 'captcha-' + Date.now()
}

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    // 简单验证码校验
    if (loginForm.captchaCode.toUpperCase() !== captchaText.value) {
      ElMessage.error('验证码错误')
      refreshCaptcha()
      return
    }

    loading.value = true
    try {
      await authStore.login(loginForm)
      ElMessage.success('登录成功')

      // 根据角色跳转
      const role = authStore.userInfo.roleType
      const redirect = route.query.redirect
      if (redirect) {
        router.push(redirect)
      } else if (role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else if (role === 'STUDENT') {
        router.push('/student/dashboard')
      } else if (role === 'EMPLOYER') {
        router.push('/employer/dashboard')
      } else {
        router.push('/login')
      }
    } catch (error) {
      refreshCaptcha()
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}

.login-bg::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 60%);
  animation: bgRotate 30s linear infinite;
}

@keyframes bgRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 440px;
  padding: 20px;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 12px 0 4px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 8px;
}

.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  width: 120px;
  height: 40px;
  background: linear-gradient(135deg, #e0e5ec, #f0f2f5);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  font-size: 22px;
  font-weight: 700;
  color: #409EFF;
  letter-spacing: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.captcha-img:hover {
  border-color: #409EFF;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  height: 44px;
  border-radius: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #909399;
}

.login-footer .el-link {
  font-size: 14px;
}
</style>
