<template>
  <router-view />
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { eventBus } from '@/utils/eventBus'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'

const themeStore = useThemeStore()
const userStore = useUserStore()

onMounted(() => {
  themeStore.applyTheme()
  eventBus.on('auth:required', userStore.openLoginDialog)
  userStore.autoLogin().catch(() => {
    userStore.openLoginDialog()
  })
})

onUnmounted(() => {
  eventBus.off('auth:required', userStore.openLoginDialog)
})
</script>
