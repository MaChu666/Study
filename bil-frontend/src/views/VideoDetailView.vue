<template>
  <section class="detail-view">
    <template v-if="loading">
      <div class="detail-view">
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
      </div>
    </template>

    <template v-else>
      <div class="main-column">
        <div class="player-box">
	<div v-if="isTranscoding" class="transcode-notice"><svg viewBox="0 0 24 24" width="40" height="40" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg><p>视频转码中，请稍后再来观看...</p></div>
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
        <h1>{{ video.videoName }}</h1>
        <div class="uploader-bar" v-if="video.userId">
          <UserBadge
            :user-id="video.userId"
            :user-name="uploaderInfo.useName"
            :avatar="uploaderInfo.avatar"
            size="md"
          />
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
        <p class="meta">{{ video.playCount || 0 }} 播放 · {{ video.danmuCount || 0 }} 弹幕</p>
        <InteractionBar
          :video="video"
          :liked="liked"
          :collected="collected"
          :coined="coined"
          @update:liked="liked = $event"
          @update:collected="collected = $event"
          @update:coined="coined = $event"
        />
        <CommentList :video-id="String(videoId)" />
      </div>
      <div class="side-column">
        <DanmuPanel :video-id="String(videoId)" :file-id="fileId" :settings="danmuSettings" />
        <section class="related">
          <h2>相关推荐</h2>
          <VideoCard v-for="item in related" :key="item.videoId" :video="item" @play="openVideo" />
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
import VideoCard from '@/components/video/VideoCard.vue'
import VideoPlayer from '@/components/video/VideoPlayer.vue'
import UserBadge from '@/components/user/UserBadge.vue'
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
function onTimeUpdate(time) {}

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
  border-radius: 12px;
}
.transcode-notice svg {
  color: #aaa;
}

.player-box {
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  aspect-ratio: 16 / 9;
}
.player-box :deep(.video-player-wrapper) {
  width: 100%; height: 100%;
}
.player-box :deep(video) {
  width: 100%; height: 100%;
  object-fit: contain;
  background: #000;
}

h1 { margin: 18px 0 8px; font-size: 24px; }
/* Uploader info bar */
.uploader-bar {
  display: flex; align-items: center; gap: 10px; margin-bottom: 6px;
}
.follow-btn {
  padding: 4px 14px; border: 1px solid var(--bil-primary);
  border-radius: 6px; background: var(--bil-primary); color: #fff;
  font-size: 13px; cursor: pointer; transition: all 0.2s;
  white-space: nowrap;
}
.follow-btn.is-following { background: transparent; color: var(--bil-text); border-color: var(--bil-border); }
.follow-btn.is-mutual { background: transparent; color: var(--bil-pink); border-color: var(--bil-pink); }
.follow-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.follow-btn.is-following:hover { border-color: var(--bil-pink); color: var(--bil-pink); }
.meta { color: var(--bil-muted); }
.related { display: grid; gap: 16px; margin-top: 18px; }
.related h2 { margin: 0; font-size: 18px; }

.skeleton-player {
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
.skeleton-line.short {
  width: 40%;
}
.skeleton-sidebar {
  background: var(--bil-surface);
  border-radius: 12px;
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
