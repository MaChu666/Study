<template>
  <section class="detail-view">
    <template v-if="loading">
      <div class="main-column">
        <div class="player-box skeleton-player" />
        <div class="skeleton-title-line" />
        <div class="skeleton-line" />
        <div class="skeleton-line short" />
      </div>
      <div class="side-column">
        <div class="skeleton-sidebar">
          <div class="skeleton-line" />
          <div class="skeleton-line" />
          <div class="skeleton-line short" />
        </div>
      </div>
    </template>

    <template v-else>
      <div class="main-column">
        <div class="player-box">
          <div v-if="isTranscoding" class="transcode-notice">
            <svg viewBox="0 0 24 24" width="40" height="40" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            <p>视频转码中，请稍后再来观看...</p>
          </div>
          <VideoPlayer
            v-else
            :src="videoSource"
            :poster="video.videoCover || ''"
            :video-id="String(videoId)"
            :file-id="fileId"
            :next-video="nextVideo"
            @danmuSettings="onDanmuSettings"
            @timeUpdate="onTimeUpdate"
            @nextVideo="onNextVideo"
          />
        </div>

        <h1 class="video-title">{{ video.videoName }}</h1>
        <div class="video-meta">
          <span>{{ formatCount(video.playCount) }}播放</span>
          <span class="meta-dot">·</span>
          <span>{{ formatCount(video.danmuCount) }}弹幕</span>
          <span class="meta-dot">·</span>
          <span>{{ formatTime(video.createTime) }}</span>
        </div>

        <div class="uploader-card" v-if="video.userId">
          <img v-if="uploaderInfo.avatar" :src="uploaderInfo.avatar" class="uploader-avatar" alt="" />
          <span v-else class="uploader-avatar uploader-avatar-text">{{ (uploaderInfo.useName || 'U').slice(0, 1) }}</span>
          <div class="uploader-info">
            <span class="uploader-name">{{ uploaderInfo.useName || 'UP主' }}</span>
            <span class="uploader-sub">UP主</span>
          </div>
          <button
            v-if="!isUploaderSelf"
            class="follow-btn"
            :class="{ 'is-following': followStatus === 'following', 'is-mutual': followStatus === 'mutual' }"
            type="button"
            :disabled="followLoading"
            @click="toggleFollow"
          >
            <template v-if="followLoading">...</template>
            <template v-else-if="followStatus === 'mutual'">已互粉</template>
            <template v-else-if="followStatus === 'following'">已关注</template>
            <template v-else>关注</template>
          </button>
        </div>

        <div class="action-bar">
          <InteractionBar
            :video="video"
            :liked="liked"
            :collected="collected"
            :coined="coined"
            @update:liked="liked = $event"
            @update:collected="collected = $event"
            @update:coined="coined = $event"
          />
        </div>

        <div class="desc-card">
          <p class="desc-text">{{ video.introduction || '简介：UP主很懒，什么都没有留下~' }}</p>
          <div class="tag-row" v-if="video.tags">
            <span v-for="t in splitTags(video.tags)" :key="t" class="tag-chip" @click="searchTag(t)">{{ t }}</span>
          </div>
        </div>

        <CommentList :video-id="String(videoId)" :author-id="String(video.userId || '')" />
      </div>

      <div class="side-column">
        <DanmuPanel :video-id="String(videoId)" :file-id="fileId" :settings="danmuSettings" :current-time="playerTime" />
        <section class="related">
          <h2 class="related-title">相关推荐</h2>
          <div class="related-list">
            <div v-for="item in related" :key="item.videoId" class="related-item" @click="openVideo(item)">
              <img class="related-cover" :src="item.videoCover" :alt="item.videoName" loading="lazy" />
              <div class="related-info">
                <span class="related-name">{{ item.videoName }}</span>
                <span class="related-meta">{{ formatCount(item.playCount) }}播放 · {{ formatCount(item.danmuCount) }}弹幕</span>
              </div>
            </div>
          </div>
        </section>
      </div>
    </template>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVideoInfoApi, getVideoRecommendApi, loadVideoPListApi } from '@/api/modules/video'
