<template>
  <div class="interaction-bar">
    <!-- Like -->
    <button
      type="button"
      class="ia-btn"
      :class="{ active: liked, disabled: loading.like }"
      :disabled="loading.like"
      @click="toggleLike"
    >
      <svg v-if="liked" class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
        <path d="M1 21h4V9H1v12zm22-11c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.59 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2z" />
      </svg>
      <svg v-else class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
        <path d="M9 21h9c.83 0 1.54-.5 1.84-1.22l3.02-7.05c.09-.23.14-.47.14-.73v-2c0-1.1-.9-2-2-2h-6.31l.95-4.57.03-.32c0-.41-.17-.79-.44-1.06L14.17 1 7.58 7.59C7.22 7.95 7 8.45 7 9v10c0 1.1.9 2 2 2zM9 9l6.34-6.34L13.1 9H21v2l-3 7H9V9zM1 9h4v12H1V9z" />
      </svg>
      <span>{{ counts.like }}</span>
    </button>

    <!-- Coin -->
    <div class="ia-btn-wrapper">
      <button
        type="button"
        class="ia-btn"
        :class="{ active: coined, disabled: loading.coin || coined }"
        :disabled="loading.coin || coined"
        @click="toggleCoinPopup"
      >
        <svg v-if="coined" class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
          <circle cx="12" cy="12" r="9" fill="currentColor" opacity="0.3" />
        </svg>
        <svg v-else class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.31-8.86c-1.77-.45-2.34-.94-2.34-1.67 0-.84.79-1.43 2.1-1.43 1.38 0 1.9.66 1.94 1.64h1.71c-.05-1.34-.87-2.57-2.49-2.97V5H10.9v1.69c-1.51.32-2.72 1.3-2.72 2.81 0 1.79 1.49 2.69 3.66 3.21 1.95.46 2.34 1.15 2.34 1.87 0 .53-.39 1.39-2.1 1.39-1.6 0-2.23-.72-2.32-1.64H8.04c.1 1.7 1.36 2.66 2.86 2.97V19h2.34v-1.67c1.52-.29 2.72-1.16 2.73-2.77-.01-2.2-1.9-2.96-3.66-3.42z" />
        </svg>
        <span>{{ counts.coin }}</span>
      </button>
      <!-- Coin popup -->
      <div v-if="showCoinPopup" class="coin-popup">
        <button type="button" class="coin-option-btn" :disabled="loading.coin" @click="doCoin(1)">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><circle cx="12" cy="12" r="9" /><text x="12" y="16" text-anchor="middle" font-size="12" fill="#fff" font-weight="700">1</text></svg>
          投1枚
        </button>
        <button type="button" class="coin-option-btn" :disabled="loading.coin" @click="doCoin(2)">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><circle cx="12" cy="12" r="9" /><text x="12" y="16" text-anchor="middle" font-size="12" fill="#fff" font-weight="700">2</text></svg>
          投2枚
        </button>
      </div>
    </div>

    <!-- Collect -->
    <button
      type="button"
      class="ia-btn"
      :class="{ active: collected, disabled: loading.collect }"
      :disabled="loading.collect"
      @click="toggleCollect"
    >
      <svg v-if="collected" class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
        <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
      </svg>
      <svg v-else class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
        <path d="M22 9.24l-7.19-.62L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21 12 17.27 18.18 21l-1.63-7.03L22 9.24zM12 15.4l-3.76 2.27 1-4.28-3.32-2.88 4.38-.38L12 6.1l1.71 4.04 4.38.38-3.32 2.88 1 4.28L12 15.4z" />
      </svg>
      <span>{{ counts.collect }}</span>
    </button>

    <!-- Share -->
    <button type="button" class="ia-btn" @click="share">
      <svg class="ia-icon" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
        <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92-1.31-2.92-2.92-2.92z" />
      </svg>
      <span>分享</span>
    </button>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { doActionApi } from '@/api/modules/video'

