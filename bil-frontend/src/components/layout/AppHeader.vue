<template>
  <header class="app-header">
    <router-link class="brand" to="/">BilBil</router-link>
    <el-input v-model="keyword" class="search" aria-label="搜索视频、UP主、番剧" @keyup.enter="goSearch" />
    <nav class="actions">
      <el-button class="bil-gradient-button" round @click="goCreator">投稿</el-button>
      <el-button circle @click="themeStore.toggleTheme">{{ themeStore.mode === 'light' ? '夜' : '日' }}</el-button>
      <button class="notice" type="button" @click="userStore.markNotificationDot(false)">
        消息
        <span v-if="userStore.notificationDot" class="dot" />
      </button>
      <el-avatar v-if="userStore.isLogin" :size="34">{{ userStore.profile?.useName?.slice(0, 1) }}</el-avatar>
      <el-button v-else text @click="userStore.openLoginDialog">登录</el-button>
    </nav>
  </header>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElAvatar, ElButton, ElInput } from 'element-plus'
import { eventBus } from '@/utils/eventBus'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const keyword = ref('')
const userStore = useUserStore()
const themeStore = useThemeStore()

function goSearch() {
  router.push({ name: 'search', query: { keyword: keyword.value } })
}

function goCreator() {
  router.push({ name: 'creator' })
}

function markDot() {
  userStore.markNotificationDot(true)
}

onMounted(() => {
  eventBus.on('video:liked', markDot)
  eventBus.on('video:collected', markDot)
})

onUnmounted(() => {
  eventBus.off('video:liked', markDot)
  eventBus.off('video:collected', markDot)
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
  gap: 20px;
  padding: 0 24px;
  background: color-mix(in srgb, var(--bil-surface) 92%, transparent);
  border-bottom: 1px solid var(--bil-border);
  backdrop-filter: blur(16px);
}

.brand {
  font-size: 24px;
  font-weight: 800;
  background: var(--bil-gradient);
  -webkit-background-clip: text;
  color: transparent;
}

.search {
  max-width: 520px;
}

.actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice {
  position: relative;
  border: 0;
  background: transparent;
  color: var(--bil-text);
  cursor: pointer;
}

.dot {
  position: absolute;
  top: -2px;
  right: -5px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--bil-pink);
}

:deep(.el-input__wrapper) {
  border-radius: 999px;
}

@media (max-width: 760px) {
  .search {
    display: none;
  }
}
</style>
