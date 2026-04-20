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
              <el-button v-if="canApplyVerify" type="primary" link size="small" style="margin-left: 12px;" @click="verifyDialogVisible = true">
                申请认证
              </el-button>
            </el-descriptions-item>
            <el-descriptions-item label="信用分">{{ profile.creditScore || 0 }}</el-descriptions-item>
            <el-descriptions-item label="余额">¥ {{ profile.balance || '0.00' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(profile.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <!-- 实名认证对话框 -->
    <el-dialog v-model="verifyDialogVisible" title="申请实名认证" width="500px">
      <el-form :model="verifyForm" label-width="100px">
        <el-form-item label="真实姓名" required>
          <el-input v-model="verifyForm.realName" placeholder="请输入真实姓名" maxlength="50" />
        </el-form-item>
        <el-form-item label="身份证号" required>
          <el-input v-model="verifyForm.idCard" placeholder="请输入18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="身份证图片" required>
          <el-input v-model="verifyForm.idCardImage" placeholder="请输入身份证图片URL" />
        </el-form-item>
        <el-form-item label="认证类型" required>
          <el-select v-model="verifyForm.verifyType" style="width: 100%;">
            <el-option label="身份证认证" value="IDENTITY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="verifyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="verifyLoading" @click="handleApplyVerify">提交认证</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getMyProfile, updateMyProfile, applyVerify } from '@/api/user'
import { formatDate, roleTypeMap, roleColorMap, verifyStatusMap, verifyStatusColorMap } from '@/utils/format'
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

const rules = {
  realName: [
    { max: 50, message: '姓名不能超过50个字符', trigger: 'blur' }
  ],
  university: [
    { max: 100, message: '学校名称不能超过100个字符', trigger: 'blur' }
  ],
  major: [
    { max: 100, message: '专业不能超过100个字符', trigger: 'blur' }
  ],
  grade: [
    { max: 20, message: '年级不能超过20个字符', trigger: 'blur' }
  ],
  avatarUrl: [
    { max: 500, message: '头像URL不能超过500个字符', trigger: 'blur' }
  ]
}



const verifyDialogVisible = ref(false)
const verifyLoading = ref(false)
const verifyForm = reactive({
  realName: '',
  idCard: '',
  idCardImage: '',
  verifyType: 'IDENTITY'
})

const canApplyVerify = computed(() => {
  const status = profile.value.verifiedStatus
  return status === 0 || status === 3
})

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
    ElMessage.error('加载个人资料失败')
  } finally {
    loading.value = false
  }
}

const handleApplyVerify = async () => {
  if (!verifyForm.realName || !verifyForm.idCard || !verifyForm.idCardImage) {
    ElMessage.warning('请填写完整的认证信息')
    return
  }
  if (!/^\d{17}[\dXx]$/.test(verifyForm.idCard)) {
    ElMessage.warning('请输入正确的18位身份证号')
    return
  }
  verifyLoading.value = true
  try {
    await applyVerify({ ...verifyForm })
    ElMessage.success('认证申请已提交，请等待审核')
    verifyDialogVisible.value = false
    loadProfile()
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    verifyLoading.value = false
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
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
