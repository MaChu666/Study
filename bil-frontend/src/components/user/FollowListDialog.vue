<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="480px"
    top="10vh"
    destroy-on-close
    @update:model-value="emit('update:visible', $event)"
    @close="emit('update:visible', false)"
  >
    <div v-if="loading" class="follow-loading">
      <div class="spinner" />
      <p>加载中...</p>
    </div>
    <div v-else-if="error" class="follow-error">
      <p>{{ error }}</p>
      <button class="retry-btn" type="button" @click="load">重试</button>
    </div>
    <div v-else-if="!list.length" class="follow-empty">暂无数据</div>
    <div v-else class="follow-list">
      <UserBadge
        v-for="item in list"
        :key="item.userId"
        :user-id="item.userId"
        :user-name="item.useName"
        :avatar="item.avatar"
        size="md"
        class="follow-item"
        @navigate="emit('update:visible', false)"
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { loadFocusListApi, loadFansListApi } from '@/api/modules/user'
import UserBadge from '@/components/user/UserBadge.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  type: { type: String, default: 'focus' }, // 'focus' | 'fans'
  userId: { type: String, default: '' }
})

const emit = defineEmits(['update:visible'])

const title = ref('')
const loading = ref(false)
const error = ref('')
const list = ref([])

async function load() {
  title.value = props.type === 'focus' ? '关注列表' : '粉丝列表'
  if (!props.userId) return
  loading.value = true
  error.value = ''
  list.value = []
  try {
    const api = props.type === 'focus' ? loadFocusListApi : loadFansListApi
    const data = await api({ userId: props.userId })
    list.value = Array.isArray(data) ? data : []
  } catch (e) {
    error.value = '加载失败'
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, (v) => { if (v) load() })
onMounted(() => { if (props.visible) load() })
</script>

<style scoped>
.follow-loading, .follow-error, .follow-empty {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 40px 20px; color: var(--bil-muted); font-size: 14px;
}
.spinner {
  width: 28px; height: 28px;
  border: 3px solid var(--bil-border);
  border-top-color: var(--bil-primary);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin-bottom: 8px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.retry-btn {
  padding: 6px 18px; border: 1px solid var(--bil-primary);
  border-radius: 6px; background: transparent; color: var(--bil-primary);
  font-size: 13px; cursor: pointer;
}
.retry-btn:hover { background: var(--bil-primary); color: #fff; }
.follow-list {
  max-height: 360px; overflow-y: auto; display: flex; flex-direction: column; gap: 2px;
}
.follow-item {
  padding: 10px 8px; border-radius: 8px;
  transition: background 0.15s;
  width: 100%;
}
.follow-item:hover { background: var(--bil-hover); }
</style>
