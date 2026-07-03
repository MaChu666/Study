<template>
  <div v-if="error" class="error-boundary">
    <div class="error-boundary-content">
      <span class="error-icon">!</span>
      <p class="error-message">页面加载出错，请刷新重试</p>
      <button class="retry-btn" @click="handleRetry">刷新</button>
    </div>
  </div>
  <slot v-else />
</template>

<script setup>
import { onErrorCaptured, ref } from 'vue'

const error = ref(null)

onErrorCaptured((err, _instance, _info) => {
  console.error('[ErrorBoundary] Captured error:', err)
  error.value = err || new Error('Unknown error')
  return false // Prevent error from propagating further
})

function handleRetry() {
  window.location.reload()
}
</script>

<style scoped>
.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 40px 24px;
}

.error-boundary-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  text-align: center;
}

.error-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--bil-pink);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.error-message {
  color: var(--bil-text, #333);
  font-size: 15px;
  margin: 0;
}

.retry-btn {
  background: var(--bil-primary, #00a1d6);
  color: #fff;
  border: none;
  padding: 8px 28px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.2s;
}

.retry-btn:hover {
  opacity: 0.85;
}
</style>
