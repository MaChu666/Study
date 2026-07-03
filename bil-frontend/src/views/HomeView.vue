<template>
  <section class="home-view">
    <!-- Banner Carousel -->
    <div class="banner-section" v-if="banners.length">
      <el-carousel :interval="5000" arrow="hover" indicator-position="none" height="220px" @change="setBanner">
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

    <!-- Category Tabs -->
    <div class="category-tabs">
      <button v-for="c in categories" :key="c.categoryId || c.id" class="cat-tab" :class="{ active: activeCat === (c.categoryId || c.id) }" @click="switchCategory(c)">
        {{ c.categoryName || c.name }}
      </button>
    </div>

    <!-- Hot Ranking + Main Content -->
    <div class="content-layout">
      <div class="main-feed">
        <section class="feed-section">
          <div class="section-title">
            <h2>{{ activeCat === 0 ? '热门推荐' : (selectedCatName) }}</h2>
            <span class="section-sub">加权推荐 · 为你精选</span>
          </div>
          <div v-if="loading" class="video-grid">
            <div v-for="n in 8" :key="n" class="skeleton-card">
              <div class="skeleton-cover" />
              <div class="skeleton-title" />
              <div class="skeleton-line" />
              <div class="skeleton-line short" />
            </div>
          </div>
          <div v-else class="video-grid">
            <VideoCard v-for="v in videos" :key="v.videoId" :video="v" @play="openVideo" />
          </div>
          <div class="load-more" v-if="videos.length >= 15">
            <el-button text @click="loadMore">加载更多</el-button>
          </div>
        </section>

        <section class="feed-section" v-if="hotVideos.length">
          <div class="section-title">
            <h2>排行榜</h2>
            <span class="section-sub">大家都在看</span>
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
        </section>
      </div>

      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="side-card" v-if="hotTags.length">
          <h3>热门标签</h3>
          <div class="tag-cloud">
            <span v-for="t in hotTags" :key="t" class="tag-chip" @click="searchTag(t)">{{ t }}</span>
          </div>
        </div>
        <div class="side-card">
          <h3>公告</h3>
          <p class="notice-text">欢迎来到 VidVault！这里是一个仿 B 站的 UGC 视频平台，你可以在这里观看、上传、分享视频。</p>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadRecommendVideoApi, loadHotVideoListApi, loadVideoApi, getSearchKeywordTopApi } from '@/api/modules/video'
import { loadAllCategoryApi } from '@/api/modules/category'
import { loadActiveBannersApi } from '@/api/modules/user'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const loading = ref(false)
const banners = ref([])
const currentBanner = ref(0)
const categories = ref([
  { categoryId: 0, categoryName: '推荐' },
  { categoryId: 1, categoryName: '动画' },
  { categoryId: 2, categoryName: '音乐' },
  { categoryId: 3, categoryName: '游戏' },
  { categoryId: 4, categoryName: '知识' },
  { categoryId: 5, categoryName: '科技' },
  { categoryId: 6, categoryName: '运动' },
  { categoryId: 7, categoryName: '生活' },
  { categoryId: 8, categoryName: '电影' },
  { categoryId: 9, categoryName: '电视剧' },
  { categoryId: 10, categoryName: '纪录片' }
])
const activeCat = ref(0)
const selectedCatName = ref('推荐')
const videos = ref([])
const hotVideos = ref([])
const hotTags = ref([])
let pageNo = 1

function formatCount(n) {
  if (!n) return '0'
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return String(n)
}

async function loadBanners() {
  try {
    const data = await loadActiveBannersApi()
    if (Array.isArray(data)) banners.value = data
  } catch {}
}

async function loadCategories() {
  try {
    const data = await loadAllCategoryApi()
    if (Array.isArray(data) && data.length) {
      categories.value = [{ categoryId: 0, categoryName: '推荐' }, ...data]
    }
  } catch {}
}

