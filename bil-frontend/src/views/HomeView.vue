<template>
  <section class="home-view">
    <div class="home-layout">
      <!-- 左侧分类导航（B站风格） -->
      <SideCategoryNav class="home-side" />

      <div class="home-main">
        <!-- 顶部：轮播 + 排行榜 -->
        <div class="hero-row">
          <div class="banner-section" v-if="banners.length">
            <el-carousel :interval="5000" arrow="hover" indicator-position="none" height="260px" @change="setBanner">
              <el-carousel-item v-for="b in banners" :key="b.bannerId">
                <div class="banner-item" :style="{ backgroundImage: `url(${b.imageUrl})` }" @click="onBannerClick(b)">
                  <div class="banner-title" v-if="b.title">{{ b.title }}</div>
                </div>
              </el-carousel-item>
            </el-carousel>
            <div class="carousel-dots">
              <span v-for="(b, i) in banners" :key="i" class="dot" :class="{ active: i === currentBanner }" @click="setBanner(i)" />
            </div>
          </div>
          <div class="banner-section banner-placeholder" v-else>
            <div class="placeholder-cover" />
          </div>

          <aside class="rank-card" v-if="hotVideos.length">
            <div class="rank-head">
              <h3>排行榜</h3>
              <span class="rank-sub">大家都在看</span>
            </div>
            <div class="rank-list">
              <div v-for="(v, idx) in hotVideos.slice(0, 10)" :key="v.videoId" class="rank-item" @click="openVideo(v)">
                <span class="rank-num" :class="{ top3: idx < 3 }">{{ idx + 1 }}</span>
                <div class="rank-info">
                  <span class="rank-name">{{ v.videoName }}</span>
                  <span class="rank-stat">{{ formatCount(v.playCount) }} 播放</span>
                </div>
              </div>
            </div>
          </aside>
        </div>

        <!-- 视频流 -->
        <div class="feed-head">
          <h2 class="feed-title">{{ feedTitle }}</h2>
          <span class="feed-sub">{{ activeCat === 0 ? '为你精选 · 加权推荐' : '分区视频' }}</span>
        </div>

        <div v-if="loading" class="video-grid">
          <div v-for="n in 10" :key="n" class="skeleton-card">
            <div class="skeleton-cover" />
            <div class="skeleton-title" />
            <div class="skeleton-line" />
          </div>
        </div>
        <div v-else class="video-grid">
          <VideoCard v-for="v in videos" :key="v.videoId" :video="v" @play="openVideo" />
        </div>
        <div class="empty-tip" v-if="!loading && !videos.length">暂无视频，换个分区看看吧～</div>
        <div class="load-more" v-if="videos.length >= 15">
          <button class="load-more-btn" :disabled="loadingMore" @click="loadMore">
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>

        <!-- 热门标签 -->
        <div class="tag-strip" v-if="hotTags.length">
          <span class="tag-strip-label">热门标签</span>
          <span v-for="t in hotTags" :key="t" class="tag-chip" @click="searchTag(t)">{{ t }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadRecommendVideoApi, loadHotVideoListApi, loadVideoApi, getSearchKeywordTopApi } from '@/api/modules/video'
import { loadActiveBannersApi } from '@/api/modules/user'
import SideCategoryNav from '@/components/layout/SideCategoryNav.vue'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const loading = ref(false)
const loadingMore = ref(false)
const banners = ref([])
const currentBanner = ref(0)
const activeCat = ref(0)
const videos = ref([])
const hotVideos = ref([])
const hotTags = ref([])
let pageNo = 1

const categoryNames = {
  0: '推荐',
  1: '动画',
  2: '音乐',
  3: '游戏',
  4: '知识',
  5: '科技',
  6: '运动',
  7: '生活',
  8: '电影',
  9: '电视剧',
  10: '纪录片'
}

const feedTitle = computed(() => (activeCat.value === 0 ? '热门推荐' : `${categoryNames[activeCat.value] || '精选'}区精选`))

function formatCount(n) {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return String(n)
}

async function loadBanners(signal) {
  try {
    const data = await loadActiveBannersApi(signal)
    if (signal && signal.aborted) return
    banners.value = Array.isArray(data) ? data : []
  } catch (e) {
    if (signal && signal.aborted) return
    banners.value = []
  }
}

let pageAbortController = null
function abortPageRequests() {
  if (pageAbortController) pageAbortController.abort()
  pageAbortController = new AbortController()
}

async function loadVideos() {
  const signal = pageAbortController.signal
  loading.value = true
  pageNo = 1
  try {
    const data = activeCat.value === 0
      ? await loadRecommendVideoApi(signal)
      : await loadVideoApi({ pCategoryId: activeCat.value, categoryId: '0', pageNo: 1 }, signal)
    if (signal.aborted) return
    const list = normalizeVideoList(data)
    videos.value = Array.isArray(list) ? list : []
  } catch (e) {
    if (signal.aborted) return
    videos.value = []
  } finally {
    if (!signal.aborted) loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value) return
  loadingMore.value = true
  pageNo++
  const signal = pageAbortController.signal
  try {
    const data = activeCat.value === 0
      ? await loadRecommendVideoApi(signal)
      : await loadVideoApi({ pCategoryId: activeCat.value, categoryId: '0', pageNo }, signal)
    if (signal.aborted) return
    const list = normalizeVideoList(data) || []
    videos.value = [...videos.value, ...list]
  } catch (e) {
    if (signal.aborted) return
  } finally {
    if (!signal.aborted) loadingMore.value = false
  }
}

