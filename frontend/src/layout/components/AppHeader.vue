<template>
  <div class="app-header">
    <div class="header-left">
      <div class="logo" @click="$router.push('/')">
        <el-icon :size="28" color="#409EFF"><Briefcase /></el-icon>
        <span class="logo-text">校园兼职平台</span>
      </div>
    </div>
    <div class="header-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="36" class="user-avatar">
            {{ username?.charAt(0) || 'U' }}
          </el-avatar>
          <span class="user-name">{{ username || '用户' }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-icon><User /></el-icon>
              {{ username || '用户' }}
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  navItems: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()
const authStore = useAuthStore()

const username = computed(() => authStore.userInfo.username)

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
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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
  background: linear-gradient(135deg, #409EFF, #67C23A);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.dropdown-icon {
  color: #909399;
  font-size: 12px;
}
</style>
