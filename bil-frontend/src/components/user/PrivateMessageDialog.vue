<template>
  <el-dialog
    :model-value="visible"
    :title="'私信 ' + (targetName || '用户')"
    width="520px"
    top="10vh"
    destroy-on-close
    @update:model-value="emit('update:visible', $event)"
    @close="emit('update:visible', false)"
  >
    <div class="pm-container">
      <div class="pm-messages" ref="msgBoxRef">
        <div v-if="msgsLoading" class="pm-placeholder">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else-if="!messages.length" class="pm-placeholder">
          <p>暂无消息，发送第一条私信吧</p>
        </div>
        <div
          v-for="m in messages"
          :key="m.messageId || m.id"
          class="pm-msg"
          :class="{ 'pm-msg-self': isSelf(m) }"
        >
          <div class="pm-msg-content">{{ m.content }}</div>
          <div class="pm-msg-time">{{ m.createTime || '' }}</div>
        </div>
      </div>
      <div class="pm-input-row">
        <textarea
          v-model="draft"
          class="pm-textarea"
          placeholder="输入私信内容..."
          rows="2"
          maxlength="500"
          @keyup.enter.exact="send"
        />
        <button class="pm-send-btn" type="button" :disabled="!draft.trim() || sending" @click="send">
          {{ sending ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { nextTick, ref, watch } from 'vue'
import { sendPrivateMessageApi, loadPrivateMessagesApi } from '@/api/modules/user'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  visible: { type: Boolean, default: false },
  targetUserId: { type: String, default: '' },
  targetName: { type: String, default: '' }
})

const emit = defineEmits(['update:visible'])
const userStore = useUserStore()

const draft = ref('')
const sending = ref(false)
const messages = ref([])
const msgsLoading = ref(false)
const msgBoxRef = ref(null)

function isSelf(m) {
  return m.userId === (userStore.profile?.userId)
}

async function loadMessages() {
  if (!props.targetUserId) return
  msgsLoading.value = true
  try {
    const data = await loadPrivateMessagesApi({ targetUserId: props.targetUserId })
    messages.value = Array.isArray(data) ? data : []
  } catch {
    messages.value = []
  } finally {
    msgsLoading.value = false
  }
}

async function send() {
  const text = draft.value.trim()
  if (!text || sending.value) return
  sending.value = true
  try {
    await sendPrivateMessageApi({
      targetUserId: props.targetUserId,
      content: text
    })
    // Add to local messages
    messages.value.push({
      userId: userStore.profile?.userId,
      content: text,
      createTime: new Date().toISOString().replace('T', ' ').slice(0, 19)
    })
    draft.value = ''
    nextTick(() => {
      if (msgBoxRef.value) msgBoxRef.value.scrollTop = msgBoxRef.value.scrollHeight
    })
  } catch {
    // handled by request interceptor
  } finally {
    sending.value = false
  }
}

watch(() => props.visible, (v) => { if (v) { draft.value = ''; loadMessages() } })
</script>

<style scoped>
.pm-container {
  display: flex; flex-direction: column; height: 360px;
}
.pm-messages {
  flex: 1; overflow-y: auto; padding: 8px 4px;
  display: flex; flex-direction: column; gap: 10px;
  min-height: 0;
}
.pm-placeholder {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  height: 100%; color: var(--bil-muted); font-size: 13px;
}
.spinner {
  width: 24px; height: 24px; border: 2px solid var(--bil-border);
  border-top-color: var(--bil-primary); border-radius: 50%;
  animation: spin 0.7s linear infinite; margin-bottom: 6px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.pm-msg {
  max-width: 75%; padding: 8px 12px; border-radius: 10px;
  background: var(--bil-hover); font-size: 13px; line-height: 1.5;
}
.pm-msg-self {
  align-self: flex-end; background: var(--bil-primary); color: #fff;
}
.pm-msg-content { word-break: break-word; }
.pm-msg-time { font-size: 10px; margin-top: 4px; opacity: 0.6; }
.pm-input-row {
  display: flex; gap: 8px; padding-top: 10px;
  border-top: 1px solid var(--bil-border);
}
.pm-textarea {
  flex: 1; padding: 8px 10px; border: 1px solid var(--bil-border);
  border-radius: 8px; background: var(--bil-hover); color: var(--bil-text);
  font-size: 13px; resize: none; outline: none; font-family: inherit;
}
.pm-textarea:focus { border-color: var(--bil-primary); }
.pm-send-btn {
  flex-shrink: 0; align-self: flex-end;
  padding: 6px 16px; border: 0; border-radius: 6px;
  background: var(--bil-primary); color: #fff; font-size: 13px;
  cursor: pointer; white-space: nowrap; transition: opacity 0.2s;
}
.pm-send-btn:hover:not(:disabled) { opacity: 0.85; }
.pm-send-btn:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
