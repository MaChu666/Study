<template>
  <section class="comment-list">
    <h2>评论</h2>

    <!-- Sort tabs -->
    <div class="sort-tabs">
      <button
        type="button"
        :class="{ active: orderType === 0 }"
        @click="switchSort(0)"
      >
        按热度
      </button>
      <button
        type="button"
        :class="{ active: orderType === 1 }"
        @click="switchSort(1)"
      >
        按时间
      </button>
    </div>

    <!-- Post input -->
    <div class="post-row">
      <div class="comment-avatar">
        <img v-if="currentUserAvatarImg" :src="currentUserAvatarImg" class="comment-avatar-img" alt="" />
        <template v-else>{{ currentUserAvatar }}</template>
      </div>
      <div class="post-input-wrap">
        <textarea
          v-model="content"
          class="comment-textarea"
          placeholder="发一条友善的评论吧"
          rows="3"
        />
        <el-button class="bil-gradient-button" @click="post">发布</el-button>
      </div>
    </div>

    <!-- Empty state -->
    <div v-if="topLevelComments.length === 0 && !loading" class="empty-state">
      <p>暂无评论，快来发第一条吧</p>
    </div>

    <!-- Comment threads -->
    <div v-for="item in topLevelComments" :key="item.commentId" class="comment-thread">
      <div class="comment-item">
        <div class="comment-avatar">
          <img v-if="item.avatar" :src="item.avatar" class="comment-avatar-img" alt="" />
          <template v-else>{{ getAvatar(item.userName) }}</template>
        </div>
        <div class="comment-body">
          <div class="comment-header">
            <span class="comment-username">{{ item.userName || 'VidVault 用户' }}</span>
            <span v-if="isAuthor(item)" class="up-badge">UP主</span>
            <span class="comment-time">{{ timeAgo(item.postTime) }}</span>
          </div>
          <p class="comment-content">{{ item.content }}</p>
          <div class="comment-actions">
            <button type="button" class="action-btn" @click="toggleReplyInput(item)">
              回复
            </button>
            <button
              v-if="isOwnComment(item)"
              type="button"
              class="action-btn danger"
              @click="deleteComment(item)"
            >
              删除
            </button>
          </div>

          <!-- Inline reply input -->
          <div v-if="replyingTo === item.commentId" class="reply-input-wrap">
            <textarea
              v-model="replyContent"
              class="comment-textarea"
              placeholder="写下你的回复..."
              rows="2"
            />
            <div class="reply-actions">
              <button type="button" class="cancel-btn" @click="cancelReply">取消</button>
              <button type="button" class="submit-btn" @click="postReply(item)">回复</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Nested replies section -->
      <div v-if="getReplies(item.commentId).length > 0" class="replies-section">
        <button
          v-if="!expandedReplies[item.commentId]"
          type="button"
          class="load-replies-btn"
          @click="loadReplies(item)"
        >
          查看{{ getReplies(item.commentId).length }}条回复 ▼
        </button>

        <div v-if="expandedReplies[item.commentId]" class="replies-list">
          <div
            v-for="reply in getReplies(item.commentId)"
            :key="reply.commentId"
            class="comment-item reply-item"
          >
            <div class="comment-avatar small">
              <img v-if="reply.avatar" :src="reply.avatar" class="comment-avatar-img" alt="" />
              <template v-else>{{ getAvatar(reply.userName) }}</template>
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-username">{{ reply.userName || 'VidVault 用户' }}</span>
                <span v-if="isAuthor(reply)" class="up-badge">UP主</span>
                <span class="comment-time">{{ timeAgo(reply.postTime) }}</span>
              </div>
              <p class="comment-content">{{ reply.content }}</p>
              <div class="comment-actions">
                <button
                  v-if="isOwnComment(reply)"
                  type="button"
                  class="action-btn danger"
                  @click="deleteComment(reply)"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
          <button
            type="button"
            class="load-replies-btn"
            @click="collapseReplies(item.commentId)"
          >
            收起回复 ▲
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loadCommentApi, postCommentApi, userDelCommentApi } from '@/api/modules/comment'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  videoId: { type: String, required: true },
  authorId: { type: String, default: '' }
})

const userStore = useUserStore()

const fallbackComments = [{ userName: '路过的观众', content: '这个视频值得三连。' }]

