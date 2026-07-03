<template>
  <section class="search-view">
    <!-- Search bar with glass-morphism -->
    <div class="search-bar-wrap">
      <div class="search-bar" :class="{ focused: searchFocused }">
        <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <path d="m21 21-4.35-4.35" />
        </svg>
        <input
          ref="searchInputRef"
          v-model="keyword"
          class="search-input"
          type="text"
          placeholder="搜索视频、UP主或话题"
          autocomplete="off"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
          @input="onKeywordInput"
          @keyup.enter="runSearch"
        />
        <button v-if="keyword" class="clear-btn" type="button" @mousedown.prevent="clearSearch">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
          </svg>
        </button>
        <button class="search-btn" type="button" @click="runSearch">搜索</button>
      </div>

      <!-- Suggestions / History dropdown -->
      <div v-if="showDropdown" class="search-dropdown">
        <!-- History: input focused but empty -->
        <template v-if="!keyword && searchHistory.length">
          <div class="dropdown-header">
            <span class="dropdown-title">搜索历史</span>
            <button class="clear-history-btn" type="button" @mousedown.prevent="clearHistory">清除历史</button>
          </div>
          <button
            v-for="item in searchHistory"
            :key="item"
            class="dropdown-item"
            type="button"
            @mousedown.prevent="useHistoryItem(item)"
          >
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            <span>{{ item }}</span>
          </button>
        </template>

        <!-- Suggestions: user is typing -->
        <template v-if="keyword && filteredSuggestions.length">
          <div class="dropdown-header">
            <span class="dropdown-title">搜索建议</span>
          </div>
          <button
            v-for="item in filteredSuggestions"
            :key="item"
            class="dropdown-item"
            type="button"
            @mousedown.prevent="useSuggestion(item)"
          >
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.35-4.35" />
            </svg>
            <span>{{ item }}</span>
          </button>
        </template>
      </div>
    </div>

    <!-- Filter tabs -->
    <div class="filter-tabs">
      <button
        v-for="tab in filterTabs"
        :key="tab.key"
        class="filter-tab"
        :class="{ active: activeFilter === tab.key }"
        :disabled="loading"
        type="button"
        @click="switchFilter(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Hot words (when no keyword) -->
    <div v-if="!keyword && !hasActiveSearch" class="hotwords">
      <span class="hotwords-label">大家都在搜</span>
      <div class="hotwords-list">
        <button
          v-for="word in hotwords"
          :key="word"
          class="hotword-chip"
          type="button"
          @click="useWord(word)"
        >
          {{ word }}
        </button>
      </div>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="video-grid">
      <div v-for="n in 8" :key="n" class="result-card skeleton-card">
        <div class="cover-wrap skeleton-box" />
        <div class="card-info">
          <div class="skeleton-line skeleton-line-title" />
          <div class="skeleton-line skeleton-line-sub" />
          <div class="skeleton-line skeleton-line-sub" style="width: 60%" />
        </div>
      </div>
    </div>

    <!-- User results -->
    <div v-if="!loading && userResults.length && hasActiveSearch" class="user-results">
      <h3 class="section-title">相关用户</h3>
      <div class="user-list">
        <div
          v-for="user in userResults.slice(0, 6)"
          :key="user.userId"
          class="user-card"
          @click="openUserProfile(user)"
        >
          <div class="user-avatar">
            <img v-if="user.avatar" :src="user.avatar" :alt="user.useName" class="avatar-img" />
            <span v-else class="avatar-placeholder">{{ (user.useName || '?')[0] }}</span>
          </div>
          <div class="user-info">
            <span class="user-name">{{ user.useName }}</span>
            <span class="user-bio">{{ user.personProfile || '这个人很懒，什么都没写~' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Results -->
    <div v-else-if="sortedResults.length" class="video-grid">
      <article
        v-for="video in sortedResults"
        :key="video.videoId"
        class="result-card"
        @click="openVideo(video)"
      >
        <div class="cover-wrap">
          <img class="cover" :src="video.videoCover" :alt="video.videoName" loading="lazy" />
          <span class="duration-badge">{{ formatDuration(video.duration) }}</span>
        </div>
        <div class="card-info">
          <h3 class="card-title">{{ video.videoName }}</h3>
          <UserBadge
            :user-id="video.userId"
            :user-name="video.userName || video.useName"
            :avatar="video.userAvatar"
            size="sm"
          />
          <div class="card-meta">
            <span>{{ formatCount(video.playCount) }} 播放</span>
            <span class="meta-divider">·</span>
            <span>{{ formatCount(video.danmuCount) }} 弹幕</span>
            <template v-if="video.createTime">
              <span class="meta-divider">·</span>
              <span>{{ video.createTime }}</span>
            </template>
          </div>
        </div>
      </article>
    </div>

    <!-- Empty state -->
    <div v-else-if="hasActiveSearch && !loading" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 120 120" width="120" height="120" fill="none">
          <circle cx="60" cy="60" r="50" stroke="var(--bil-border)" stroke-width="2" />
          <circle cx="45" cy="42" r="6" fill="var(--bil-muted)" />
          <circle cx="75" cy="42" r="6" fill="var(--bil-muted)" />
          <path d="M40 70 Q60 85 80 70" stroke="var(--bil-muted)" stroke-width="2" fill="none" stroke-linecap="round" />
        </svg>
      </div>
      <h3 class="empty-title">没有找到相关内容</h3>
      <p class="empty-hint">换个关键词试试，或看看大家都在搜：</p>
      <div class="suggested-keywords">
        <button
          v-for="word in hotwords"
          :key="word"
          class="suggestion-chip"
          type="button"
          @click="useWord(word)"
        >
          {{ word }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSearchKeywordTopApi, searchVideoApi } from '@/api/modules/video'
import { searchUsersApi } from '@/api/modules/user'
import UserBadge from '@/components/user/UserBadge.vue'
import { usePlayerStore } from '@/stores/player'
import { normalizeVideoList } from '@/utils/videoList'

const HISTORY_KEY = 'bil-search-history'
const MAX_HISTORY = 10

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()

const searchInputRef = ref(null)
const keyword = ref(String(route.query.keyword || ''))
const searchFocused = ref(false)
const showDropdown = ref(false)
const loading = ref(false)
const hasActiveSearch = ref(false)

const hotwords = ref(['游戏', '弹幕', '前端', '美食', '二次元', '音乐'])
const allSuggestions = ref([])
const searchHistory = ref(loadHistory())
const results = ref([])
const userResults = ref([])
const activeFilter = ref('default')

const filterTabs = [
  { key: 'default', label: '综合' },
  { key: 'play_count', label: '最多播放' },
  { key: 'create_time', label: '最新发布' },
  { key: 'danmu_count', label: '最多弹幕' }
]

// --- Computed ---

const filteredSuggestions = computed(() => {
  if (!keyword.value) return []
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return []
  return allSuggestions.value.filter(function (s) {
    return String(s).toLowerCase().includes(kw)
  }).slice(0, 8)
})

const sortedResults = computed(() => {
  if (activeFilter.value === 'default') return results.value
  var list = results.value.slice()
  if (activeFilter.value === 'play_count') {
    list.sort(function (a, b) { return (Number(b.playCount) || 0) - (Number(a.playCount) || 0) })
  } else if (activeFilter.value === 'create_time') {
    list.sort(function (a, b) {
      return String(b.createTime || '').localeCompare(String(a.createTime || ''))
    })
  } else if (activeFilter.value === 'danmu_count') {
    list.sort(function (a, b) { return (Number(b.danmuCount) || 0) - (Number(a.danmuCount) || 0) })
  }
  return list
})

// --- History ---

function loadHistory() {
  try {
    var raw = localStorage.getItem(HISTORY_KEY)
    return raw ? JSON.parse(raw) : []
  } catch (_e) {
    return []
  }
}

function saveHistory() {
  try {
    localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
  } catch (_e) {
    // localStorage unavailable
  }
}

function addToHistory(kw) {
  if (!kw) return
  var list = searchHistory.value.filter(function (item) {
    return item !== kw
  })
  list.unshift(kw)
  searchHistory.value = list.slice(0, MAX_HISTORY)
  saveHistory()
}

function clearHistory() {
  searchHistory.value = []
  saveHistory()
}

function useHistoryItem(item) {
  keyword.value = item
  showDropdown.value = false
  runSearch()
}

function useSuggestion(item) {
  keyword.value = item
  showDropdown.value = false
  runSearch()
}

// --- Dropdown ---

function onSearchFocus() {
  searchFocused.value = true
  if (!keyword.value && searchHistory.value.length) {
    showDropdown.value = true
  } else if (keyword.value && filteredSuggestions.value.length) {
    showDropdown.value = true
  }
}

function onSearchBlur() {
  // Delay to allow mousedown on dropdown item to fire first
  setTimeout(function () {
    searchFocused.value = false
    showDropdown.value = false
  }, 200)
}

function onKeywordInput() {
  if (keyword.value && filteredSuggestions.value.length) {
    showDropdown.value = true
  } else if (!keyword.value && searchHistory.value.length) {
    showDropdown.value = true
  } else {
    showDropdown.value = false
  }
}

function clearSearch() {
  keyword.value = ''
  userResults.value = []
  showDropdown.value = false
  searchInputRef.value && searchInputRef.value.focus()
}

// --- Search ---

async function fetchResults(nextKeyword) {
  if (!nextKeyword) return
  loading.value = true
  try {
    const [videoData, userData] = await Promise.all([
      searchVideoApi({ keyword: nextKeyword }),
      searchUsersApi({ keyword: nextKeyword })
    ])
    results.value = normalizeVideoList(videoData)
    userResults.value = Array.isArray(userData) ? userData : (userData && userData.data ? userData.data : [])
  } catch (_e) {
    results.value = []
    userResults.value = []
  } finally {
    loading.value = false
  }
}

async function runSearch() {
  var nextKeyword = String(keyword.value || '').trim()
  keyword.value = nextKeyword
  showDropdown.value = false
  if (!nextKeyword) {
    results.value = []
    userResults.value = []
    hasActiveSearch.value = false
    return
  }
  hasActiveSearch.value = true
  addToHistory(nextKeyword)
  activeFilter.value = 'default'
  if (nextKeyword !== String(route.query.keyword || '')) {
    await router.replace({
      name: 'search',
      query: { keyword: nextKeyword }
    })
    // watch will trigger fetchResults
  } else {
    await fetchResults(nextKeyword)
  }
}

function switchFilter(key) {
  activeFilter.value = key
}

function useWord(word) {
  keyword.value = word
  return runSearch()
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

function openUserProfile(user) {
  router.push({ name: 'user-home', params: { userId: user.userId } })
}

// --- Suggestions ---

async function loadSuggestions() {
  try {
    var data = await getSearchKeywordTopApi()
    allSuggestions.value = Array.isArray(data) && data.length ? data : allSuggestions.value
  } catch (_e) {
    // keep fallback
  }
}

// --- Helpers ---

function formatDuration(seconds) {
  var s = Number(seconds) || 0
  if (s < 60) return '00:' + String(s).padStart(2, '0')
  var m = Math.floor(s / 60)
  var sec = s % 60
  if (m < 60) return String(m).padStart(2, '0') + ':' + String(sec).padStart(2, '0')
  var h = Math.floor(m / 60)
  var min = m % 60
  return String(h) + ':' + String(min).padStart(2, '0') + ':' + String(sec).padStart(2, '0')
}

function formatCount(value) {
  var count = Number(value || 0)
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return String(count)
}

// --- Lifecycle ---

onMounted(function () {
  loadSuggestions()
  // Auto-focus search input on page load
  nextTick(function () {
    if (searchInputRef.value) {
      searchInputRef.value.focus()
    }
  })
})

watch(
  function () { return route.query.keyword },
  async function (value) {
    var nextKeyword = String(value || '')
    if (nextKeyword !== keyword.value) {
      keyword.value = nextKeyword
    }
    if (nextKeyword) {
      hasActiveSearch.value = true
      activeFilter.value = 'default'
      await fetchResults(nextKeyword)
    } else {
      hasActiveSearch.value = false
      results.value = []
      userResults.value = []
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.search-view {
  min-width: 0;
}

/* ---- Search bar ---- */
.search-bar-wrap {
  position: relative;
  margin-bottom: 16px;
}

.search-bar {
  display: flex;
  align-items: center;
  height: 52px;
  padding: 0 8px 0 18px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--bil-border);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: border-color 0.2s, box-shadow 0.2s, background 0.3s;
}

[data-theme="dark"] .search-bar {
  background: rgba(31, 31, 51, 0.8);
}

.search-bar.focused {
  border-color: var(--bil-primary);
  box-shadow: 0 0 0 3px rgba(0, 161, 214, 0.12);
}

.search-icon {
  flex-shrink: 0;
  color: var(--bil-muted);
  margin-right: 10px;
}

.search-input {
  flex: 1;
  min-width: 0;
  height: 100%;
  border: 0;
  outline: 0;
  background: transparent;
  color: var(--bil-text);
  font-size: 15px;
  line-height: 1.4;
}

.search-input::placeholder {
  color: var(--bil-muted);
}

.clear-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: var(--bil-muted);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.clear-btn:hover {
  background: var(--bil-hover);
  color: var(--bil-text);
}

.search-btn {
  flex-shrink: 0;
  height: 36px;
  padding: 0 22px;
  margin-left: 4px;
  border: 0;
  border-radius: 10px;
  background: var(--bil-primary);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: var(--bil-primary-hover);
}

/* ---- Dropdown ---- */
.search-dropdown {
  position: absolute;
  inset: 56px 0 auto;
  z-index: 15;
  max-height: 340px;
  overflow-y: auto;
  background: var(--bil-surface);
  border: 1px solid var(--bil-border);
  border-radius: 12px;
  box-shadow: var(--bil-shadow);
  padding: 8px;
}

.dropdown-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px 4px;
}

.dropdown-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--bil-muted);
}

.clear-history-btn {
  border: 0;
  background: transparent;
  color: var(--bil-primary);
  font-size: 12px;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background 0.2s;
}

.clear-history-btn:hover {
  background: var(--bil-hover);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--bil-text);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s;
}

.dropdown-item:hover {
  background: var(--bil-hover);
}

.dropdown-item svg {
  flex-shrink: 0;
  color: var(--bil-muted);
}

/* ---- Filter tabs ---- */
.filter-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  padding-bottom: 2px;
  overflow-x: auto;
}

