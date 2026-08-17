<template>
  <aside class="side-nav" :class="{ 'mobile-hidden': !mobileVisible }">
    <!-- Static items at top -->
    <button
      type="button"
      class="nav-item"
      :class="{ active: isHomeActive }"
      @click="goHome"
    >
      <span class="nav-icon">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
          <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
          <polyline points="9 22 9 12 15 12 15 22" />
        </svg>
      </span>
      <span class="nav-label">首页</span>
    </button>

    <button
      type="button"
      class="nav-item"
      :class="{ active: isHotActive }"
      @click="goHot"
    >
      <span class="nav-icon">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8 0-1.2.29-2.32.78-3.34 1.07 1.96 2.54 4.16 5.12 4.78.76.17 1.46-.01 1.97-.53.33.97.92 1.5 1.8 2.1.38.26 1.2.79 1.73 1.63h-4.4c-.37 0-.68-.3-.68-.68s.3-.68.68-.68h3.4c.38 0 .68.3.68.68 0 .04 0 .08-.01.12z" />
        </svg>
      </span>
      <span class="nav-label">热门</span>
    </button>

    <!-- API-loaded categories -->
    <button
      v-for="item in categories"
      :key="item.categoryId"
      type="button"
      class="nav-item"
      :class="{ active: String(route.query.pCategoryId || '0') === String(item.categoryId) }"
      @click="selectCategory(item)"
    >
      <span class="nav-icon">
        <img v-if="item.icon && isImageUrl(item.icon)" :src="item.icon" alt="" class="icon-img" @error="onImgError" />
        <span v-else>{{ getCategoryIcon(item) }}</span>
      </span>
      <span class="nav-label">{{ item.categoryName }}</span>
    </button>
  </aside>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadAllCategoryApi } from '@/api/modules/category'

defineProps({
  mobileVisible: { type: Boolean, default: false }
})

const route = useRoute()
const router = useRouter()

const fallbackCategories = [
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
]

const categories = ref(fallbackCategories)

// Category icon map (simple text-based, rendered as first character)
const categoryIcons = {
  '动画': '动',
  '音乐': '音',
  '游戏': '游',
  '知识': '知',
  '科技': '科',
  '运动': '运',
  '生活': '生',
  '电影': '影',
  '电视剧': '剧',
  '纪录片': '纪'
}

const isHomeActive = computed(
  () => route.name === 'home' && !route.query.pCategoryId && !route.query.hot
)

const isHotActive = computed(() => !!route.query.hot)

function getCategoryIcon(item) {
  return categoryIcons[item.categoryName] || (item.categoryName || '?').charAt(0)
}

async function loadCategories() {
  try {
    const data = await loadAllCategoryApi()
    const list = Array.isArray(data) ? data : data?.list || data?.data || []
    if (list.length) {
      categories.value = list
      return
    }
    categories.value = fallbackCategories
  } catch {
    categories.value = fallbackCategories
  }
}

function goHome() {
  router.push({ name: 'home' })
}

function goHot() {
  router.push({ name: 'home', query: { hot: '1' } })
}

function isImageUrl(val) {
  return val && (val.startsWith('/') || val.startsWith('http'))
}

function onImgError(e) {
  e.target.style.display = 'none'
  e.target.nextElementSibling.style.display = ''
}

function selectCategory(item) {
  router.push({
    name: 'home',
    query: { pCategoryId: item.categoryId }
  })
}

onMounted(loadCategories)
</script>

<style scoped>
.side-nav {
  position: sticky;
  top: calc(var(--bil-header-h) + 16px);
  height: fit-content;
  display: flex;
  flex-direction: column;
  gap: 2px;
  z-index: 10;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 40px;
  padding: 0 12px;
  border: none;
  border-radius: var(--bil-radius);
  background: transparent;
  color: var(--bil-text);
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  background: color-mix(in srgb, var(--bil-pink) 10%, transparent);
  color: var(--bil-pink);
}

.nav-item.active {
  color: var(--bil-pink);
  background: color-mix(in srgb, var(--bil-pink) 14%, transparent);
  font-weight: 600;
}

.nav-icon {
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  line-height: 0;
}

.nav-label {
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Desktop: always visible. Mobile: hidden unless toggled */
@media (max-width: 760px) {
  .side-nav {
    position: fixed;
    top: 60px;
    left: 0;
    bottom: 0;
    width: 200px;
    padding: 12px;
    background: var(--bil-surface);
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
    overflow-y: auto;
    gap: 4px;
    z-index: 100;
    transition: transform 0.25s ease;
  }

  .side-nav.mobile-hidden {
    transform: translateX(-100%);
  }

  .nav-item {
    height: 38px;
    font-size: 13px;
    border-radius: 10px;
    padding: 0 12px;
  }
}
.icon-img {
  width: 20px;
  height: 20px;
  object-fit: cover;
  border-radius: 50%;
  flex-shrink: 0;
}
</style>