import { getUserInfoApi, focusApi, cancelFocusApi, loadFocusListApi, loadFansListApi } from '@/api/modules/user'
import CommentList from '@/components/video/CommentList.vue'
import DanmuPanel from '@/components/video/DanmuPanel.vue'
import InteractionBar from '@/components/video/InteractionBar.vue'
import VideoPlayer from '@/components/video/VideoPlayer.vue'
import request from '@/api/request'
import { usePlayerStore } from '@/stores/player'
import { useUserStore } from '@/stores/user'
import { mockVideos } from '@/utils/mockData'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const userStore = useUserStore()
const videoId = computed(() => route.params.videoId || '')
const loading = ref(true)
const video = ref(mockVideos[0])
const related = ref(mockVideos.slice(1))
const fileId = ref('')
const videoFile = ref(null)
const uploaderInfo = ref({})
const followStatus = ref('none') // 'none' | 'following' | 'mutual'
const followLoading = ref(false)

const isUploaderSelf = computed(() => {
  const viewerId = userStore.profile?.userId
  const uploaderId = video.value?.userId
  return Boolean(viewerId && uploaderId && String(viewerId) === String(uploaderId))
})
const mockFallbackVideo = computed(() => mockVideos.find((item) => item.videoId === String(videoId.value)) || mockVideos[0])
const videoSource = computed(() => {
  const fp = videoFile.value?.filePath || video.value?.filePath
  if (fp && (fp.endsWith('.m3u8') || fp.endsWith('.mp4'))) return fp
  if (video.value?.videoUrl && video.value.videoUrl !== '/cover/default.png') return video.value.videoUrl
  return ''
})
const isTranscoding = computed(() =>
  videoFile.value && !videoFile.value.filePath && videoFile.value.status !== 2
)
const nextVideo = computed(() => {
  const list = related.value || []
  return list.find((item) => String(item.videoId) !== String(videoId.value)) || list[0] || null
})

function formatCount(n) {
  const count = Number(n || 0)
  if (count >= 10000) return (count / 10000).toFixed(1) + '万'
  return String(count)
}

function formatTime(dateStr) {
  if (!dateStr) return '--'
  try {
    const date = new Date(dateStr.replace(/-/g, '/'))
    if (isNaN(date.getTime())) return dateStr
    const now = new Date()
    const diff = now - date
    if (diff < 7 * 24 * 3600 * 1000) {
      const days = Math.floor(diff / (24 * 3600 * 1000))
      if (days < 1) return '今天'
      return days + '天前'
    }
    const y = date.getFullYear()
    const m = String(date.getMonth() + 1).padStart(2, '0')
    const d = String(date.getDate()).padStart(2, '0')
    return `${y}-${m}-${d}`
  } catch {
    return dateStr
  }
}

function splitTags(tags) {
  if (!tags) return []
  return String(tags).split(/[,，、\s]+/).filter(Boolean)
}

function searchTag(t) {
  router.push({ name: 'search', query: { keyword: t } })
}

