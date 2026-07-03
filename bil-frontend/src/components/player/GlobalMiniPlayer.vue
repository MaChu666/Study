<template>
  <Transition name="mini-player">
    <section v-if="visible" class="mini-player">
      <!-- Thumbnail -->
      <div class="thumbnail-box" @click="goToVideo">
        <img
          v-if="player.current?.videoCover"
          :src="player.current.videoCover"
          alt=""
          class="thumbnail"
        />
        <span v-else class="thumbnail-placeholder">BIL</span>
      </div>

      <!-- Title -->
      <div class="meta" @click="goToVideo">
        <strong class="title">{{ player.current?.videoName || '未知视频' }}</strong>
        <div class="progress-track">
          <div
            class="progress-fill"
            :class="{ playing: player.isPlaying, paused: !player.isPlaying }"
          />
        </div>
      </div>

      <!-- Play / Pause button -->
      <button
        type="button"
        class="ctrl-btn play-btn"
        :title="player.isPlaying ? '暂停' : '播放'"
        @click="player.togglePlay"
      >
        <svg v-if="player.isPlaying" viewBox="0 0 24 24" class="icon">
          <rect x="6" y="4" width="4" height="16" rx="1" fill="currentColor" />
          <rect x="14" y="4" width="4" height="16" rx="1" fill="currentColor" />
        </svg>
        <svg v-else viewBox="0 0 24 24" class="icon">
          <path d="M8 5v14l11-7z" fill="currentColor" />
        </svg>
      </button>

      <!-- Close button -->
      <button
        type="button"
        class="ctrl-btn close-btn"
        title="关闭"
        @click="player.clearQueue"
      >
        <svg viewBox="0 0 24 24" class="icon">
          <path
            d="M18 6L6 18M6 6l12 12"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            fill="none"
          />
        </svg>
      </button>
    </section>
  </Transition>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlayerStore } from '@/stores/player'

const route = useRoute()
const router = useRouter()
const player = usePlayerStore()

const visible = computed(() => {
  return route.name !== 'video-detail' && player.current !== null
})

function goToVideo() {
  if (player.current?.videoId) {
    router.push({ name: 'video-detail', params: { videoId: player.current.videoId } })
  }
}
</script>

<style scoped>
/* ============================================================
   Mini player container — fixed bottom-right
   ============================================================ */
.mini-player {
  position: fixed;
  right: 18px;
  bottom: 18px;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 380px;
  max-width: calc(100vw - 36px);
  padding: 10px 14px;
  border: 1px solid var(--bil-border);
  border-radius: 14px;
  background: var(--bil-surface);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.45);
}

/* ---- Thumbnail ---- */
.thumbnail-box {
  flex-shrink: 0;
  width: 52px;
  height: 32px;
  border-radius: 6px;
  overflow: hidden;
  background: #1a1a1a;
  cursor: pointer;
}

.thumbnail {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1px;
  color: var(--bil-primary);
}

/* ---- Meta (title + progress) ---- */
.meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  cursor: pointer;
}

.title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  line-height: 1.3;
}

.progress-track {
  width: 100%;
  height: 3px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  width: 100%;
  border-radius: 2px;
  background: var(--bil-gradient);
}

.progress-fill.playing {
  animation: progress-pulse 1.8s ease-in-out infinite;
}

.progress-fill.paused {
  animation: none;
  opacity: 0.4;
}

@keyframes progress-pulse {
  0% {
    width: 0%;
    margin-left: 0%;
  }
  50% {
    width: 60%;
    margin-left: 40%;
  }
  100% {
    width: 0%;
    margin-left: 100%;
  }
}

/* ---- Control buttons ---- */
.ctrl-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: 0;
  border-radius: 999px;
  background: transparent;
  color: var(--bil-text, #e8e8e8);
  cursor: pointer;
  transition: background 0.15s;
}

.ctrl-btn:hover {
  background: rgba(255, 255, 255, 0.08);
}

.ctrl-btn .icon {
  width: 20px;
  height: 20px;
}

.play-btn {
  color: var(--bil-primary, #00a1d6);
}

.close-btn {
  color: var(--bil-muted, #999);
}

.close-btn:hover {
  color: var(--bil-pink);
}

/* ============================================================
   Transition: slide up / fade
   ============================================================ */
.mini-player-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.mini-player-leave-active {
  transition: all 0.25s ease-in;
}

.mini-player-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.mini-player-leave-to {
  opacity: 0;
  transform: translateY(12px);
}
</style>
