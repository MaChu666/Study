<template>
  <section v-if="player.current" class="mini-player">
    <button type="button" class="play" @click="player.togglePlay">{{ player.isPlaying ? '暂停' : '播放' }}</button>
    <div class="meta">
      <strong>{{ player.current.videoName }}</strong>
      <span>{{ player.queue.length }} 个视频在队列中</span>
    </div>
    <button type="button" class="close" @click="player.clearQueue">关闭</button>
  </section>
</template>

<script setup>
import { usePlayerStore } from '@/stores/player'

const player = usePlayerStore()
</script>

<style scoped>
.mini-player {
  position: fixed;
  left: 50%;
  bottom: 18px;
  z-index: 30;
  width: min(560px, calc(100vw - 28px));
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  border: 1px solid var(--bil-border);
  border-radius: 16px;
  background: var(--bil-surface);
  box-shadow: var(--bil-shadow);
}

.play {
  border: 0;
  border-radius: 999px;
  padding: 9px 16px;
  color: #fff;
  background: var(--bil-gradient);
}

.meta {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.meta strong,
.meta span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta span {
  color: var(--bil-muted);
  font-size: 12px;
}

.close {
  margin-left: auto;
  border: 0;
  background: transparent;
  color: var(--bil-muted);
}
</style>
