<template>
  <aside class="side-nav">
    <button
      v-for="item in categories"
      :key="item.categoryId"
      type="button"
      class="nav-item"
      :class="{ active: String(route.query.pCategoryId || '0') === String(item.categoryId) }"
      @click="selectCategory(item)"
    >
      {{ item.categoryName }}
    </button>
  </aside>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadAllCategoryApi } from '@/api/modules/category'

const route = useRoute()
const router = useRouter()
const fallbackCategories = [
  { categoryId: 0, categoryName: '推荐' },
  { categoryId: 1, categoryName: '动画' },
  { categoryId: 2, categoryName: '游戏' },
  { categoryId: 3, categoryName: '音乐' },
  { categoryId: 4, categoryName: '科技' },
  { categoryId: 5, categoryName: '生活' }
]
const categories = ref(fallbackCategories)

async function loadCategories() {
  try {
    const data = await loadAllCategoryApi()
    categories.value = Array.isArray(data) && data.length ? [{ categoryId: 0, categoryName: '推荐' }, ...data] : fallbackCategories
  } catch {
    categories.value = fallbackCategories
  }
}

function selectCategory(item) {
  router.push({
    name: 'home',
    query: item.categoryId ? { pCategoryId: item.categoryId } : {}
  })
}

onMounted(loadCategories)
</script>

<style scoped>
.side-nav {
  position: sticky;
  top: 84px;
  height: fit-content;
  display: grid;
  gap: 10px;
}

.nav-item {
  height: 42px;
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
}

.nav-item.active {
  color: #fff;
  border: 0;
  background: var(--bil-gradient);
}

@media (max-width: 760px) {
  .side-nav {
    position: static;
    display: flex;
    overflow-x: auto;
    margin-bottom: 14px;
  }

  .nav-item {
    flex: 0 0 auto;
    padding: 0 16px;
  }
}
</style>
