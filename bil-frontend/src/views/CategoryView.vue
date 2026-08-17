<template>
  <section class="category-view">
    <!-- 频道头部 -->
    <div class="cat-header">
      <div class="cat-title-row">
        <span class="cat-icon">{{ iconText }}</span>
        <div class="cat-title-info">
          <h1 class="cat-name">{{ categoryName }}</h1>
          <p class="cat-desc">分区视频 · 共 {{ totalCount }} 个视频</p>
        </div>
      </div>
      <div class="cat-sub-nav" v-if="categories.length">
        <button
          v-for="c in categories"
          :key="c.categoryId"
          type="button"
          class="cat-sub-item"
          :class="{ active: String(c.categoryId) === String(categoryId) }"
          @click="switchCategory(c.categoryId)"
        >{{ c.categoryName }}</button>
      </div>
    </div>

    <!-- 排序 -->
    <div class="sort-tabs">
      <button
        v-for="s in sorts"
        :key="s.key"
        type="button"
        class="sort-tab"
        :class="{ active: activeSort === s.key }"
        @click="switchSort(s.key)"
      >{{ s.label }}</button>
    </div>

    <!-- 视频流 -->
    <div v-if="loading" class="video-grid">
      <div v-for="n in 12" :key="n" class="skeleton-card">
        <div class="skeleton-cover" />
        <div class="skeleton-title" />
        <div class="skeleton-line" />
      </div>
    </div>
    <div v-else class="video-grid">
      <VideoCard v-for="v in videos" :key="v.videoId" :video="v" @play="openVideo" />
    </div>
    <div class="empty-tip" v-if="!loading && !videos.length">该分区还没有视频，去别的分区逛逛吧～</div>
    <div class="load-more" v-if="videos.length >= 15">
      <button class="load-more-btn" :disabled="loadingMore" @click="loadMore">
        {{ loadingMore ? '加载中...' : '加载更多' }}
      </button>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadVideoApi } from '@/api/modules/video'
import { loadAllCategoryApi } from '@/api/modules/category'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()

const sorts = [
  { key: 'create_time desc', label: '最新发布' },
  { key: 'play_count desc', label: '最多播放' },
  { key: 'danmu_count desc', label: '最多弹幕' }
]

const categoryId = computed(() => route.params.categoryId || '1')
const activeSort = ref('create_time desc')
const categories = ref([])
const videos = ref([])
const totalCount = ref(0)
const loading = ref(false)
const loadingMore = ref(false)
let pageNo = 1
let abortController = null

const defaultNames = {
  1: '动画', 2: '音乐', 3: '游戏', 4: '知识', 5: '科技',
  6: '运动', 7: '生活', 8: '电影', 9: '电视剧', 10: '纪录片'
}
const categoryName = computed(() => {
  const cat = categories.value.find((c) => String(c.categoryId) === String(categoryId.value))
  return cat?.categoryName || defaultNames[categoryId.value] || '分区'
})
const iconText = computed(() => categoryName.value.slice(0, 1))

function abortRequests() {
  if (abortController) abortController.abort()
  abortController = new AbortController()
}

async function loadCategories() {
  try {
    const data = await loadAllCategoryApi()
    const list = Array.isArray(data) ? data : data?.list || data?.data || []
    categories.value = Array.isArray(list) ? list : []
  } catch {
    categories.value = []
  }
}

async function loadVideos() {
  const signal = abortController.signal
  loading.value = true
  pageNo = 1
  try {
    const data = await loadVideoApi({
      pCategoryId: categoryId.value,
      categoryId: '0',
      pageNo: 1,
      orderBy: activeSort.value
    }, signal)
    if (signal.aborted) return
    videos.value = normalizeVideoList(data) || []
    totalCount.value = Number(data?.totalCount || 0)
  } catch (e) {
    if (signal.aborted) return
    videos.value = []
    totalCount.value = 0
  } finally {
    if (!signal.aborted) loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value) return
  loadingMore.value = true
  pageNo++
  const signal = abortController.signal
  try {
    const data = await loadVideoApi({
      pCategoryId: categoryId.value,
      categoryId: '0',
      pageNo,
      orderBy: activeSort.value
    }, signal)
    if (signal.aborted) return
    const list = normalizeVideoList(data) || []
    videos.value = [...videos.value, ...list]
    totalCount.value = Number(data?.totalCount || totalCount.value)
  } catch (e) {
    if (signal.aborted) return
  } finally {
    if (!signal.aborted) loadingMore.value = false
  }
}

function switchCategory(id) {
  router.push({ name: 'category', params: { categoryId: id } })
}

function switchSort(key) {
  if (activeSort.value === key) return
  activeSort.value = key
  abortRequests()
  loadVideos()
}

function openVideo(v) {
  playerStore.play(v)
  router.push({ name: 'video-detail', params: { videoId: v.videoId } })
}

watch(
  categoryId,
  () => {
    abortRequests()
    loadVideos()
  },
  { immediate: true }
)

loadCategories()
</script>

<style scoped>
.category-view { min-width: 0; }

.cat-header {
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  padding: 20px 24px 14px;
  box-shadow: var(--bil-shadow);
  margin-bottom: 16px;
}
.cat-title-row {
  display: flex;
  align-items: center;
  gap: 14px;
}
.cat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: var(--bil-radius-lg);
  background: var(--bil-gradient);
  color: #fff;
  font-size: 24px;
  font-weight: 700;
  flex-shrink: 0;
}
.cat-title-info { min-width: 0; }
.cat-name {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}
.cat-desc {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--bil-muted);
}
.cat-sub-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}
.cat-sub-item {
  padding: 4px 14px;
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.cat-sub-item:hover { border-color: var(--bil-pink); color: var(--bil-pink); }
.cat-sub-item.active {
  background: var(--bil-pink);
  border-color: var(--bil-pink);
  color: #fff;
}

.sort-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--bil-border);
}
.sort-tab {
  position: relative;
  padding: 10px 18px;
  border: none;
  background: transparent;
  color: var(--bil-muted);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}
.sort-tab:hover { color: var(--bil-text); }
.sort-tab.active { color: var(--bil-pink); font-weight: 600; }
.sort-tab.active::after {
  content: "";
  position: absolute;
  left: 14px;
  right: 14px;
  bottom: 0;
  height: 2px;
  border-radius: 1px;
  background: var(--bil-pink);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px 16px;
}
.empty-tip {
  padding: 48px 0;
  text-align: center;
  color: var(--bil-muted);
}
.load-more { text-align: center; margin-top: 24px; }
.load-more-btn {
  height: 36px;
  padding: 0 32px;
  border: 1px solid var(--bil-border);
  border-radius: var(--bil-radius);
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 13px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}
.load-more-btn:hover { border-color: var(--bil-pink); color: var(--bil-pink); }
.load-more-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.skeleton-card { border-radius: var(--bil-radius-lg); }
.skeleton-cover {
  aspect-ratio: 16 / 9;
  border-radius: var(--bil-radius-lg);
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}
.skeleton-title {
  height: 15px;
  border-radius: 4px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin: 10px 0 8px;
  width: 85%;
}
.skeleton-line {
  height: 12px;
  border-radius: 4px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  width: 55%;
}
@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
