<template>
  <div class="interaction-bar">
    <button type="button" @click="like">点赞 {{ counts.like }}</button>
    <button type="button" @click="collect">收藏 {{ counts.collect }}</button>
    <button type="button" @click="coin">投币 {{ counts.coin }}</button>
    <button type="button">分享</button>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { doActionApi } from '@/api/modules/video'
import { eventBus } from '@/utils/eventBus'

const props = defineProps({
  video: {
    type: Object,
    required: true
  }
})

const counts = reactive({ like: 0, collect: 0, coin: 0 })

watch(
  () => props.video,
  (video) => {
    counts.like = Number(video?.likeCount || 0)
    counts.collect = Number(video?.collectCount || 0)
    counts.coin = Number(video?.coinCount || 0)
  },
  { immediate: true }
)

async function like() {
  try {
    await doActionApi({ videoId: props.video.videoId, actionType: 1, actionCount: 1, commentId: 0 })
    counts.like += 1
    eventBus.emit('video:liked', props.video.videoId)
  } catch {
    ElMessage.error('操作失败，请稍后再试')
  }
}

async function collect() {
  try {
    await doActionApi({ videoId: props.video.videoId, actionType: 3, actionCount: 1, commentId: 0 })
    counts.collect += 1
    eventBus.emit('video:collected', props.video.videoId)
  } catch {
    ElMessage.error('操作失败，请稍后再试')
  }
}

async function coin() {
  try {
    await doActionApi({ videoId: props.video.videoId, actionType: 2, actionCount: 1, commentId: 0 })
    counts.coin += 1
  } catch {
    ElMessage.error('操作失败，请稍后再试')
  }
}
</script>

<style scoped>
.interaction-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 18px 0;
}

button {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 9px 16px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
}

button:first-child {
  border: 0;
  color: #fff;
  background: var(--bil-gradient);
}
</style>
