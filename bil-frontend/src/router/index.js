import { createRouter, createWebHistory } from 'vue-router'
import { eventBus } from '@/utils/eventBus'
import { getToken } from '@/utils/token'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/HomeView.vue') },
      { path: 'search', name: 'search', component: () => import('@/views/SearchView.vue') },
      { path: 'video/:videoId', name: 'video-detail', component: () => import('@/views/VideoDetailView.vue') },
      { path: 'user/:userId?', name: 'user-home', component: () => import('@/views/UserHomeView.vue') },
      { path: 'creator', name: 'creator', meta: { requiresAuth: true }, component: () => import('@/views/CreatorCenterView.vue') },
      { path: 'messages', name: 'messages', meta: { requiresAuth: true }, component: () => import('@/views/MessageCenterView.vue') },
      { path: 'series/:seriesId', name: 'series-detail', component: () => import('@/views/SeriesDetailView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !getToken()) {
    eventBus.emit('auth:required')
    return { name: 'home' }
  }
  return true
})

export default router