async function loadDetail() {
  const currentId = String(videoId.value)
  const fallbackVideo = mockVideos.find((item) => item.videoId === currentId) || mockVideos[0]
  fileId.value = ''
  videoFile.value = null
  loading.value = true

  try {
    const data = await getVideoInfoApi({ videoId: currentId })
    video.value = {
      ...fallbackVideo,
      ...(data || {}),
      videoUrl: data?.videoUrl || '',
      filePath: data?.filePath || ''
    }
    playerStore.play(video.value)
  } catch {
    video.value = fallbackVideo
    playerStore.play(video.value)
  }

  try {
    const files = await loadVideoPListApi({ videoId: currentId })
    const firstFile = Array.isArray(files) ? files[0] : null
    fileId.value = firstFile?.fileId || ''
    videoFile.value = firstFile
    if (firstFile && !firstFile.filePath && firstFile.status !== 2) {
      setTimeout(() => loadDetail(), 10000)
    }
    if (firstFile?.filePath) {
      video.value = { ...video.value, filePath: firstFile.filePath }
    }
  } catch {
    fileId.value = ''
  }

  try {
    const data = await getVideoRecommendApi({ videoId: currentId })
    related.value = Array.isArray(data) && data.length ? data : mockVideos.filter((item) => item.videoId !== currentId)
  } catch {
    related.value = mockVideos.filter((item) => item.videoId !== currentId)
  }
  try { checkInteractionStatus(currentId) } catch (_) {}
  loading.value = false
}

async function checkInteractionStatus(currentId) {
  if (!userStore.isLogin) return
  try {
    const res = await request.post('/userAction/checkStatus', { videoId: currentId })
    if (res) {
      liked.value = Boolean(res.liked)
      collected.value = Boolean(res.collected)
      coined.value = Boolean(res.coined)
    }
  } catch (_) {}
}

const playerTime = ref(0)
const danmuSettings = ref({ on: true, opacity: 0.8, fontSize: 'normal', area: 'full', speed: 'normal' })
const liked = ref(false)
const collected = ref(false)
const coined = ref(false)

watch(videoId, function () {
  liked.value = false
  collected.value = false
  coined.value = false
})

function onDanmuSettings(settings) { danmuSettings.value = settings }
function onTimeUpdate(time) { playerTime.value = Number(time) || 0 }

function openVideo(item) {
  playerStore.play(item)
  router.push({ name: 'video-detail', params: { videoId: item.videoId } })
}

function onNextVideo(videoData) {
  if (videoData && videoData.videoId) { openVideo(videoData) }
  else if (nextVideo.value) { openVideo(nextVideo.value) }
}

// --- Uploader info ---
async function loadUploaderInfo() {
  const uid = video.value?.userId
  if (!uid) { uploaderInfo.value = {}; return }
  try {
    const data = await getUserInfoApi({ userId: uid })
    uploaderInfo.value = data || {}
  } catch { uploaderInfo.value = {} }
  await checkFollowStatus()
}

async function checkFollowStatus() {
  const viewerId = userStore.profile?.userId
  const uploaderId = video.value?.userId
  if (!viewerId || !uploaderId || String(viewerId) === String(uploaderId)) {
    followStatus.value = 'none'
    return
  }
  try {
    const [focusList, fansList] = await Promise.all([
      loadFocusListApi({ userId: viewerId }),
      loadFansListApi({ userId: viewerId })
    ])
    const iFollow = Array.isArray(focusList) && focusList.some(f => String(f.focusUserId) === String(uploaderId))
    const theyFollowMe = Array.isArray(fansList) && fansList.some(f => String(f.userId) === String(uploaderId))
    if (iFollow && theyFollowMe) {
      followStatus.value = 'mutual'
    } else if (iFollow) {
      followStatus.value = 'following'
    } else {
      followStatus.value = 'none'
    }
  } catch { followStatus.value = 'none' }
}

async function toggleFollow() {
  const viewerId = userStore.profile?.userId
  const uploaderId = video.value?.userId
  if (!viewerId || !uploaderId) return
  followLoading.value = true
  try {
    if (followStatus.value === 'none') {
      await focusApi({ userId: viewerId, focusUserId: uploaderId })
      followStatus.value = 'following'
    } else {
      await cancelFocusApi({ userId: viewerId, focusUserId: uploaderId })
      followStatus.value = 'none'
    }
    // Re-check mutual status
    await checkFollowStatus()
  } catch {
    // handled by interceptor
  } finally {
    followLoading.value = false
  }
}