const allComments = ref([])
const content = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const orderType = ref(0)
const loading = ref(false)
const expandedReplies = reactive({})

// --- computed ---
const currentUserAvatarImg = computed(() => userStore.profile?.avatar || '')
const currentUserAvatar = computed(() => {
  const name = userStore.profile?.userName || '我'
  return (name || '我')[0]
})

const topLevelComments = computed(() =>
  allComments.value.filter(
    (c) => !c.replyCommentId || String(c.replyCommentId) === '0'
  )
)

const repliesMap = computed(() => {
  const map = {}
  allComments.value.forEach((c) => {
    if (c.replyCommentId && String(c.replyCommentId) !== '0') {
      const parentId = String(c.replyCommentId)
      if (!map[parentId]) map[parentId] = []
      map[parentId].push(c)
    }
  })
  return map
})

// --- helpers ---
function getAvatar(name) {
  return (name || '用')[0]
}

function timeAgo(dateStr) {
  if (!dateStr) return ''
  const d = typeof dateStr === 'number' ? new Date(dateStr) : new Date(dateStr)
  if (isNaN(d.getTime())) return ''
  const diff = Date.now() - d.getTime()
  const seconds = Math.floor(diff / 1000)
  if (seconds < 60) return '刚刚'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`
  const months = Math.floor(days / 30)
  if (months < 12) return `${months}个月前`
  const years = Math.floor(months / 12)
  return `${years}年前`
}

function isAuthor(comment) {
  return Boolean(props.authorId && String(comment.userId) === String(props.authorId))
}

function isOwnComment(comment) {
  const profile = userStore.profile
  if (!profile) return false
  const myId = String(profile.userId ?? '')
  const commentUserId = String(comment.userId ?? '')
  return myId && commentUserId && myId === commentUserId
}

function getReplies(parentId) {
  return repliesMap.value[String(parentId)] || []
}

// --- actions ---
async function load() {
  loading.value = true
  try {
    const data = await loadCommentApi({
      videoId: props.videoId,
      pageNo: 1,
      orderType: orderType.value
    })
    if (Array.isArray(data?.list) && data.list.length) {
      allComments.value = data.list
      return
    }
    if (Array.isArray(data) && data.length) {
      allComments.value = data
      return
    }
    // API returned successfully but empty — show empty state
    allComments.value = []
  } catch {
    // API errored — show fallback
    allComments.value = [...fallbackComments]
  } finally {
    loading.value = false
  }
}

function switchSort(type) {
  if (orderType.value === type) return
  orderType.value = type
  expandedReplies && Object.keys(expandedReplies).forEach(k => delete expandedReplies[k])
  load()
}

async function post() {
  const nextContent = content.value.trim()
  if (!nextContent) return
  if (!userStore.isLogin) {
    userStore.openLoginDialog()
    ElMessage.warning('请先登录')
    return
  }
  try {
    await postCommentApi({
      videoId: props.videoId,
      content: nextContent,
      replyCommentId: '0',
      imgPath: ''
    })
    content.value = ''
    replyingTo.value = null
    replyContent.value = ''
    await load()
  } catch {
    ElMessage.error('发布失败，请稍后再试')
  }
}

async function postReply(parent) {
  const nextContent = replyContent.value.trim()
  if (!nextContent) return
  if (!userStore.isLogin) {
    userStore.openLoginDialog()
    ElMessage.warning('请先登录')
    return
  }
  try {
    await postCommentApi({
      videoId: props.videoId,
      content: nextContent,
      replyCommentId: String(parent.commentId),
      imgPath: ''
    })
    replyContent.value = ''
    replyingTo.value = null
    expandedReplies[String(parent.commentId)] = true
    await load()
  } catch {
    ElMessage.error('回复失败，请稍后再试')
  }
}

function toggleReplyInput(comment) {
  if (!userStore.isLogin) {
    userStore.openLoginDialog()
    ElMessage.warning('请先登录')
    return
  }
  replyingTo.value = replyingTo.value === comment.commentId ? null : comment.commentId
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

function loadReplies(comment) {
  expandedReplies[String(comment.commentId)] = true
}

function collapseReplies(commentId) {
  delete expandedReplies[String(commentId)]
}

async function deleteComment(comment) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await userDelCommentApi({ commentId: comment.commentId })
    // Remove from local
    allComments.value = allComments.value.filter(
      (c) => String(c.commentId) !== String(comment.commentId)
    )
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败，请稍后再试')
  }
}

watch(
  () => props.videoId,
  () => {
    replyingTo.value = null
    replyContent.value = ''
    Object.keys(expandedReplies).forEach(k => delete expandedReplies[k])
    load()
  }
)

onMounted(load)
</script>

<style scoped>
.comment-list {
  margin-top: 24px;
  border-radius: 12px;
  padding: 20px;
  background: var(--bil-surface);
}

h2 {
  margin: 0 0 14px;
}

/* Sort tabs */
.sort-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 16px;
  border-bottom: 2px solid var(--bil-border);
}

.sort-tabs button {
  background: none;
  border: none;
  padding: 8px 18px;
  font-size: 14px;
  color: var(--bil-muted);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: color 0.2s, border-color 0.2s;
}

.sort-tabs button.active,
.sort-tabs button:hover {
  color: var(--bil-primary, #00a1d6);
  border-bottom-color: var(--bil-primary, #00a1d6);
}

/* Post row */
.post-row {
  display: flex;
  gap: 12px;
  margin-bottom: 22px;
  align-items: flex-start;
}

.post-input-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.comment-textarea {
  width: 100%;
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  color: var(--bil-text);
  background: var(--bil-bg);
  resize: vertical;
  outline: none;
  font-family: inherit;
  box-sizing: border-box;
}

.comment-textarea:focus {
  border-color: var(--bil-primary, #00a1d6);
}

/* Empty state */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--bil-muted);
  font-size: 14px;
}

/* Avatar */
.comment-avatar {
  width: 40px;
  height: 40px;
  min-width: 40px;
  border-radius: 50%;
  background: var(--bil-gradient, linear-gradient(135deg, #00a1d6, #fb7299));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  user-select: none;
}

.comment-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.comment-avatar.small {
  width: 30px;
  height: 30px;
  min-width: 30px;
  font-size: 13px;
}

/* Comment thread */
.comment-thread {
  border-top: 1px solid var(--bil-border);
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
}

.reply-item {
  padding: 8px 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 4px;
}

.comment-username {
  font-weight: 600;
  font-size: 13px;
  color: var(--bil-primary, #00a1d6);
}

.up-badge {
  flex-shrink: 0;
  margin-left: -4px;
  padding: 0 6px;
  border-radius: 4px;
  background: var(--bil-pink);
  color: #fff;
  font-size: 11px;
  line-height: 16px;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: var(--bil-muted);
}

.comment-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: var(--bil-text);
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 12px;
  margin-top: 6px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 12px;
  color: var(--bil-muted);
  cursor: pointer;
  padding: 0;
}

.action-btn:hover {
  color: var(--bil-primary, #00a1d6);
}

.action-btn.danger:hover {
  color: var(--bil-pink);
}

/* Reply input */
.reply-input-wrap {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reply-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.cancel-btn {
  background: none;
  border: 1px solid var(--bil-border);
  border-radius: 6px;
  padding: 5px 14px;
  font-size: 13px;
  color: var(--bil-muted);
  cursor: pointer;
}

.cancel-btn:hover {
  background: var(--bil-border);
}

.submit-btn {
  background: var(--bil-gradient, linear-gradient(135deg, #00a1d6, #fb7299));
  border: none;
  border-radius: 6px;
  padding: 5px 14px;
  font-size: 13px;
  color: #fff;
  cursor: pointer;
}

.submit-btn:hover {
  opacity: 0.9;
}

/* Replies section */
.replies-section {
  padding-left: 52px;
}

.load-replies-btn {
  background: none;
  border: none;
  color: var(--bil-primary, #00a1d6);
  font-size: 13px;
  cursor: pointer;
  padding: 6px 0;
  margin-bottom: 4px;
}

.load-replies-btn:hover {
  text-decoration: underline;
}

.replies-list {
  padding-bottom: 4px;
}

@media (max-width: 768px) {
  .comment-list {
    padding: 14px;
  }

  .replies-section {
    padding-left: 42px;
  }

  .post-row {
    gap: 8px;
  }
}
</style>
