<template>
  <div class="profile-page">
    <el-card shadow="never" class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">👤 个人资料</span>
        </div>
      </template>

      <div v-loading="loading" class="profile-content">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
          <el-form-item label="用户名">
            <el-input :value="profile.username" disabled />
          </el-form-item>
          <el-form-item label="角色">
            <el-tag :type="roleColorMap[profile.roleType] || 'info'">{{ roleTypeMap[profile.roleType] || '未知' }}</el-tag>
          </el-form-item>
          <el-divider />
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" maxlength="50" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio :label="0">未知</el-radio>
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="学校" prop="university">
            <el-input v-model="form.university" placeholder="请输入学校" maxlength="100" />
          </el-form-item>
          <el-form-item label="专业" prop="major">
            <el-input v-model="form.major" placeholder="请输入专业" maxlength="100" />
          </el-form-item>
          <el-form-item label="年级" prop="grade">
            <el-input v-model="form.grade" placeholder="请输入年级" maxlength="20" />
          </el-form-item>
          <el-form-item label="头像URL" prop="avatarUrl">
            <el-input v-model="form.avatarUrl" placeholder="请输入头像URL" maxlength="500" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saveLoading" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>

        <el-divider />

        <div class="info-section">
          <h3>认证信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="认证状态">
              <el-tag :type="verifyStatusColorMap[profile.verifiedStatus] || 'info'">
                {{ verifyStatusMap[profile.verifiedStatus] || '未认证' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="信用分">{{ profile.creditScore || 0 }}</el-descriptions-item>
            <el-descriptions-item label="余额">¥ {{ profile.balance || '0.00' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(profile.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyProfile, updateMyProfile } from '@/api/user'
import { formatDate, roleTypeMap, roleColorMap } from '@/utils/format'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const saveLoading = ref(false)

const profile = ref({
  username: '',
  roleType: '',
  verifiedStatus: 0,
  creditScore: 0,
  balance: '0.00',
  createTime: ''
})

const form = reactive({
  realName: '',
  gender: 0,
  university: '',
  major: '',
  grade: '',
  avatarUrl: ''
})

const rules = {}

const verifyStatusMap = { 0: '未认证', 1: '审核中', 2: '已认证', 3: '认证失败' }
const verifyStatusColorMap = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }

onMounted(() => {
  loadProfile()
})

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await getMyProfile()
    if (res.data) {
      profile.value = res.data
      form.realName = res.data.realName || ''
      form.gender = res.data.gender || 0
      form.university = res.data.university || ''
      form.major = res.data.major || ''
      form.grade = res.data.grade || ''
      form.avatarUrl = res.data.avatarUrl || ''
    }
  } catch (error) {
    console.error('加载个人资料失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  saveLoading.value = true
  try {
    const res = await updateMyProfile({ ...form })
    ElMessage.success('资料更新成功')
    if (res.data) {
      profile.value = res.data
    }
  } catch (error) {
    // 错误已处理
  } finally {
    saveLoading.value = false
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.profile-form {
  padding: 20px 0;
}

.info-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}
</style>
