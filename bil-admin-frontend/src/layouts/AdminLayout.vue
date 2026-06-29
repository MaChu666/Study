<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <router-link class="logo" to="/">BilBil Admin</router-link>
      <nav class="nav">
        <router-link v-for="item in menuItems" :key="item.path" :to="item.path" class="nav-item">
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </aside>
    <div class="main-area">
      <div class="topbar">
        <span class="breadcrumb">{{ currentTitle }}</span>
      </div>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearToken } from '@/stores/auth'
import { logoutApi } from '@/api/modules/account'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { path: '/', label: '仪表盘' },
  { path: '/category', label: '分类管理' },
  { path: '/video', label: '稿件管理' },
  { path: '/interact', label: '互动管理' },
  { path: '/user', label: '用户管理' },
  { path: '/setting', label: '系统设置' }
]

const currentTitle = computed(() => {
  const item = menuItems.find((m) => m.path === route.path) || menuItems[0]
  return item.label
})

async function handleLogout() {
  try {
    await logoutApi()
  } catch {
    // ignore
  }
  clearToken()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
}
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--admin-sidebar-bg);
  display: flex;
  flex-direction: column;
  padding: 20px 0;
}
.logo {
  display: block;
  padding: 0 20px 24px;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}
.nav {
  flex: 1;
  display: grid;
  gap: 4px;
  padding: 0 12px;
}
.nav-item {
  padding: 12px 16px;
  border-radius: 8px;
  color: var(--admin-sidebar-text);
  transition: background 0.2s, color 0.2s;
}
.nav-item:hover {
  background: rgba(255,255,255,0.06);
  color: #fff;
}
.nav-item.router-link-active {
  background: var(--admin-primary);
  color: #fff;
}
.sidebar-footer {
  padding: 12px;
}
.logout-btn {
  width: 100%;
  padding: 10px;
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 8px;
  background: transparent;
  color: var(--admin-sidebar-text);
  cursor: pointer;
}
.logout-btn:hover {
  color: var(--admin-danger);
  border-color: var(--admin-danger);
}
.main-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: var(--admin-surface);
  border-bottom: 1px solid var(--admin-border);
}
.breadcrumb {
  font-size: 15px;
  font-weight: 600;
}
.content {
  flex: 1;
  padding: 24px;
  overflow: auto;
}
</style>