// Load uploader info after video is loaded
watch(videoId, () => { loadDetail().then(() => loadUploaderInfo()) }, { immediate: true })
</script>

<style scoped>
.detail-view {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 22px;
}
.main-column, .side-column { min-width: 0; }

.transcode-notice {
  aspect-ratio: 16/9;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  background: #111; color: #aaa; font-size: 16px; gap: 8px;
  border-radius: var(--bil-radius-lg);
}
.transcode-notice svg { color: #aaa; }

.player-box {
  border-radius: var(--bil-radius-lg);
  overflow: hidden;
  background: #000;
  display: flex;
  flex-direction: column;
  box-shadow: var(--bil-shadow);
}
.player-box :deep(.video-player-wrapper) { width: 100%; }
.player-box :deep(video) {
  width: 100%; height: 100%;
  object-fit: contain;
  background: #000;
}

.video-title {
  margin: 16px 0 8px;
  font-size: 20px;
  font-weight: 600;
  line-height: 1.4;
}
.video-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--bil-muted);
  font-size: 13px;
}
.meta-dot { color: var(--bil-border); }

/* Uploader card */
.uploader-card {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding: 12px 16px;
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  box-shadow: var(--bil-shadow);
}
.uploader-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  background: var(--bil-pink);
  flex-shrink: 0;
}
.uploader-avatar-text {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}
.uploader-info { min-width: 0; flex: 1; }
.uploader-name {
  display: block;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.uploader-sub { font-size: 12px; color: var(--bil-muted); }
.follow-btn {
  padding: 6px 18px;
  border: 1px solid var(--bil-pink);
  border-radius: var(--bil-radius);
  background: var(--bil-pink);
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.follow-btn:hover { background: var(--bil-pink-hover); }
.follow-btn.is-following,
.follow-btn.is-mutual {
  background: var(--bil-surface);
  color: var(--bil-text);
  border-color: var(--bil-border);
}
.follow-btn.is-following:hover,
.follow-btn.is-mutual:hover { border-color: var(--bil-pink); color: var(--bil-pink); background: var(--bil-surface); }
.follow-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.action-bar { margin: 14px 0; }

/* Description card */
.desc-card {
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  padding: 14px 16px;
  box-shadow: var(--bil-shadow);
  margin-bottom: 20px;
}
.desc-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}
.tag-chip {
  padding: 2px 10px;
  background: var(--bil-hover);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  color: var(--bil-primary);
  transition: background 0.15s, color 0.15s;
}
.tag-chip:hover { background: var(--bil-pink); color: #fff; }

/* Related */
.related { margin-top: 18px; }
.related-title { margin: 0 0 12px; font-size: 17px; font-weight: 600; }
.related-list { display: flex; flex-direction: column; gap: 14px; }
.related-item {
  display: flex;
  gap: 10px;
  cursor: pointer;
  min-width: 0;
}
.related-cover {
  width: 150px;
  aspect-ratio: 16 / 9;
  border-radius: var(--bil-radius);
  object-fit: cover;
  flex-shrink: 0;
  background: var(--bil-border);
  transition: transform 0.2s ease;
}
.related-item:hover .related-cover { transform: scale(1.02); }
.related-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 2px 0;
}
.related-name {
  font-size: 13px;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  transition: color 0.2s;
}
.related-item:hover .related-name { color: var(--bil-pink); }
.related-meta { font-size: 12px; color: var(--bil-muted); }

/* Skeleton */
.skeleton-player {
  aspect-ratio: 16 / 9;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}
.skeleton-title-line {
  height: 24px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-top: 18px;
  width: 70%;
}
.skeleton-line {
  height: 14px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-bottom: 8px;
}
.skeleton-line.short { width: 40%; }
.skeleton-sidebar {
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  padding: 18px;
}
@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 980px) {
  .detail-view { grid-template-columns: 1fr; }
  .side-column { display: none; }
}
</style>
