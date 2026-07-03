<template>
  <div class="main-layout">
    <AppHeader @toggle-sidebar="toggleMobileSidebar" />
    <div class="layout-body">
      <main class="content">
        <ErrorBoundary>
          <router-view />
        </ErrorBoundary>
      </main>
    </div>

    <!-- Bottom navigation bar (mobile only) -->
    <nav v-if="isMobile" class="bottom-nav">
      <router-link to="/" class="bottom-nav-item" :class="{ active: isTabActive('home') }">
        <span class="bottom-nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        </span>
        <span class="bottom-nav-label">首页</span>
      </router-link>
      <router-link to="/search" class="bottom-nav-item" :class="{ active: isTabActive('search') }">
        <span class="bottom-nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
        </span>
        <span class="bottom-nav-label">发现</span>
      </router-link>
      <router-link to="/messages" class="bottom-nav-item" :class="{ active: isTabActive('messages') }">
        <span class="bottom-nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
          </svg>
          <span v-if="userStore.unreadCount > 0" class="bottom-nav-badge">{{ userStore.unreadCount > 99 ? '99+' : userStore.unreadCount }}</span>
        </span>
        <span class="bottom-nav-label">消息</span>
      </router-link>
      <router-link to="/creator" class="bottom-nav-item" :class="{ active: isTabActive('creator') }">
        <span class="bottom-nav-icon">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
        </span>
        <span class="bottom-nav-label">我的</span>
      </router-link>
    </nav>

    <GlobalMiniPlayer />
    <LoginDialog />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LoginDialog from '@/components/auth/LoginDialog.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import ErrorBoundary from '@/components/layout/ErrorBoundary.vue'
import SideCategoryNav from '@/components/layout/SideCategoryNav.vue'
import GlobalMiniPlayer from '@/components/player/GlobalMiniPlayer.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isMobile = ref(false)
const mobileSidebarVisible = ref(false)

function toggleMobileSidebar() {
  mobileSidebarVisible.value = !mobileSidebarVisible.value
}

function isTabActive(name) {
  if (name === 'home') return route.name === 'home' && !route.path.startsWith('/messages') && !route.path.startsWith('/creator') && !route.path.startsWith('/search')
  if (name === 'messages') return route.path.startsWith('/messages')
  if (name === 'creator') return route.path.startsWith('/creator')
  if (name === 'search') return route.path.startsWith('/search')
  return route.name === name
}

function checkMobile() {
  isMobile.value = window.innerWidth < 768
  if (!isMobile.value) {
    mobileSidebarVisible.value = false
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: var(--bil-bg);
}

.layout-body {
  max-width: 1440px;
  margin: 0 auto;
  padding: 84px 24px 96px;
}

.content {
  min-width: 0;
}

/* Mobile overlay */
.mobile-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 50;
}

/* Bottom navigation bar */
.bottom-nav {
  display: none;
}

@media (max-width: 767px) {
  .layout-body {
    display: block;
    padding: 62px 14px 72px;
  }

  .bottom-nav {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 56px;
    background: var(--bil-surface);
    border-top: 1px solid var(--bil-border);
    z-index: 90;
    align-items: center;
    justify-content: space-around;
    padding-bottom: env(safe-area-inset-bottom, 0);
  }

  .bottom-nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    text-decoration: none;
    color: var(--bil-muted);
    font-size: 11px;
    cursor: pointer;
    padding: 4px 12px;
    transition: color 0.2s;
  }

  .bottom-nav-item.active,
  .bottom-nav-item:hover {
    color: var(--bil-primary, #00a1d6);
  }

  .bottom-nav-icon {
    position: relative;
    line-height: 0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }

  .bottom-nav-badge {
    position: absolute;
    top: -6px;
    right: -8px;
    min-width: 16px;
    height: 16px;
    padding: 0 4px;
    border-radius: 8px;
    background: var(--bil-pink);
    color: #fff;
    font-size: 10px;
    font-weight: 600;
    line-height: 16px;
    text-align: center;
    white-space: nowrap;
  }

  .bottom-nav-label {
    font-size: 11px;
  }
}

</style>
