import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/token'
import { useAuthStore } from '@/store/auth'

// 学生布局
import StudentLayout from '@/layout/StudentLayout.vue'
// 雇主布局
import EmployerLayout from '@/layout/EmployerLayout.vue'
// 管理员布局
import AdminLayout from '@/layout/AdminLayout.vue'

// 学生页面
import StudentDashboard from '@/views/student/Dashboard.vue'
import JobList from '@/views/student/JobList.vue'
import JobDetail from '@/views/student/JobDetail.vue'
import MyApplications from '@/views/student/MyApplications.vue'
import MyReviews from '@/views/student/MyReviews.vue'

// 雇主页面
import EmployerDashboard from '@/views/employer/Dashboard.vue'
import PublishJob from '@/views/employer/PublishJob.vue'
import MyJobs from '@/views/employer/MyJobs.vue'
import JobApplications from '@/views/employer/JobApplications.vue'
import MyOrders from '@/views/employer/MyOrders.vue'

// 管理员页面
import AdminDashboard from '@/views/admin/Dashboard.vue'
import AuditJobs from '@/views/admin/AuditJobs.vue'
import UserManage from '@/views/admin/UserManage.vue'
import DictManage from '@/views/admin/DictManage.vue'

// 公共页面
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'

// 个人资料
import Profile from '@/views/Profile.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册', public: true }
  },
  {
    path: '/',
    redirect: '/login'
  },
  // 学生路由
  {
    path: '/student',
    component: StudentLayout,
    redirect: '/student/dashboard',
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: StudentDashboard,
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'jobs',
        name: 'StudentJobs',
        component: JobList,
        meta: { title: '兼职列表', icon: 'Search' }
      },
      {
        path: 'jobs/:id',
        name: 'JobDetail',
        component: JobDetail,
        meta: { title: '职位详情', hidden: true }
      },
      {
        path: 'applications',
        name: 'MyApplications',
        component: MyApplications,
        meta: { title: '我的报名', icon: 'Document' }
      },
      {
        path: 'reviews',
        name: 'MyReviews',
        component: MyReviews,
        meta: { title: '我的评价', icon: 'Star' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: Profile,
        meta: { title: '个人资料', icon: 'User' }
      }
    ]
  },
  // 雇主路由
  {
    path: '/employer',
    component: EmployerLayout,
    redirect: '/employer/dashboard',
    meta: { requiresAuth: true, role: 'EMPLOYER' },
    children: [
      {
        path: 'dashboard',
        name: 'EmployerDashboard',
        component: EmployerDashboard,
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'publish',
        name: 'PublishJob',
        component: PublishJob,
        meta: { title: '发布兼职', icon: 'EditPen' }
      },
      {
        path: 'jobs',
        name: 'EmployerJobs',
        component: MyJobs,
        meta: { title: '我的职位', icon: 'Briefcase' }
      },
      {
        path: 'applications',
        name: 'JobApplications',
        component: JobApplications,
        meta: { title: '报名管理', icon: 'User' }
      },
      {
        path: 'orders',
        name: 'EmployerOrders',
        component: MyOrders,
        meta: { title: '我的订单', icon: 'Tickets' }
      },
      {
        path: 'profile',
        name: 'EmployerProfile',
        component: Profile,
        meta: { title: '个人资料', icon: 'User' }
      }
    ]
  },
  // 管理员路由
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'audit',
        name: 'AuditJobs',
        component: AuditJobs,
        meta: { title: '职位审核', icon: 'CircleCheck' }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'dict',
        name: 'DictManage',
        component: DictManage,
        meta: { title: '数据字典', icon: 'Setting' }
      }
    ]
  },
  // 403 禁止访问页面
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Forbidden.vue'),
    meta: { title: '无权限', public: true }
  },
  // 404 兜底
  {
    path: '/:pathMatch(.*)*',
    redirect: '/403'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - 校园兼职平台`
    : '校园兼职平台'

  const token = getToken()

  // 不需要认证的页面直接放行
  if (to.meta.public || to.path === '/login' || to.path === '/register') {
    // 已登录用户访问登录页，跳转到对应首页
    if (token && (to.path === '/login' || to.path === '/register')) {
      try {
        const authStore = useAuthStore()
        if (!authStore.userInfo.userId) {
          await authStore.loadUser()
        }
        const role = authStore.userInfo.roleType
        if (role === 'ADMIN') return next('/admin/dashboard')
        if (role === 'STUDENT') return next('/student/dashboard')
        if (role === 'EMPLOYER') return next('/employer/dashboard')
      } catch (e) {
        // token 无效，放行到登录页
      }
    }
    return next()
  }

  // 没有 token，跳转到登录页
  if (!token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // 有 token，检查用户信息
  try {
    const authStore = useAuthStore()
    if (!authStore.userInfo.userId) {
      await authStore.loadUser()
    }
    const role = authStore.userInfo.roleType

    // 检查角色权限
    if (to.meta.role && to.meta.role !== role) {
      // 角色不匹配，跳转到 403
      return next('/403')
    }

    // 根据角色检查路由访问权限
    if (to.path.startsWith('/student') && role !== 'STUDENT') {
      return next('/403')
    }
    if (to.path.startsWith('/employer') && role !== 'EMPLOYER') {
      return next('/403')
    }
    if (to.path.startsWith('/admin') && role !== 'ADMIN') {
      return next('/403')
    }

    next()
  } catch (error) {
    // 获取用户信息失败，清除 token 并跳转登录
    const authStore = useAuthStore()
    authStore.logout()
    next({ path: '/login', query: { redirect: to.fullPath } })
  }
})

export default router