async function loadHot() {
  const signal = pageAbortController ? pageAbortController.signal : null
  try {
    const data = await loadHotVideoListApi(signal)
    if (signal && signal.aborted) return
    hotVideos.value = normalizeVideoList(data) || []
  } catch (e) {
    if (signal && signal.aborted) return
  }
}

async function loadTags() {
  const signal = pageAbortController ? pageAbortController.signal : null
  try {
    const data = await getSearchKeywordTopApi(signal)
    if (signal && signal.aborted) return
    hotTags.value = Array.isArray(data) ? data.slice(0, 15) : []
  } catch (e) {
    if (signal && signal.aborted) return
  }
}

function onBannerClick(b) {
  if (b.linkType === 1 && b.linkValue) {
    router.push({ name: 'video-detail', params: { videoId: b.linkValue } })
  } else if (b.linkValue) {
    window.open(b.linkValue, '_blank')
  }
}

function setBanner(i) { currentBanner.value = i }

function searchTag(t) {
  router.push({ name: 'search', query: { keyword: t } })
}

function openVideo(v) {
  playerStore.play(v)
  router.push({ name: 'video-detail', params: { videoId: v.videoId } })
}

let switchTimer = null
function applyCategory(catId) {
  abortPageRequests()
  activeCat.value = catId
  if (switchTimer) clearTimeout(switchTimer)
  switchTimer = setTimeout(() => {
    loadVideos()
  }, 150)
}

// Sync active category from route query (e.g. sidebar nav clicks)
watch(
  () => route.query.pCategoryId,
  (newVal) => {
    const catId = Number(newVal) || 0
    if (activeCat.value !== catId) {
      applyCategory(catId)
    }
  },
  { immediate: true }
)

watch(
  () => route.name,
  (name) => {
    if (name === 'home' && !route.query.pCategoryId && activeCat.value !== 0) {
      applyCategory(0)
    }
  }
)

onMounted(() => {
  pageAbortController = new AbortController()
  const signal = pageAbortController.signal
  loadBanners(signal)
  loadVideos()
  loadHot()
  loadTags()
})
</script>

<style scoped>
.home-view { min-width: 0; }

.home-layout {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}
.home-side {
  position: sticky;
  top: calc(var(--bil-header-h) + 16px);
}
.home-main { min-width: 0; }

/* Hero: banner + rank */
.hero-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 260px;
  gap: 16px;
  margin-bottom: 22px;
}
.banner-section {
  position: relative;
  border-radius: var(--bil-radius-lg);
  overflow: hidden;
  box-shadow: var(--bil-shadow);
}
.banner-item {
  width: 100%;
  height: 260px;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  display: flex;
  align-items: flex-end;
  padding: 20px;
}
.banner-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
}
.banner-placeholder { height: 260px; }
.placeholder-cover {
  height: 100%;
  background: linear-gradient(135deg, var(--bil-primary) 0%, var(--bil-pink) 100%);
  opacity: 0.35;
}
.carousel-dots {
  position: absolute;
  bottom: 12px;
  right: 16px;
  display: flex;
  gap: 6px;
  z-index: 2;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.2s;
}
.dot.active { background: #fff; width: 20px; border-radius: 4px; }

/* Rank card */
.rank-card {
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  padding: 14px 14px 6px;
  box-shadow: var(--bil-shadow);
  min-width: 0;
}
.rank-head {
  display: flex;
  align-items: baseline;
  gap: 8px;
  padding: 0 4px 10px;
}
.rank-head h3 { margin: 0; font-size: 16px; }
.rank-sub { font-size: 12px; color: var(--bil-muted); }
.rank-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 4px;
  cursor: pointer;
  border-radius: var(--bil-radius);
  transition: background 0.15s;
}
.rank-item:hover { background: var(--bil-hover); }
.rank-num {
  width: 22px;
  text-align: center;
  font-weight: 700;
  font-style: italic;
  color: var(--bil-muted);
  font-size: 14px;
  flex-shrink: 0;
}
.rank-num.top3 { color: var(--bil-pink); }
.rank-info { min-width: 0; }
.rank-name {
  display: block;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rank-stat { font-size: 11px; color: var(--bil-muted); }

/* Feed head */
.feed-head {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin: 4px 0 14px;
}
.feed-title { margin: 0; font-size: 20px; font-weight: 600; }
.feed-sub { color: var(--bil-muted); font-size: 12px; }

/* Video grid */
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

/* Hot tags */
.tag-strip {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 28px;
  padding: 14px 16px;
  background: var(--bil-surface);
  border-radius: var(--bil-radius-lg);
  box-shadow: var(--bil-shadow);
}
.tag-strip-label { font-size: 13px; color: var(--bil-muted); flex-shrink: 0; }
.tag-chip {
  padding: 3px 12px;
  background: var(--bil-hover);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  color: var(--bil-text);
  transition: background 0.15s, color 0.15s;
}
.tag-chip:hover { background: var(--bil-pink); color: #fff; }

/* Skeleton */
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

@media (max-width: 1080px) {
  .hero-row { grid-template-columns: minmax(0, 1fr) 240px; }
}
@media (max-width: 900px) {
  .home-layout { grid-template-columns: 1fr; }
  .home-side { display: none; }
  .hero-row { grid-template-columns: 1fr; }
  .rank-card { display: none; }
}
</style>
