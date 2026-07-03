<template>
  <article class="video-card" @click="$emit('play', video)">
    <div class="cover-wrap">
      <img class="cover" :src="video.videoCover" :alt="video.videoName" />
      <span class="duration">{{ formatDuration(video.duration) }}</span>
      <span class="play-count-badge" v-if="video.playCount">
        <svg class="play-icon" viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
          <path d="M8 5v14l11-7z" />
        </svg>
        {{ formatCount(video.playCount) }}
      </span>
    </div>
    <h3 class="title">{{ video.videoName }}</h3>
    <UserBadge
    class="author"
    :user-id="video.userId"
    :user-name="video.userName || video.useName"
    :avatar="video.userAvatar"
    size="sm"
  />
    <footer class="meta">
      <span>{{ formatCount(video.playCount) }} 播放</span>
      <span class="meta-divider">·</span>
      <span>{{ formatCount(video.danmuCount) }} 弹幕</span>
      <span class="meta-divider">·</span>
      <span>{{ timeAgo(video.createTime || video.postTime) }}</span>
    </footer>
  </article>
</template>

<script setup>
import UserBadge from '@/components/user/UserBadge.vue'

defineProps({
  video: {
    type: Object,
    required: true
  }
})

defineEmits(['play'])

function formatDuration(seconds) {
  const s = Number(seconds) || 0
  if (s < 60) return '00:' + String(s).padStart(2, '0')
  const m = Math.floor(s / 60)
  const sec = s % 60
  if (m < 60) return String(m).padStart(2, '0') + ':' + String(sec).padStart(2, '0')
  const h = Math.floor(m / 60)
  const min = m % 60
  return String(h) + ':' + String(min).padStart(2, '0') + ':' + String(sec).padStart(2, '0')
}

function formatCount(value) {
  const count = Number(value || 0)
  if (count >= 10000) {
    return `${(count / 10000).toFixed(1)}万`
  }
  return `${count}`
}

function timeAgo(dateStr) {
  if (!dateStr) return '--'
  try {
    const now = Date.now()
    const date = new Date(dateStr.replace(/-/g, '/')).getTime()
    if (isNaN(date)) return dateStr
    const diff = now - date
    const minutes = Math.floor(diff / 60000)
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    const hours = Math.floor(minutes / 60)
    if (hours < 24) return `${hours}小时前`
    const days = Math.floor(hours / 24)
    if (days < 30) return `${days}天前`
    const months = Math.floor(days / 30)
    if (months < 12) return `${months}个月前`
    return `${Math.floor(months / 12)}年前`
  } catch {
    return dateStr
  }
}
</script>

<style scoped>
.video-card {
  min-width: 0;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
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

.play-count-badge {
  position: absolute;
  left: 10px;
  bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 12px;
  line-height: 1.2;
}

.play-icon {
  flex-shrink: 0;
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
  align-items: center;
  gap: 4px;
  margin-top: 6px;
}

.meta-divider {
  margin: 0 2px;
}
</style>
