<template>
  <aside class="danmu-panel">
    <h2>弹幕</h2>
    <div class="danmu-list">
      <p v-for="item in list" :key="item.danmuId || item.text">{{ item.text }}</p>
    </div>
    <div class="send-row">
      <el-input v-model="text" aria-label="发送弹幕" />
      <el-button class="bil-gradient-button" @click="post">发送</el-button>
    </div>
  </aside>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { loadDanmuApi, postDanmuApi } from '@/api/modules/danmu'
import { eventBus } from '@/utils/eventBus'

const props = defineProps({
  videoId: { type: String, required: true },
  fileId: { type: String, default: '' }
})

const fallbackDanmu = [
  { text: '这个开场有内味了' },
  { text: '前方高能' }
]

const list = ref([...fallbackDanmu])
const text = ref('')

async function load() {
  try {
    const data = await loadDanmuApi({ videoId: props.videoId, fileId: props.fileId })
    list.value = Array.isArray(data) && data.length ? data : [...fallbackDanmu]
  } catch {
    list.value = [...fallbackDanmu]
  }
}

async function post() {
  const content = text.value.trim()
  if (!content) return
  await postDanmuApi({ videoId: props.videoId, fileId: props.fileId, text: content, mode: 1, color: '#ffffff', time: 0 })
  list.value.push({ text: content })
  text.value = ''
  eventBus.emit('danmu:posted', props.videoId)
}

watch(
  () => [props.videoId, props.fileId],
  () => {
    load()
  }
)

onMounted(load)
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

.danmu-list {
  display: grid;
  gap: 8px;
  max-height: 280px;
  overflow: auto;
  color: var(--bil-muted);
}

.send-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  margin-top: 14px;
}
</style>