const props = defineProps({
  video: {
    type: Object,
    required: true
  },
  liked: {
    type: Boolean,
    default: false
  },
  collected: {
    type: Boolean,
    default: false
  },
  coined: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:liked', 'update:collected', 'update:coined'])

const counts = reactive({ like: 0, collect: 0, coin: 0 })
const loading = reactive({ like: false, collect: false, coin: false })
const showCoinPopup = ref(false)

// Sync counts from video prop
watch(
  function () { return props.video },
  function (video) {
    counts.like = Number(video && video.likeCount || 0)
    counts.collect = Number(video && video.collectCount || 0)
    counts.coin = Number(video && video.coinCount || 0)
  },
  { immediate: true }
)

async function toggleLike() {
  if (loading.like) return
  loading.like = true
  var wasLiked = props.liked
  try {
    if (wasLiked) {
      await doActionApi({ videoId: props.video.videoId, actionType: 1, actionCount: 1, commentId: 0 })
      counts.like = Math.max(0, counts.like - 1)
      emit('update:liked', false)
    } else {
      await doActionApi({ videoId: props.video.videoId, actionType: 1, actionCount: 1, commentId: 0 })
      counts.like += 1
      emit('update:liked', true)
    }
  } catch (_e) {
    ElMessage.error('操作失败，请稍后再试')
  } finally {
    loading.like = false
  }
}

function toggleCoinPopup() {
  if (props.coined || loading.coin) return
  showCoinPopup.value = !showCoinPopup.value
}

async function doCoin(count) {
  if (loading.coin) return
  loading.coin = true
  showCoinPopup.value = false
  try {
    await doActionApi({ videoId: props.video.videoId, actionType: 2, actionCount: count, commentId: 0 })
    counts.coin += count
    emit('update:coined', true)
    ElMessage.success('已投' + count + '枚硬币')
  } catch (_e) {
    ElMessage.error('投币失败')
  } finally {
    loading.coin = false
  }
}

async function toggleCollect() {
  if (loading.collect) return
  loading.collect = true
  var wasCollected = props.collected
  try {
    if (wasCollected) {
      await doActionApi({ videoId: props.video.videoId, actionType: 3, actionCount: 1, commentId: 0 })
      counts.collect = Math.max(0, counts.collect - 1)
      emit('update:collected', false)
    } else {
      await doActionApi({ videoId: props.video.videoId, actionType: 3, actionCount: 1, commentId: 0 })
      counts.collect += 1
      emit('update:collected', true)
    }
  } catch (_e) {
    ElMessage.error('操作失败，请稍后再试')
  } finally {
    loading.collect = false
  }
}

function share() {
  var url = window.location.origin + '/video/' + props.video.videoId
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(function () {
      ElMessage.success('链接已复制到剪贴板')
    }).catch(function () {
      ElMessage.info('分享链接: ' + url)
    })
  } else {
    ElMessage.info('分享链接: ' + url)
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

.ia-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 9px 18px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}

.ia-btn:hover:not(:disabled) {
  background: var(--bil-hover);
  border-color: var(--bil-primary);
}

.ia-btn:disabled,
.ia-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Active / filled states */
.ia-btn.active {
  color: var(--bil-primary);
  border-color: var(--bil-primary);
  background: var(--bil-surface);
}

.ia-icon {
  flex-shrink: 0;
}

.ia-btn .ia-icon + span {
  font-variant-numeric: tabular-nums;
}

/* Coin button wrapper and popup */
.ia-btn-wrapper {
  position: relative;
}

.coin-popup {
  position: absolute;
  top: calc(100% + 6px);
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px;
  background: var(--bil-surface);
  border: 1px solid var(--bil-border);
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  z-index: 10;
  white-space: nowrap;
}

.coin-option-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}

.coin-option-btn:hover:not(:disabled) {
  background: var(--bil-hover);
  border-color: var(--bil-primary);
}

.coin-option-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
