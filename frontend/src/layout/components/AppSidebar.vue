<template>
  <el-aside :width="width" class="app-sidebar">
    <el-menu
      :default-active="activeMenu"
      router
      class="sidebar-menu"
      :collapse="collapsed"
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.path"
        :index="item.path"
      >
        <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

defineProps({
  menuItems: {
    type: Array,
    default: () => []
  },
  width: {
    type: String,
    default: '220px'
  },
  collapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.app-sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}
</style>
