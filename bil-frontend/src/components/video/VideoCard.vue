<template>
  <article class="video-card" @click="$emit('play', video)">
    <div class="cover-wrap">
      <img class="cover" :src="video.videoCover" :alt="video.videoName" />
      <span class="duration">{{ video.duration || '00:00' }}</span>
    </div>
    <h3 class="title">{{ video.videoName }}</h3>
    <p class="author">{{ video.userName || video.useName || 'BilBil UP主' }}</p>
    <footer class="meta">
      <span>{{ formatCount(video.playCount) }} 播放</span>
      <span>{{ formatCount(video.danmuCount) }} 弹幕</span>
    </footer>
  </article>
</template>

<script setup>
defineProps({
  video: {
    type: Object,
    required: true
  }
})

defineEmits(['play'])

function formatCount(value) {
  const count = Number(value || 0)
  if (count >= 10000) {
    return `${(count / 10000).toFixed(1)}万`
  }
  return `${count}`
}
</script>

<style scoped>
.video-card {
  min-width: 0;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--bil-shadow);
}

.video-card:hover .cover {
  transform: scale(1.03);
}

.cover-wrap {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 12px;
  background: var(--bil-border);
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.duration {
  position: absolute;
  right: 10px;
  bottom: 10px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.68);
  color: #fff;
  font-size: 12px;
  line-height: 1.2;
}

.title {
  margin: 10px 0 4px;
  font-size: 15px;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.author,
.meta {
  margin: 0;
  color: var(--bil-muted);
  font-size: 13px;
  line-height: 1.4;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 6px;
}
</style>
