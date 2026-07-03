<template>
  <section class="message-center">
    <div class="msg-layout">
      <!-- Conversation list -->
      <div class="conv-list">
        <div class="conv-list-header">
          <h2>私信</h2>
        </div>
        <div v-if="loading" class="conv-loading">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        <div v-else-if="conversations.length === 0" class="conv-empty">
          <svg viewBox="0 0 120 120" width="80" height="80" fill="none" class="empty-illustration">
            <rect x="20" y="30" width="80" height="55" rx="10" stroke="var(--bil-border)" stroke-width="2" fill="var(--bil-hover)" />
            <circle cx="42" cy="52" r="10" stroke="var(--bil-border)" stroke-width="2" fill="var(--bil-surface)" />
            <circle cx="72" cy="52" r="10" stroke="var(--bil-border)" stroke-width="2" fill="var(--bil-surface)" />
            <path d="M42 82 L72 82" stroke="var(--bil-border)" stroke-width="2" stroke-linecap="round" />
            <path d="M35 92 L60 92" stroke="var(--bil-border)" stroke-width="2" stroke-linecap="round" />
          </svg>
          <span class="empty-text">暂无消息</span>
          <span class="empty-hint">浏览视频时，点击UP主头像可发送私信</span>
        </div>
        <div v-else class="conv-items">
          <div
            v-for="conv in conversations"
            :key="conv.userId"
            class="conv-item"
            :class="{ active: activeConv === conv.userId }"
            @click="openConversation(conv)"
          >
            <div class="conv-avatar">
              <img v-if="conv.avatar" :src="conv.avatar" class="conv-avatar-img" />
              <span v-else class="conv-avatar-text">{{ (conv.userName || '?')[0] }}</span>
            </div>
            <div class="conv-info">
              <div class="conv-top">
                <span class="conv-name">{{ conv.userName }}</span>
                <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
              </div>
              <div class="conv-bottom">
                <span class="conv-preview">{{ conv.lastMsg }}</span>
                <span v-if="conv.unreadCount" class="conv-badge">{{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Chat area -->
      <div class="chat-area">
        <template v-if="!activeConv">
          <div class="chat-placeholder">
            <svg viewBox="0 0 120 120" width="100" height="100" fill="none" class="placeholder-illustration">
              <circle cx="60" cy="45" r="22" stroke="var(--bil-border)" stroke-width="2" fill="var(--bil-hover)" />
              <path d="M30 90 Q38 70 44 68 Q50 66 60 70 Q70 74 76 68 Q82 70 90 90" stroke="var(--bil-border)" stroke-width="2" fill="var(--bil-hover)" stroke-linecap="round" />
              <circle cx="60" cy="78" r="3" fill="var(--bil-muted)" />
              <circle cx="52" cy="80" r="2.5" fill="var(--bil-muted)" />
              <circle cx="68" cy="80" r="2.5" fill="var(--bil-muted)" />
            </svg>
            <span class="placeholder-text">选择一个对话开始聊天</span>
            <span class="placeholder-hint">点击左侧用户即可查看消息</span>
          </div>
        </template>
        <template v-else>
          <div class="chat-header">
            <div class="chat-header-avatar">
              <img v-if="activeUser.avatar" :src="activeUser.avatar" class="chat-avatar-img" />
              <span v-else class="chat-avatar-text">{{ (activeUser.userName || '?')[0] }}</span>
            </div>
            <span class="chat-name">{{ activeUser.userName }}</span>
          </div>
          <div class="chat-messages" ref="chatMsgsRef">
            <div v-if="chatMessages.length === 0" class="chat-empty-msgs">
              <span>暂无消息，发送第一条消息吧</span>
            </div>
            <template v-for="(msg, idx) in chatMessagesWithDividers" :key="idx">
              <div v-if="msg._divider" class="time-divider">
                <span>{{ msg._divider }}</span>
              </div>
              <div v-else class="chat-msg" :class="{ mine: msg.mine }">
                <div class="chat-bubble">{{ msg.content }}</div>
                <div class="chat-time">{{ formatMessageTime(msg.time) }}</div>
              </div>
            </template>
          </div>
          <div class="chat-input-row">
            <textarea
              ref="chatTextareaRef"
              v-model="chatInput"
              class="chat-input"
              placeholder="输入消息..."
              rows="1"
              @keydown.enter.exact="sendMessage"
              @input="autoResizeTextarea"
            ></textarea>
            <button
              class="chat-send-btn"
              @click="sendMessage"
              :disabled="!chatInput.trim()"
              title="发送"
            >
              <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z" />
              </svg>
            </button>
          </div>
        </template>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { sendPrivateMessageApi } from '@/api/modules/user'
import { loadMessageApi, readAllMessageApi } from '@/api/modules/account'
import { getUserInfoApi } from '@/api/modules/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const messages = ref([])
const conversations = ref([])
const activeConv = ref(null)
const activeUser = ref({})
const chatMessages = ref([])
const chatInput = ref('')
const chatMsgsRef = ref(null)
const chatTextareaRef = ref(null)

const myUserId = computed(() => userStore.profile?.userId || '')
const CHAT_STORE_KEY = 'vidvault_chats_'

function loadChats(userId) {
  try {
    const key = CHAT_STORE_KEY + userId
    const data = localStorage.getItem(key)
    return data ? JSON.parse(data) : {}
  } catch { return {} }
}

function saveChats(userId, data) {
  try {
    localStorage.setItem(CHAT_STORE_KEY + userId, JSON.stringify(data))
  } catch {}
}

function getConvKey(uid1, uid2) {
  return [uid1, uid2].sort().join('_')
}

async function loadMessages() {
  loading.value = true
  try {
    const data = await loadMessageApi()
    messages.value = Array.isArray(data) ? data : []
    buildConversations()
  } catch { messages.value = [] }
  finally { loading.value = false }
}

function parsePmContent(m) {
  const raw = m.content || ''
  const myId = myUserId.value
  // Sent: "to:targetId:content"
  if (raw.startsWith('to:')) {
    const idx = raw.indexOf(':', 3)
    if (idx > 0) return { otherId: raw.substring(3, idx), content: raw.substring(idx + 1), mine: true }
  }
  // Received from: "senderId:content"
  const idx = raw.indexOf(':')
  if (idx > 0) {
    const senderId = raw.substring(0, idx)
    if (senderId !== myId) return { otherId: senderId, content: raw.substring(idx + 1), mine: false }
  }
  return null
}

function buildConversations() {
  const map = {}
  const myId = myUserId.value
  messages.value.filter(m => m.messageType === 5).forEach(m => {
    const parsed = parsePmContent(m)
    if (!parsed) return
    const oid = parsed.otherId
    if (!map[oid]) {
      var isUnread = !parsed.mine && m.readStatus === 0
      map[oid] = { userId: oid, userName: oid, lastMsg: parsed.content, lastTime: m.createTime, unreadCount: isUnread ? 1 : 0 }
    } else {
      if (new Date(m.createTime) > new Date(map[oid].lastTime)) {
        map[oid].lastMsg = parsed.content
        map[oid].lastTime = m.createTime
      }
      if (!parsed.mine && m.readStatus === 0) map[oid].unreadCount = (map[oid].unreadCount || 0) + 1
    }
  })
  Object.keys(map).forEach(uid => {
    const local = loadChats(getConvKey(myId, uid))
    if (local.messages && local.messages.length) {
      const last = local.messages[local.messages.length - 1]
      if (!map[uid].lastTime || new Date(last.time) > new Date(map[uid].lastTime)) {
        map[uid].lastMsg = last.content
        map[uid].lastTime = last.time
      }
    }
  })
  conversations.value = Object.values(map).sort((a, b) => new Date(b.lastTime) - new Date(a.lastTime))
  conversations.value.forEach(async c => {
    try {
      const info = await getUserInfoApi({ userId: c.userId })
      if (info) {
        c.userName = info.useName || c.userId
        c.avatar = info.avatar || ''
      }
    } catch {}
  })
}

async function openConversation(conv) {
  activeConv.value = conv.userId
  activeUser.value = { userId: conv.userId, userName: conv.userName, avatar: conv.avatar }
  // Mark as read in backend
  try { await readAllMessageApi() } catch (_) {}
  conv.unreadCount = 0
  messages.value.filter(m => m.messageType === 5).forEach(m => { m.readStatus = 1 })
  const myId = myUserId.value
  const convKey = getConvKey(myId, conv.userId)
  const local = loadChats(convKey)
  const msgs = (local.messages || []).map(l => ({ ...l, fromLocal: true }))
  const serverMsgs = messages.value
    .filter(m => m.messageType === 5)
    .map(m => {
      const parsed = parsePmContent(m)
      if (!parsed || parsed.otherId !== conv.userId) return null
      return { content: parsed.content, time: m.createTime, mine: parsed.mine, fromServer: true }
    })
    .filter(Boolean)
  const merged = combineMessages(msgs, serverMsgs)
  chatMessages.value = merged
  await nextTick()
  scrollChatBottom()
}

function combineMessages(local, server) {
  const all = [...local]
  server.forEach(s => {
    if (!all.find(a => a.time === s.time && a.content === s.content)) {
      all.push(s)
    }
  })
  return all.sort((a, b) => new Date(a.time) - new Date(b.time))
}

function autoResizeTextarea() {
  const el = chatTextareaRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

async function sendMessage() {
  const text = chatInput.value.trim()
  if (!text || !activeConv.value) return
  chatInput.value = ''
  // Reset textarea height
  if (chatTextareaRef.value) {
    chatTextareaRef.value.style.height = 'auto'
  }
  const now = new Date().toISOString()
  chatMessages.value.push({ content: text, time: now, mine: true })
  const conv = conversations.value.find(c => c.userId === activeConv.value)
  if (conv) { conv.lastMsg = text; conv.lastTime = now }
  // Save local
  const convKey = getConvKey(myUserId.value, activeConv.value)
  const local = loadChats(convKey)
  if (!local.messages) local.messages = []
  local.messages.push({ content: text, time: now, mine: true })
  local.lastMsg = text
  local.lastTime = now
  saveChats(convKey, local)
  // Send to server
  try { await sendPrivateMessageApi({ targetUserId: activeConv.value, content: text }) }
  catch { ElMessage.error('发送失败') }
  await nextTick()
  scrollChatBottom(true)
}

function scrollChatBottom(animate) {
  if (!chatMsgsRef.value) return
  if (animate) {
    chatMsgsRef.value.scrollTo({
      top: chatMsgsRef.value.scrollHeight,
      behavior: 'smooth'
    })
  } else {
    chatMsgsRef.value.scrollTop = chatMsgsRef.value.scrollHeight
  }
}

// --- Timestamp dividers ---
const chatMessagesWithDividers = computed(() => {
  if (!chatMessages.value.length) return []
  const result = []
  let lastDivider = ''
  chatMessages.value.forEach((msg, idx) => {
    const time = new Date(msg.time)
    const now = new Date()
    let dateLabel
    if (time.toDateString() === now.toDateString()) {
      dateLabel = '今天'
    } else {
      const yesterday = new Date(now)
      yesterday.setDate(yesterday.getDate() - 1)
      if (time.toDateString() === yesterday.toDateString()) {
        dateLabel = '昨天'
      } else {
        dateLabel = time.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
      }
    }
    const timeLabel = time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    const divider = dateLabel + ' ' + timeLabel
    if (divider !== lastDivider) {
      lastDivider = divider
      result.push({ _divider: divider })
    }
    result.push(msg)
  })
  return result
})

function formatMessageTime(t) {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (d.toDateString() === now.toDateString()) return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (d.toDateString() === yesterday.toDateString()) return '昨天'
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

async function markAllRead() {
  try { await readAllMessageApi(); messages.value.forEach(m => m.readStatus = 1) }
  catch { ElMessage.error('操作失败') }
}

onMounted(loadMessages)
</script>

<style scoped>
.message-center {
  height: calc(100vh - 130px);
}

.msg-layout {
  display: flex;
  height: 100%;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid var(--bil-border);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

[data-theme="dark"] .msg-layout {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

/* ===== Conversation List ===== */
.conv-list {
  width: 300px;
  background: var(--bil-surface);
  border-right: 1px solid var(--bil-border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.conv-list-header {
  padding: 16px 18px 12px;
  border-bottom: 1px solid var(--bil-border);
}

.conv-list-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.2px;
}

.conv-items {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.conv-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  cursor: pointer;
  transition: background 0.15s, transform 0.1s;
  position: relative;
}

.conv-item:hover {
  background: var(--bil-hover);
}

.conv-item.active {
  background: var(--bil-primary);
  /* subtle tint via opacity on primary color */
  background: linear-gradient(135deg, var(--bil-primary) 0%, rgba(0, 161, 214, 0.08) 100%);
  background: rgba(0, 161, 214, 0.08);
  border-left: 3px solid var(--bil-primary);
  padding-left: 15px;
}

/* Avatar */
.conv-avatar {
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

.conv-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.conv-avatar-text {
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  line-height: 1;
}

/* Info */
.conv-info {
  flex: 1;
  min-width: 0;
}

.conv-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conv-name {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.conv-time {
  font-size: 11px;
  color: var(--bil-muted);
  white-space: nowrap;
  flex-shrink: 0;
}

.conv-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.conv-preview {
  font-size: 12px;
  color: var(--bil-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 160px;
}

.conv-badge {
  min-width: 18px;
  height: 18px;
  border-radius: 999px;
  background: #ff4d4f;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  line-height: 1;
  flex-shrink: 0;
}

/* Conv list empty/loading */
.conv-loading {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--bil-muted);
  font-size: 13px;
  gap: 10px;
}

.loading-spinner {
  width: 28px;
  height: 28px;
  border: 3px solid var(--bil-border);
  border-top-color: var(--bil-primary);
  border-radius: 50%;
  animation: msg-spin 0.7s linear infinite;
}

@keyframes msg-spin {
  to { transform: rotate(360deg); }
}

.conv-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 20px;
  color: var(--bil-muted);
  text-align: center;
}

.empty-illustration {
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-text {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--bil-text);
}

.empty-hint {
  font-size: 12px;
  color: var(--bil-muted);
  max-width: 200px;
  line-height: 1.5;
}

/* ===== Chat Area ===== */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background:
    linear-gradient(180deg, var(--bil-bg) 0%, rgba(0, 161, 214, 0.02) 100%);
  background: var(--bil-bg);
  min-width: 0;
}

[data-theme="dark"] .chat-area {
  background:
    linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.chat-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--bil-muted);
  gap: 12px;
}

.placeholder-illustration {
  margin-bottom: 8px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--bil-text);
}

.placeholder-hint {
  font-size: 13px;
  color: var(--bil-muted);
}

/* Chat header */
.chat-header {
  padding: 12px 18px;
  background: var(--bil-surface);
  border-bottom: 1px solid var(--bil-border);
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.chat-header-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bil-pink);
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-avatar-text {
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}

.chat-name {
  font-size: 16px;
  font-weight: 600;
}

/* Chat messages area */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px 18px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  scroll-behavior: smooth;
}

.chat-empty-msgs {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bil-muted);
  font-size: 14px;
}

/* Message bubble */
.chat-msg {
  display: flex;
  flex-direction: column;
  margin-bottom: 14px;
}

.chat-msg:not(.mine) {
  align-items: flex-start;
}

.chat-msg.mine {
  align-items: flex-end;
}

.chat-bubble {
  max-width: 72%;
  padding: 10px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.55;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
}

.chat-msg:not(.mine) .chat-bubble {
  background: #fff;
  color: var(--bil-text);
  border: 1px solid var(--bil-border);
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

[data-theme="dark"] .chat-msg:not(.mine) .chat-bubble {
  background: #2a2a3e;
  border-color: rgba(255, 255, 255, 0.08);
}

.chat-msg.mine .chat-bubble {
  background: var(--bil-primary);
  color: #fff;
  border-bottom-right-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 161, 214, 0.2);
}

.chat-time {
  font-size: 11px;
  color: var(--bil-muted);
  margin-top: 4px;
  padding: 0 4px;
}

/* Time divider */
.time-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 18px 0;
}

.time-divider span {
  font-size: 12px;
  color: var(--bil-muted);
  background: var(--bil-bg);
  padding: 4px 12px;
  border-radius: 999px;
  border: 1px solid var(--bil-border);
}

[data-theme="dark"] .time-divider span {
  background: #1a1a2e;
}

/* Chat input row */
.chat-input-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 12px 18px;
  background: var(--bil-surface);
  border-top: 1px solid var(--bil-border);
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
  min-height: 40px;
  max-height: 120px;
  padding: 10px 14px;
  border: 1px solid var(--bil-border);
  border-radius: 20px;
  background: var(--bil-bg);
  color: var(--bil-text);
  font-size: 14px;
  line-height: 1.45;
  outline: none;
  resize: none;
  font-family: inherit;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.chat-input:focus {
  border-color: var(--bil-primary);
  box-shadow: 0 0 0 3px rgba(0, 161, 214, 0.1);
}

.chat-input::placeholder {
  color: var(--bil-muted);
}

.chat-send-btn {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: var(--bil-primary);
  color: #fff;
  cursor: pointer;
  transition: background 0.2s, transform 0.15s, box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0, 161, 214, 0.3);
}

.chat-send-btn:hover:not(:disabled) {
  background: var(--bil-primary-hover);
  transform: scale(1.06);
  box-shadow: 0 4px 12px rgba(0, 161, 214, 0.4);
}

.chat-send-btn:active:not(:disabled) {
  transform: scale(0.96);
}

.chat-send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  box-shadow: none;
}

/* ===== Responsive ===== */
@media (max-width: 760px) {
  .message-center {
    height: calc(100vh - 100px);
  }

  .conv-list {
    width: 100%;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
    border-radius: 14px 14px 0 0;
  }

  .chat-area {
    border-radius: 14px;
  }

  .chat-bubble {
    max-width: 85%;
  }
}
</style>
