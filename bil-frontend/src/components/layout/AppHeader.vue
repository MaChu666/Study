<template>
  <header class="app-header">
    <div class="left-group">
      <button class="hamburger-btn" @click="emit('toggle-sidebar')" aria-label="菜单">
        <span></span><span></span><span></span>
      </button>
      <router-link class="brand" to="/" @click="menuOpen = false">
        <svg class="brand-icon" viewBox="0 0 24 24" width="26" height="26" fill="none">
          <rect x="2" y="5" width="20" height="14" rx="3" fill="currentColor" />
          <path d="M8 9.5v5l4.5-2.5L8 9.5z" fill="#fff" />
          <path d="M7 3.5 4.5 6M17 3.5 19.5 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
        </svg>
        <span class="brand-name">VidVault</span>
      </router-link>
      <nav class="nav-links">
        <router-link v-for="link in navLinks" :key="link.path" :to="link.path" class="nav-item" active-class="active" :exact="link.exact">
          {{ link.label }}
        </router-link>
      </nav>
    </div>

    <div class="search-wrapper">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索视频、UP主"
        @keyup.enter="goSearch"
        @focus="showSuggest = true"
        @blur="hideSuggest"
      />
      <button class="search-btn" @click="goSearch" aria-label="搜索">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2.5">
          <circle cx="11" cy="11" r="8" />
          <path d="m21 21-4.35-4.35" />
        </svg>
      </button>
      <div class="suggest-drop" v-if="showSuggest && suggestList.length && keyword">
        <div v-for="t in suggestList" :key="t" class="suggest-item" @mousedown.prevent="pickSuggest(t)">{{ t }}</div>
      </div>
    </div>

    <div class="actions">
      <button class="icon-btn" @click="themeStore.toggleTheme" :title="themeStore.mode === 'light' ? '暗色模式' : '亮色模式'">
        <svg v-if="themeStore.mode === 'light'" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
        </svg>
        <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5" />
          <line x1="12" y1="1" x2="12" y2="3" />
          <line x1="12" y1="21" x2="12" y2="23" />
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
          <line x1="1" y1="12" x2="3" y2="12" />
          <line x1="21" y1="12" x2="23" y2="12" />
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
        </svg>
      </button>
      <button class="icon-btn msg-btn" @click="goMessages" title="消息">
        <svg viewBox="0 0 24 24" width="19" height="19" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        <span v-if="userStore.unreadCount > 0" class="unread-badge">{{ userStore.unreadCount > 99 ? '99+' : userStore.unreadCount }}</span>
      </button>
      <router-link v-if="userStore.isLogin" to="/creator" class="upload-btn">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z" />
        </svg>
        投稿
      </router-link>

      <div v-if="userStore.isLogin" class="user-menu" @click="menuOpen = !menuOpen">
        <img v-if="userStore.profile?.avatar" :src="userStore.profile.avatar" class="avatar avatar-img" />
        <span v-else class="avatar">{{ userStore.profile?.useName?.slice(0, 1) || 'U' }}</span>
        <div class="menu-drop" v-if="menuOpen" @click.stop>
          <div class="menu-item" @click="goProfile">个人中心</div>
          <div class="menu-item" @click="goCreator">创作中心</div>
          <div class="menu-divider" />
          <div class="menu-item" @click="userStore.logout">退出登录</div>
        </div>
      </div>
      <button v-else class="login-btn" @click="userStore.openLoginDialog">登录</button>
    </div>
  </header>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'
import { getSearchKeywordTopApi } from '@/api/modules/video'
import { readAllMessageApi } from '@/api/modules/account'
import { eventBus } from '@/utils/eventBus'

const emit = defineEmits(['toggle-sidebar'])

const router = useRouter()
const keyword = ref('')
const userStore = useUserStore()
const themeStore = useThemeStore()
const showSuggest = ref(false)
const suggestList = ref([])
const menuOpen = ref(false)

const navLinks = [
  { label: '首页', path: '/', exact: true },
  { label: '动画', path: '/category/1' },
  { label: '音乐', path: '/category/2' },
  { label: '游戏', path: '/category/3' },
  { label: '知识', path: '/category/4' },
  { label: '科技', path: '/category/5' },
  { label: '生活', path: '/category/7' },
  { label: '发现', path: '/search' }
]

let hideTimer = null
let unreadTimer = null
const UNREAD_POLL_INTERVAL = 60 * 1000 // 60s

async function loadSuggests() {
  try {
    const data = await getSearchKeywordTopApi()
    suggestList.value = Array.isArray(data) ? data : []
  } catch { suggestList.value = [] }
}

function goSearch() {
  if (!keyword.value.trim()) return
  menuOpen.value = false
  router.push({ name: 'search', query: { keyword: keyword.value } })
  showSuggest.value = false
}

function pickSuggest(t) {
  keyword.value = t
  goSearch()
}

function hideSuggest() {
  hideTimer = setTimeout(() => { showSuggest.value = false }, 200)
}

function goProfile() {
  menuOpen.value = false
  router.push({ name: 'user-home', params: { userId: userStore.profile?.userId } })
}

async function goMessages() {
  if (!userStore.isLogin) {
    userStore.openLoginDialog()
    return
  }
  try { await readAllMessageApi() } catch (_) {}
  userStore.unreadCount = 0
  userStore.notificationDot = false
  router.push({ name: 'messages' })
}

