<template>
  <div class="main-layout">
    <AppHeader />
    <div class="layout-body">
      <SideCategoryNav />
      <main class="content">
        <router-view />
      </main>
    </div>
    <GlobalMiniPlayer />
    <LoginDialog />
  </div>
</template>

<script setup>
import LoginDialog from '@/components/auth/LoginDialog.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import SideCategoryNav from '@/components/layout/SideCategoryNav.vue'
import GlobalMiniPlayer from '@/components/player/GlobalMiniPlayer.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: var(--bil-bg);
}

.layout-body {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 18px;
  max-width: 1440px;
  margin: 0 auto;
  padding: 84px 24px 96px;
}

.content {
  min-width: 0;
}

@media (max-width: 760px) {
  .layout-body {
    display: block;
    padding: 72px 14px 90px;
  }
}
</style>
