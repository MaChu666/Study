<template>
  <header class="app-header">
    <div class="left-group">
      <button class="hamburger-btn" @click="emit('toggle-sidebar')" aria-label="菜单">
        <span></span><span></span><span></span>
      </button>
      <router-link class="brand" to="/">VidVault</router-link>
      <nav class="nav-links">
        <router-link to="/" class="nav-item" active-class="active" exact-active-class="active" :exact="true">首页</router-link>
        <router-link to="/search" class="nav-item" active-class="active">发现</router-link>
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
      <button class="search-btn" @click="goSearch">
        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2.5">
          <circle cx="11" cy="11" r="8" />
          <path d="m21 21-4.35-4.35" />
        </svg>
      </button>
      <div class="suggest-drop" v-if="showSuggest && suggestList.length && keyword">
        <div v-for="t in suggestList" :key="t" class="suggest-item" @mousedown.prevent="pickSuggest(t)">{{ t }}</div>
      </div>
    </div>

    <div class="actions">
      <router-link to="/creator" v-if="userStore.isLogin">
        <el-button class="upload-btn" round>投稿</el-button>
      </router-link>
      <button class="theme-btn" @click="themeStore.toggleTheme" :title="themeStore.mode === 'light' ? '暗色模式' : '亮色模式'">
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
      <div class="msg-btn" @click="goMessages">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        <span v-if="userStore.unreadCount > 0" class="unread-badge">{{ userStore.unreadCount > 99 ? '99+' : userStore.unreadCount }}</span>
      </div>

      <div v-if="userStore.isLogin" class="user-menu" @click="menuOpen = !menuOpen">
        <img v-if="userStore.profile?.avatar" :src="userStore.profile.avatar" class="avatar-text avatar-img" />
        <span v-else class="avatar-text">{{ userStore.profile?.useName?.slice(0, 1) || 'U' }}</span>
        <div class="menu-drop" v-if="menuOpen" @click.stop>
          <div class="menu-item" @click="goProfile">个人中心</div>
          <div class="menu-item" @click="goCreator">创作中心</div>
          <div class="menu-divider" />
          <div class="menu-item" @click="userStore.logout">退出登录</div>
        </div>
      </div>
      <el-button v-else class="login-btn" round @click="userStore.openLoginDialog">登录</el-button>
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
  z-index: 20;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 24px;
  background: color-mix(in srgb, var(--bil-surface) 92%, transparent);
  border-bottom: 1px solid var(--bil-border);
  backdrop-filter: blur(16px);
}
.left-group { display: flex; align-items: center; gap: 20px; }
.brand {
  font-size: 22px; font-weight: 800;
  background: var(--bil-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  text-decoration: none;
}
.nav-links { display: flex; gap: 4px; }
.nav-item {
  padding: 6px 14px; border-radius: 6px; font-size: 14px;
  color: var(--bil-text); text-decoration: none; transition: background 0.2s;
}
.nav-item:hover, .nav-item.active { background: var(--bil-hover); color: var(--bil-primary); }

.search-wrapper {
  flex: 1; max-width: 480px; position: relative; display: flex;
}
.search-input {
  flex: 1; height: 38px; padding: 0 40px 0 16px; border: 1px solid var(--bil-border);
  border-radius: 999px; background: var(--bil-hover); color: var(--bil-text);
  font-size: 14px; outline: none; transition: border-color 0.2s;
}
.search-input:focus { border-color: var(--bil-primary); }
.search-btn {
  position: absolute; right: 4px; top: 4px; width: 30px; height: 30px;
  border: none; background: var(--bil-primary); color: #fff; border-radius: 50%; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
}
.suggest-drop {
  position: absolute; top: 42px; left: 0; right: 0; background: var(--bil-surface);
  border: 1px solid var(--bil-border); border-radius: 8px; padding: 6px 0; box-shadow: var(--bil-shadow); z-index: 30;
}
.suggest-item {
  padding: 8px 16px; font-size: 13px; color: var(--bil-text); cursor: pointer;
}
.suggest-item:hover { background: var(--bil-hover); }

.actions { margin-left: auto; display: flex; align-items: center; gap: 10px; }
.upload-btn { border-radius: 999px !important; }
.theme-btn { border: none; background: none; cursor: pointer; padding: 4px; display: flex; align-items: center; color: var(--bil-text); }
.msg-btn {
  position: relative; border: none; background: none; cursor: pointer; display: flex; align-items: center; color: var(--bil-text);
}
.unread-badge {
  position: absolute;
  top: -6px;
  right: -10px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--bil-pink);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  line-height: 18px;
  text-align: center;
  white-space: nowrap;
}

.user-menu { position: relative; cursor: pointer; }
.avatar-text {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px; border-radius: 50%; background: var(--bil-pink); color: #fff; font-size: 14px; font-weight: 600;
}
.avatar-text.avatar-img { object-fit: cover; background: none; }
.menu-drop {
  position: absolute; top: 42px; right: 0; min-width: 140px;
  background: var(--bil-surface); border: 1px solid var(--bil-border); border-radius: 8px;
  padding: 6px 0; box-shadow: var(--bil-shadow); z-index: 30;
}
.menu-item {
  padding: 8px 20px; font-size: 13px; color: var(--bil-text); cursor: pointer; white-space: nowrap;
}
.menu-item:hover { background: var(--bil-hover); }
.menu-divider { height: 1px; background: var(--bil-border); margin: 4px 0; }
.login-btn { border-radius: 999px !important; }

/* Hamburger menu button */
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
}

.hamburger-btn span {
  display: block;
  width: 100%;
  height: 2px;
  background: var(--bil-text);
  border-radius: 2px;
  transition: transform 0.2s, opacity 0.2s;
}

@media (max-width: 760px) {
  .hamburger-btn { display: flex; flex-shrink: 0; }
  .nav-links { display: none; }
  .search-wrapper { max-width: 200px; }
}
</style>
