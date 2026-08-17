<template>
  <article class="video-card" @click="$emit('play', video)">
    <div class="cover-wrap">
      <img class="cover" :src="video.videoCover" :alt="video.videoName" loading="lazy" />
      <span class="duration">{{ formatDuration(video.duration) }}</span>
      <span class="play-count-badge" v-if="video.playCount">
        <svg class="play-icon" viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
          <path d="M8 5v14l11-7z" />
        </svg>
        {{ formatCount(video.playCount) }}
      </span>
      <span class="hover-mask">
        <svg viewBox="0 0 24 24" width="26" height="26" fill="currentColor">
          <path d="M8 5v14l11-7z" />
        </svg>
      </span>
    </div>
    <h3 class="title">{{ video.videoName }}</h3>
    <div class="author-row">
      <img v-if="video.userAvatar" class="author-avatar" :src="video.userAvatar" alt="" loading="lazy" />
      <span v-else class="author-avatar author-avatar-text">{{ (video.userName || video.useName || 'U').slice(0, 1) }}</span>
      <span class="author-name">{{ video.userName || video.useName || '未知UP主' }}</span>
    </div>
    <footer class="meta">
      <span>{{ formatCount(video.playCount) }}播放</span>
      <span class="meta-divider">·</span>
      <span>{{ formatCount(video.danmuCount) }}弹幕</span>
      <span class="meta-divider">·</span>
      <span>{{ timeAgo(video.createTime || video.postTime) }}</span>
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
  border-radius: var(--bil-radius-lg);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: transform 0.2s ease;
}
.video-card:hover {
  transform: translateY(-2px);
}
.video-card:hover .cover {
  transform: scale(1.06);
}
.video-card:hover .hover-mask {
  opacity: 1;
}

.cover-wrap {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: var(--bil-radius-lg);
  background: var(--bil-border);
}
.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 1px 6px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 12px;
  line-height: 18px;
}
.play-count-badge {
  position: absolute;
  left: 8px;
  bottom: 8px;
  display: flex;
  align-items: center;
  gap: 3px;
  color: #fff;
  font-size: 12px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.7);
  line-height: 18px;
}
.play-icon {
  flex-shrink: 0;
}
.hover-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.title {
  margin: 10px 0 6px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  transition: color 0.2s;
}
.video-card:hover .title { color: var(--bil-pink); }

.author-row {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  margin-top: auto;
}
.author-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  background: var(--bil-pink);
}
.author-avatar-text {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 10px;
  font-weight: 600;
}
.author-name {
  font-size: 13px;
  color: var(--bil-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}
.video-card:hover .author-name { color: var(--bil-pink); }

.meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  color: var(--bil-muted);
  font-size: 12px;
  line-height: 1.4;
}
.meta-divider { margin: 0 2px; }
</style>