function goCreator() {
  menuOpen.value = false
  router.push({ name: 'creator' })
}

onMounted(() => {
  loadSuggests()
  userStore.fetchUnreadCount()
  unreadTimer = setInterval(() => userStore.fetchUnreadCount(), UNREAD_POLL_INTERVAL)
  eventBus.on('video:liked', () => userStore.markNotificationDot(true))
  eventBus.on('video:collected', () => userStore.markNotificationDot(true))
})
onUnmounted(() => {
  clearTimeout(hideTimer)
  clearInterval(unreadTimer)
  eventBus.off('video:liked')
  eventBus.off('video:collected')
})
</script>

<style scoped>
.app-header {
  position: fixed;
  inset: 0 0 auto;
  z-index: 100;
  height: var(--bil-header-h);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  background: color-mix(in srgb, var(--bil-surface) 94%, transparent);
  border-bottom: 1px solid var(--bil-border);
  backdrop-filter: blur(16px);
}
.left-group { display: flex; align-items: center; gap: 14px; min-width: 0; }
.brand { display: flex; align-items: center; gap: 6px; flex-shrink: 0; color: var(--bil-pink); }
.brand-icon { display: block; }
.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: var(--bil-text);
}
.nav-links { display: flex; align-items: center; gap: 2px; }
.nav-item {
  position: relative;
  padding: 6px 10px;
  font-size: 14px;
  color: var(--bil-text);
  text-decoration: none;
  white-space: nowrap;
  transition: color 0.2s;
}
.nav-item:hover { color: var(--bil-pink); }
.nav-item.active { color: var(--bil-pink); }
.nav-item.active::after {
  content: "";
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: 0;
  height: 2px;
  border-radius: 1px;
  background: var(--bil-pink);
}

.search-wrapper {
  flex: 1;
  max-width: 440px;
  position: relative;
  display: flex;
  align-items: center;
  height: 34px;
  background: var(--bil-hover);
  border: 1px solid transparent;
  border-radius: 6px;
  transition: border-color 0.2s, background 0.2s;
}
.search-wrapper:focus-within {
  background: var(--bil-surface);
  border-color: var(--bil-pink);
}
.search-input {
  flex: 1;
  min-width: 0;
  height: 100%;
  padding: 0 8px 0 14px;
  border: none;
  background: transparent;
  color: var(--bil-text);
  font-size: 13px;
  outline: none;
}
.search-btn {
  width: 28px;
  height: 28px;
  margin-right: 3px;
  border: none;
  background: var(--bil-pink);
  color: #fff;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.2s;
}
.search-btn:hover { background: var(--bil-pink-hover); }
.suggest-drop {
  position: absolute;
  top: 40px;
  left: 0;
  right: 0;
  background: var(--bil-surface);
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  padding: 6px 0;
  box-shadow: var(--bil-shadow);
  z-index: 30;
}
.suggest-item {
  padding: 8px 16px;
  font-size: 13px;
  color: var(--bil-text);
  cursor: pointer;
}
.suggest-item:hover { background: var(--bil-hover); color: var(--bil-pink); }

.actions { margin-left: auto; display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.icon-btn {
  position: relative;
  border: none;
  background: none;
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bil-text);
  border-radius: 6px;
  transition: background 0.2s, color 0.2s;
}
.icon-btn:hover { background: var(--bil-hover); color: var(--bil-pink); }
.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
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

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 34px;
  padding: 0 16px;
  border-radius: 6px;
  background: var(--bil-pink);
  color: #fff;
  font-size: 14px;
  text-decoration: none;
  transition: background 0.2s;
}
.upload-btn:hover { background: var(--bil-pink-hover); }

.user-menu { position: relative; cursor: pointer; padding: 4px; }
.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--bil-pink);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}
.avatar.avatar-img { object-fit: cover; background: none; }
.menu-drop {
  position: absolute;
  top: 42px;
  right: 0;
  min-width: 140px;
  background: var(--bil-surface);
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  padding: 6px 0;
  box-shadow: var(--bil-shadow);
  z-index: 30;
}
.menu-item {
  padding: 8px 20px;
  font-size: 13px;
  color: var(--bil-text);
  cursor: pointer;
  white-space: nowrap;
}
.menu-item:hover { background: var(--bil-hover); color: var(--bil-pink); }
.menu-divider { height: 1px; background: var(--bil-border); margin: 4px 0; }
.login-btn {
  height: 34px;
  padding: 0 20px;
  border: 1px solid var(--bil-border);
  border-radius: 6px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}
.login-btn:hover { border-color: var(--bil-pink); color: var(--bil-pink); }

.hamburger-btn {
  display: none;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  width: 28px;
  height: 28px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;
}
.hamburger-btn span {
  display: block;
  width: 100%;
  height: 2px;
  background: var(--bil-text);
  border-radius: 2px;
}

@media (max-width: 1080px) {
  .nav-item { padding: 6px 6px; font-size: 13px; }
  .nav-item.active::after { left: 6px; right: 6px; }
}
@media (max-width: 920px) {
  .nav-links { display: none; }
  .brand-name { display: none; }
}
@media (max-width: 760px) {
  .hamburger-btn { display: flex; }
  .search-wrapper { max-width: none; }
  .upload-btn { padding: 0 10px; }
  .app-header { padding: 0 12px; gap: 8px; }
}
</style>

