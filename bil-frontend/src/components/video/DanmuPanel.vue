<template>
  <aside class="danmu-panel">
    <h2>弹幕</h2>

    <!-- Danmu is turned off -->
    <div v-if="!settings.on" class="danmu-off-hint">
      <span class="hint-icon">弹幕已关闭</span>
    </div>

    <!-- Empty state -->
    <div v-else-if="list.length === 0" class="danmu-empty">暂无弹幕</div>

    <!-- Danmu list -->
    <div
      v-else
      ref="listContainerRef"
      class="danmu-list"
    >
      <div
        v-for="item in list"
        :key="item.danmuId"
        class="danmu-item"
      >
        <span class="danmu-time">{{ formatVideoTime(item.time) }}</span>
        <span
          class="danmu-bubble"
          :style="{ color: item.color || '#ffffff' }"
        >{{ item.text }}</span>
      </div>
    </div>

    <!-- Send row -->
    <div class="send-row">
      <el-input v-model="text" aria-label="发送弹幕" placeholder="发送弹幕，精彩马上出现" />
      <el-button class="bil-gradient-button" @click="post">发送</el-button>
    </div>
  </aside>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { loadDanmuApi, postDanmuApi } from '@/api/modules/danmu'
import { eventBus } from '@/utils/eventBus'

const props = defineProps({
  videoId: { type: String, required: true },
  fileId: { type: String, default: '' },
  settings: {
    type: Object,
    default: () => ({ on: true, opacity: 0.8, fontSize: 'normal', area: 'full', speed: 'normal' })
  }
})

const MAX_DANMU = 50
const POLL_INTERVAL = 5000

const list = ref([])
const text = ref('')
const listContainerRef = ref(null)
let pollTimer = null

/* ============================
   Format time (seconds → MM:SS)
   ============================ */
function formatVideoTime(seconds) {
  const s = Math.floor(Number(seconds) || 0)
  const m = Math.floor(s / 60)
  const sec = s % 60
  return `${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
}

/* ============================
   Scroll list to bottom
   ============================ */
function scrollToBottom() {
  const el = listContainerRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

/* ============================
   Load danmu from API
   ============================ */
async function load() {
  if (!props.videoId || !props.fileId) return
  try {
    const data = await loadDanmuApi({ videoId: props.videoId, fileId: props.fileId })
    if (Array.isArray(data)) {
      // Keep only the latest MAX_DANMU items (sorted by time ascending from API)
      list.value = data.slice(-MAX_DANMU)
    } else {
      list.value = []
    }
  } catch {
    // Keep current list on network error
  }
  await nextTick()
  scrollToBottom()
}

/* ============================
   Polling
   ============================ */
function startPolling() {
  stopPolling()
  if (props.settings.on && props.videoId && props.fileId) {
    pollTimer = setInterval(load, POLL_INTERVAL)
  }
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

/* ============================
   Post a new danmu
   ============================ */
async function post() {
  const content = text.value.trim()
  if (!content) return
  try {
    await postDanmuApi({
      videoId: props.videoId,
      fileId: props.fileId,
      text: content,
      mode: 1,
      color: '#ffffff',
      time: 0
    })
    text.value = ''
    eventBus.emit('danmu:posted', props.videoId)
    // Refresh list immediately after posting
    await load()
  } catch {
    // Error handled by request interceptor (ElMessage)
  }
}

/* ============================
   Watchers
   ============================ */
watch(
  () => props.settings.on,
  (on) => {
    if (on) {
      load()
      startPolling()
    } else {
      stopPolling()
    }
  }
)

watch(
  () => [props.videoId, props.fileId],
  () => {
    load()
    if (props.settings.on) {
      startPolling()
    }
  }
)

/* ============================
   Lifecycle
   ============================ */
onMounted(() => {
  load()
  if (props.settings.on) {
    startPolling()
  }
})

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
.danmu-panel {
  border-radius: 12px;
  padding: 18px;
  background: var(--bil-surface);
}

h2 {
  margin: 0 0 12px;
  font-size: 18px;
}

/* ---- Off / Empty states ---- */
.danmu-off-hint,
.danmu-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  color: var(--bil-muted);
  font-size: 14px;
}

.danmu-off-hint .hint-icon {
  opacity: 0.7;
}

/* ---- Danmu list ---- */
.danmu-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
}

.danmu-list::-webkit-scrollbar {
  width: 4px;
}

.danmu-list::-webkit-scrollbar-thumb {
  border-radius: 2px;
  background: var(--bil-border);
}

/* ---- Danmu item ---- */
.danmu-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 8px;
  background: var(--bil-hover);
  transition: background 0.15s;
}

.danmu-item:hover {
  background: var(--bil-border);
}

.danmu-time {
  flex-shrink: 0;
  min-width: 40px;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
  color: var(--bil-muted);
  background: var(--bil-hover);
}

.danmu-bubble {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  line-height: 22px;
  word-break: break-word;
}

/* ---- Send row ---- */
.send-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  margin-top: 14px;
}
</style>
