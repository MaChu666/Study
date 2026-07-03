<template>
  <section class="series-detail">
    <!-- Loading state -->
    <template v-if="loading">
      <div class="series-header-skeleton">
        <div class="skeleton-line skeleton-title" />
        <div class="skeleton-line skeleton-desc" />
        <div class="skeleton-line skeleton-meta" />
      </div>
      <div class="video-list-skeleton">
        <div v-for="n in 4" :key="n" class="skeleton-video-item">
          <div class="skeleton-line skeleton-ep" />
          <div class="skeleton-line skeleton-cover" />
          <div class="skeleton-line skeleton-ep-title" />
        </div>
      </div>
    </template>

    <!-- Error state -->
    <template v-else-if="error">
      <div class="error-state">
        <p>{{ error }}</p>
        <button class="retry-btn" type="button" @click="loadSeries">重试</button>
      </div>
    </template>

    <!-- Empty state -->
    <template v-else-if="!series">
      <div class="empty-state">暂无系列信息</div>
    </template>

    <!-- Content -->
    <template v-else>
      <div class="series-header">
        <h1>{{ series.seriesName || series.name || '未命名系列' }}</h1>
        <p v-if="series.seriesDescription || series.description" class="series-desc">
          {{ series.seriesDescription || series.description }}
        </p>
        <p class="series-meta">
          {{ videos.length }} 个视频
          <template v-if="series.updateTime || series.createTime">
            · 更新于 {{ formatDate(series.updateTime || series.createTime) }}
          </template>
        </p>
      </div>

      <div v-if="videos.length" class="video-list">
        <div
          v-for="(v, idx) in videos"
          :key="v.videoId"
          class="series-video-item"
          @click="openVideo(v)"
        >
          <span class="ep-index">{{ idx + 1 }}</span>
          <img
            v-if="v.videoCover"
            :src="v.videoCover"
            class="ep-cover"
            alt=""
          />
          <div v-else class="ep-cover-placeholder" />
          <div class="ep-info">
            <h3>{{ v.videoName }}</h3>
            <p>{{ formatCount(v.playCount) }} 播放 · {{ formatDate(v.createTime) }}</p>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">暂无视频</div>
    </template>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlayerStore } from '@/stores/player'
import { getVideoSeriesDetailApi } from '@/api/modules/user'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()

const seriesId = computed(function () { return route.params.seriesId })

const loading = ref(true)
const error = ref('')
const series = ref(null)
const videos = ref([])

async function loadSeries() {
  var id = seriesId.value
  if (!id) {
    error.value = '缺少系列 ID'
    loading.value = false
    return
  }

  loading.value = true
  error.value = ''
  series.value = null
  videos.value = []

  try {
    var data = await getVideoSeriesDetailApi({ seriesId: id })
    if (data) {
      series.value = data
      videos.value = Array.isArray(data.videos)
        ? data.videos
        : (Array.isArray(data.videoList) ? data.videoList : [])
    } else {
      error.value = '未找到该系列'
    }
  } catch (_e) {
    error.value = '加载系列详情失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

function openVideo(v) {
  if (!v || !v.videoId) return
  playerStore.play(v)
  router.push({ name: 'video-detail', params: { videoId: v.videoId } })
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN')
}

function formatCount(value) {
  var count = Number(value || 0)
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return String(count)
}

watch(seriesId, function () {
  loadSeries()
}, { immediate: true })
</script>

<style scoped>
.series-detail {
  padding: 16px 0;
}

/* ---- Skeleton ---- */
.series-header-skeleton {
  margin-bottom: 24px;
}

.skeleton-line {
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-title {
  width: 260px;
  height: 28px;
  margin-bottom: 14px;
}

.skeleton-desc {
  width: 400px;
  height: 16px;
  margin-bottom: 8px;
}

.skeleton-meta {
  width: 200px;
  height: 14px;
}

.video-list-skeleton {
  display: grid;
  gap: 12px;
}

.skeleton-video-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
}

.skeleton-ep {
  width: 28px;
  height: 20px;
}

.skeleton-cover {
  width: 120px;
  height: 68px;
  flex-shrink: 0;
}

.skeleton-ep-title {
  flex: 1;
  height: 20px;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ---- States ---- */
.error-state,
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--bil-muted);
  font-size: 14px;
}

.error-state p {
  margin: 0 0 16px;
}

.retry-btn {
  padding: 8px 24px;
  border: 1px solid var(--bil-primary);
  border-radius: 8px;
  background: transparent;
  color: var(--bil-primary);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.retry-btn:hover {
  background: var(--bil-primary);
  color: #fff;
}

/* ---- Header ---- */
.series-header {
  margin-bottom: 20px;
}

.series-header h1 {
  margin: 0;
  font-size: 24px;
  color: var(--bil-text);
}

.series-desc {
  color: var(--bil-muted);
  margin: 8px 0 0;
  font-size: 14px;
  line-height: 1.5;
}

.series-meta {
  color: var(--bil-muted);
  font-size: 13px;
  margin: 4px 0 0;
}

/* ---- Video list ---- */
.video-list {
  display: grid;
  gap: 12px;
}

.series-video-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.series-video-item:hover {
  background: var(--bil-surface);
}

.ep-index {
  width: 28px;
  text-align: center;
  font-weight: 700;
  color: var(--bil-muted);
  flex-shrink: 0;
}

.ep-cover {
  width: 120px;
  height: 68px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.ep-cover-placeholder {
  width: 120px;
  height: 68px;
  border-radius: 6px;
  flex-shrink: 0;
  background: var(--bil-border);
}

.ep-info {
  flex: 1;
  min-width: 0;
}

.ep-info h3 {
  margin: 0 0 4px;
  font-size: 14px;
  color: var(--bil-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ep-info p {
  margin: 0;
  font-size: 12px;
  color: var(--bil-muted);
}

/* ---- Responsive ---- */
@media (max-width: 600px) {
  .ep-cover,
  .ep-cover-placeholder {
    width: 100px;
    height: 56px;
  }

  .series-header h1 {
    font-size: 20px;
  }
}
</style>
