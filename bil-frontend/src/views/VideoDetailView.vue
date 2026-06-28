<template>
  <section class="detail-view">
    <div class="main-column">
      <div class="player-box">
        <video controls :poster="video.videoCover" :src="videoSource" />
      </div>
      <h1>{{ video.videoName }}</h1>
      <p class="meta">{{ video.playCount || 0 }} 播放 · {{ video.danmuCount || 0 }} 弹幕</p>
      <InteractionBar :video="video" />
      <CommentList :video-id="String(videoId)" />
    </div>
    <div class="side-column">
      <DanmuPanel :video-id="String(videoId)" :file-id="fileId" />
      <section class="related">
        <h2>相关推荐</h2>
        <VideoCard v-for="item in related" :key="item.videoId" :video="item" @play="openVideo" />
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVideoInfoApi, getVideoRecommendApi, loadVideoPListApi } from '@/api/modules/video'
import CommentList from '@/components/video/CommentList.vue'
import DanmuPanel from '@/components/video/DanmuPanel.vue'
import InteractionBar from '@/components/video/InteractionBar.vue'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const videoId = computed(() => route.params.videoId || '')
const video = ref(mockVideos[0])
const related = ref(mockVideos.slice(1))
const fileId = ref('')
const mockFallbackVideo = computed(() => mockVideos.find((item) => item.videoId === String(videoId.value)) || mockVideos[0])
const videoSource = computed(() => {
  if (video.value?.videoUrl) return video.value.videoUrl
  if (video.value?.filePath) return video.value.filePath
  return mockFallbackVideo.value.videoUrl
})

async function loadDetail() {
  const currentId = String(videoId.value)
  const fallbackVideo = mockVideos.find((item) => item.videoId === currentId) || mockVideos[0]
  fileId.value = ''

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
    if (firstFile?.filePath && !video.value?.videoUrl) {
      video.value = { ...video.value, filePath: firstFile.filePath }
      playerStore.play(video.value)
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
}

function openVideo(item) {
  playerStore.play(item)
  router.push({ name: 'video-detail', params: { videoId: item.videoId } })
}

watch(videoId, () => {
  loadDetail()
}, { immediate: true })
</script>

<style scoped>
.detail-view {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 22px;
}

.main-column,
.side-column {
  min-width: 0;
}

.player-box {
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 12px;
  background: #050505;
}

video {
  width: 100%;
  height: 100%;
}

h1 {
  margin: 18px 0 6px;
  font-size: 24px;
}

.meta {
  color: var(--bil-muted);
}

.related {
  display: grid;
  gap: 16px;
  margin-top: 18px;
}

.related h2 {
  margin: 0;
  font-size: 18px;
}

@media (max-width: 980px) {
  .detail-view {
    grid-template-columns: 1fr;
  }
}
</style>