.filter-tab {
  flex-shrink: 0;
  padding: 8px 18px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--bil-muted);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.filter-tab:hover {
  background: var(--bil-hover);
  color: var(--bil-text);
}

.filter-tab.active {
  background: var(--bil-surface);
  color: var(--bil-primary);
  font-weight: 600;
  box-shadow: var(--bil-shadow);
}

.filter-tab:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ---- Hot words ---- */
.hotwords {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
}

.hotwords-label {
  color: var(--bil-muted);
  font-size: 13px;
  white-space: nowrap;
}

.hotwords-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hotword-chip {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 7px 14px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
  line-height: 1.2;
  font-size: 13px;
  transition: border-color 0.2s, color 0.2s;
}

.hotword-chip:hover {
  border-color: var(--bil-primary);
  color: var(--bil-primary);
}

/* ---- User results ---- */
.user-results {
  margin-bottom: 28px;
}

.section-title {
  margin: 0 0 14px;
  font-size: 16px;
  font-weight: 600;
  color: var(--bil-text);
}

.user-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  background: var(--bil-surface);
  border: 1px solid var(--bil-border);
  cursor: pointer;
  transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
  min-width: 240px;
  flex: 1;
}

.user-card:hover {
  background: var(--bil-hover);
  box-shadow: var(--bil-shadow);
  transform: translateY(-2px);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bil-pink);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--bil-text);
}

