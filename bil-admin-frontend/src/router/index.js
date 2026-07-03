import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
      { path: 'category', name: 'category', component: () => import('@/views/CategoryView.vue') },
      { path: 'video', name: 'video', component: () => import('@/views/VideoView.vue') },
      { path: 'interact', name: 'interact', component: () => import('@/views/InteractView.vue') },
      { path: 'user', name: 'user', component: () => import('@/views/UserView.vue') },
      { path: 'banner', name: 'banner', component: () => import('@/views/BannerView.vue') },
      { path: 'setting', name: 'setting', component: () => import('@/views/SettingView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !getToken()) {
    return { name: 'login' }
  }
  if (to.name === 'login' && getToken()) {
    return { name: 'dashboard' }
  }
  return true
})

export default router