async function loadVideos() {
  loading.value = true
  pageNo = 1
  try {
    const data = activeCat.value === 0
      ? await loadRecommendVideoApi()
      : await loadVideoApi({ pCategoryId: activeCat.value, categoryId: '0', pageNo: 1 })
    const list = normalizeVideoList(data)
    videos.value = Array.isArray(list) ? list : []
  } catch {
    videos.value = []
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  pageNo++
  try {
    const data = activeCat.value === 0
      ? await loadRecommendVideoApi()
      : await loadVideoApi({ pCategoryId: activeCat.value, categoryId: '0', pageNo })
    const list = normalizeVideoList(data) || []
    videos.value = [...videos.value, ...list]
  } catch {}
}

async function loadHot() {
  try {
    const data = await loadHotVideoListApi()
    hotVideos.value = normalizeVideoList(data) || []
  } catch {}
}

async function loadTags() {
  try {
    const data = await getSearchKeywordTopApi()
    hotTags.value = Array.isArray(data) ? data.slice(0, 15) : []
  } catch {}
}

function switchCategory(c) {
  activeCat.value = c.categoryId || c.id
  selectedCatName.value = c.categoryName || c.name
  loadVideos()
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

// Sync active category from route query (e.g. sidebar nav clicks)
watch(
  () => route.query.pCategoryId,
  (newVal) => {
    const catId = Number(newVal) || 0
    if (activeCat.value !== catId) {
      const cat = categories.value.find((c) => (c.categoryId || c.id) === catId)
      if (cat) {
        activeCat.value = catId
        selectedCatName.value = cat.categoryName || cat.name
        loadVideos()
      }
    }
  },
  { immediate: true }
)

watch(
  () => route.name,
  (name) => {
    if (name === 'home' && !route.query.pCategoryId) {
      activeCat.value = 0
      selectedCatName.value = '推荐'
      loadVideos()
    }
  }
)

onMounted(() => {
  loadBanners()
  loadCategories()
  loadVideos()
  loadHot()
  loadTags()
})
</script>

<style scoped>
.home-view { min-width: 0; }

.banner-section {
  position: relative;
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
}
.banner-item {
  width: 100%;
  height: 220px;
  background-size: cover;
  background-position: center;
  cursor: pointer;
  display: flex;
  align-items: flex-end;
  padding: 20px;
}
.banner-title {
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0,0,0,0.6);
}
.carousel-dots {
  position: absolute;
  bottom: 10px;
  right: 20px;
  display: flex;
  gap: 6px;
}
.dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.2s;
}
.dot.active { background: #fff; width: 20px; border-radius: 4px; }

.category-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 4px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}
.category-tabs::-webkit-scrollbar {
  display: none;
}
.cat-tab {
  padding: 6px 16px;
  border: none;
  background: var(--bil-surface);
  color: var(--bil-text);
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}
.cat-tab:hover { background: var(--bil-hover); }
.cat-tab.active { background: var(--bil-primary); color: #fff; }

.content-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 300px;
  gap: 24px;
}

.feed-section { margin-bottom: 32px; }
.section-title {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 16px;
}
.section-title h2 { margin: 0; font-size: 20px; }
.section-sub { color: var(--bil-muted); font-size: 13px; }

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px 16px;
}
.load-more { text-align: center; margin-top: 20px; }

.rank-list { background: var(--bil-surface); border-radius: 10px; padding: 12px; }
.rank-item {
  display: flex; align-items: center; gap: 12px;
  padding: 8px 6px; cursor: pointer; border-radius: 6px; transition: background 0.15s;
}
.rank-item:hover { background: var(--bil-hover); }
.rank-num { width: 24px; text-align: center; font-weight: 700; color: var(--bil-muted); font-size: 15px; }
.rank-num.top3 { color: var(--bil-pink); }
.rank-info { min-width: 0; }
.rank-name { display: block; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.rank-stat { font-size: 11px; color: var(--bil-muted); }

.sidebar { display: flex; flex-direction: column; gap: 16px; }
.side-card { background: var(--bil-surface); border-radius: 10px; padding: 16px; }
.side-card h3 { margin: 0 0 10px; font-size: 15px; }
.tag-cloud { display: flex; flex-wrap: wrap; gap: 6px; }
.tag-chip {
  padding: 3px 10px; background: var(--bil-hover); border-radius: 4px;
  font-size: 12px; cursor: pointer; color: var(--bil-text); transition: background 0.15s;
}
.tag-chip:hover { background: var(--bil-primary); color: #fff; }
.notice-text { font-size: 13px; color: var(--bil-muted); line-height: 1.6; margin: 0; }

/* Skeleton cards */
.skeleton-card {
  border-radius: 12px;
}
.skeleton-cover {
  aspect-ratio: 16 / 9;
  border-radius: 12px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}
.skeleton-title {
  height: 16px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin: 10px 0 6px;
  width: 85%;
}
.skeleton-line {
  height: 12px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-bottom: 6px;
  width: 60%;
}
.skeleton-line.short {
  width: 40%;
}
@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 900px) {
  .content-layout { grid-template-columns: 1fr; }
  .sidebar { display: none; }
}
</style>
