<template>
  <section class="home-view">
    <div class="section-head">
      <div class="heading">
        <h1>推荐视频</h1>
        <p>发现今天值得点开的内容</p>
      </div>
      <el-button text @click="loadVideos">刷新</el-button>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated />

    <div v-else class="video-grid">
      <VideoCard
        v-for="video in videos"
        :key="video.videoId"
        :video="video"
        @play="openVideo"
      />
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadRecommendVideoApi, loadVideoApi } from '@/api/modules/video'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const loading = ref(false)
const videos = ref(mockVideos)

async function loadVideos() {
  loading.value = true
  try {
    const categoryId = route.query.pCategoryId
    const data = categoryId
      ? await loadVideoApi({ pCategoryId: categoryId, categoryId: '0', pageNo: 1 })
      : await loadRecommendVideoApi()
    const list = normalizeVideoList(data)
    videos.value = list.length ? list : mockVideos
  } catch {
    videos.value = mockVideos
  } finally {
    loading.value = false
  }
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(loadVideos)

watch(() => route.query.pCategoryId, loadVideos)
</script>

<style scoped>
.home-view {
  min-width: 0;
}

.section-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.heading {
  min-width: 0;
}

h1 {
  margin: 0;
  font-size: 28px;
  line-height: 1.15;
}

p {
  margin: 8px 0 0;
  color: var(--bil-muted);
  line-height: 1.5;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 24px 20px;
}

@media (max-width: 760px) {
  .section-head {
    align-items: start;
  }

  h1 {
    font-size: 24px;
  }
}
</style>