.user-bio {
  font-size: 12px;
  color: var(--bil-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

/* ---- Loading ---- */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 20px;
  color: var(--bil-muted);
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--bil-border);
  border-top-color: var(--bil-primary);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  margin: 0;
  font-size: 14px;
}

/* ---- Result cards ---- */
.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px 18px;
}

.result-card {
  min-width: 0;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.result-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--bil-shadow);
}

.result-card:hover .cover {
  transform: scale(1.03);
}

.cover-wrap {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 10px;
  background: var(--bil-border);
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.duration-badge {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 11px;
  font-weight: 500;
  line-height: 1.3;
  letter-spacing: 0.3px;
}

.card-info {
  padding: 10px 2px 0;
}

.card-title {
  margin: 0 0 6px;
  font-size: 14px;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
  color: var(--bil-muted);
  font-size: 12px;
  line-height: 1.3;
}

.meta-divider {
  margin: 0 2px;
}

/* ---- Skeleton loading ---- */
.skeleton-card {
  pointer-events: none;
}

.skeleton-box {
  background: var(--bil-border);
  border-radius: 10px;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-line {
  height: 14px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-bottom: 8px;
}

.skeleton-line-title {
  width: 90%;
  height: 16px;
}

.skeleton-line-sub {
  width: 100%;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ---- Empty state ---- */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 20px 64px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 16px;
  color: var(--bil-muted);
}

.empty-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--bil-text);
}

.empty-hint {
  margin: 0 0 18px;
  font-size: 14px;
  color: var(--bil-muted);
}

.suggested-keywords {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.suggestion-chip {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 8px 18px;
  background: var(--bil-surface);
  color: var(--bil-primary);
  cursor: pointer;
  font-size: 13px;
  transition: background 0.2s, border-color 0.2s;
}

.suggestion-chip:hover {
  background: var(--bil-primary);
  color: #fff;
  border-color: var(--bil-primary);
}

/* ---- Responsive ---- */
@media (max-width: 760px) {
  .search-bar {
    height: 44px;
    padding: 0 6px 0 14px;
    border-radius: 12px;
  }

  .search-btn {
    height: 32px;
    padding: 0 16px;
    font-size: 13px;
    border-radius: 8px;
  }

  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 18px 12px;
  }

  .filter-tab {
    padding: 6px 14px;
    font-size: 13px;
  }

  .card-title {
    font-size: 13px;
  }
}
</style>
