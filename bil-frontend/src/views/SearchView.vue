<template>
  <section class="search-view">
    <div class="search-panel">
      <el-input
        v-model="keyword"
        aria-label="输入关键词搜索"
        placeholder="搜索视频、UP主或话题"
        @keyup.enter="runSearch"
      />
      <el-button class="bil-gradient-button" @click="runSearch">搜索</el-button>
    </div>

    <div class="hotwords">
      <span class="hotwords-label">热词</span>
      <button
        v-for="word in hotwords"
        :key="word"
        type="button"
        class="hotword-chip"
        @click="useWord(word)"
      >
        {{ word }}
      </button>
    </div>

    <div class="video-grid">
      <VideoCard
        v-for="video in results"
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
import { getSearchKeywordTopApi, searchVideoApi } from '@/api/modules/video'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const keyword = ref(String(route.query.keyword || ''))
const hotwords = ref(['游戏', '弹幕', '前端', '美食'])
const results = ref(mockVideos)

async function loadHotwords() {
  try {
    const data = await getSearchKeywordTopApi()
    hotwords.value = Array.isArray(data) && data.length ? data : hotwords.value
  } catch {
    hotwords.value = ['游戏', '弹幕', '前端', '美食']
  }
}

async function fetchResults(nextKeyword) {
  if (!nextKeyword) {
    results.value = mockVideos
    return
  }
  try {
    const data = await searchVideoApi({ keyword: nextKeyword })
    const list = normalizeVideoList(data)
    results.value = list.length ? list : mockVideos
  } catch {
    results.value = mockVideos
  }
}

async function runSearch() {
  const nextKeyword = String(keyword.value || '').trim()
  keyword.value = nextKeyword
  if (nextKeyword === String(route.query.keyword || '')) {
    await fetchResults(nextKeyword)
    return
  }
  await router.replace({
    name: 'search',
    query: nextKeyword ? { keyword: nextKeyword } : {}
  })
}

function useWord(word) {
  keyword.value = word
  return runSearch()
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(async () => {
  await loadHotwords()
})

watch(
  () => route.query.keyword,
  async (value) => {
    const nextKeyword = String(value || '')
    if (nextKeyword !== keyword.value) {
      keyword.value = nextKeyword
    }
    await fetchResults(nextKeyword)
  },
  { immediate: true }
)
</script>

<style scoped>
.search-view {
  min-width: 0;
}

.search-panel {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  margin-bottom: 16px;
}

.hotwords {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
}

.hotwords-label {
  color: var(--bil-muted);
  font-size: 13px;
}

.hotword-chip {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 7px 12px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
  line-height: 1.2;
}

.hotword-chip:hover {
  border-color: var(--bil-blue);
  color: var(--bil-blue);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 24px 20px;
}

@media (max-width: 760px) {
  .search-panel {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
