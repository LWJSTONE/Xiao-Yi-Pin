<template>
  <el-container class="app-container">
    <!-- 顶部导航栏 -->
    <el-header class="app-header" height="60px">
      <div class="header-left">
        <div class="logo" @click="router.push('/admin/dashboard')">
          <el-icon :size="28" color="#409EFF"><Briefcase /></el-icon>
          <span class="logo-text">校园兼职平台</span>
        </div>
      </div>
      <div class="header-center">
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          router
          class="nav-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/admin/audit">
            <el-icon><CircleCheck /></el-icon>
            <span>职位审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dict">
            <el-icon><Setting /></el-icon>
            <span>数据字典</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="36" class="user-avatar">
              {{ authStore.userInfo.username?.charAt(0) || 'A' }}
            </el-avatar>
            <span class="user-name">{{ authStore.userInfo.username || '管理员' }}</span>
            <el-tag type="danger" size="small" class="role-tag">管理员</el-tag>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>
                <el-icon><User /></el-icon>
                {{ authStore.userInfo.username || '管理员' }}
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-container">
      <!-- 左侧菜单 -->
      <el-aside width="220px" class="app-sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/audit">
            <el-icon><CircleCheck /></el-icon>
            <span>职位审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dict">
            <el-icon><Setting /></el-icon>
            <span>数据字典</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 底部 -->
    <AppFooter />
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessageBox } from 'element-plus'
import AppFooter from './components/AppFooter.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => {
  return route.path
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await authStore.logout()
      router.push('/login')
    } catch (e) {
      // 用户取消
    }
  }
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #409EFF;
  white-space: nowrap;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none !important;
  background: transparent;
}

.nav-menu .el-menu-item {
  font-size: 15px;
  height: 60px;
  line-height: 60px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #F56C6C, #E6A23C);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.role-tag {
  font-size: 11px;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}

.main-container {
  margin-top: 60px;
}

.app-sidebar {
  background-color: #304156;
  height: calc(100vh - 60px - 60px);
  position: fixed;
  left: 0;
  overflow-y: auto;
}

.sidebar-menu {
  border-right: none;
}

.app-main {
  margin-left: 220px;
  padding: 24px;
  min-height: calc(100vh - 60px - 60px);
}
</style>
