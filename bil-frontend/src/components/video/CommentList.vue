<template>
  <section class="comment-list">
    <h2>评论</h2>
    <div class="post-row">
      <el-input v-model="content" type="textarea" :rows="3" aria-label="写评论" />
      <el-button class="bil-gradient-button" @click="post">发布</el-button>
    </div>
    <article v-for="item in comments" :key="item.commentId || item.content" class="comment-item">
      <strong>{{ item.userName || 'BilBil 用户' }}</strong>
      <p>{{ item.content }}</p>
    </article>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { loadCommentApi, postCommentApi } from '@/api/modules/comment'

const props = defineProps({
  videoId: { type: String, required: true }
})

const fallbackComments = [{ userName: '路过的观众', content: '这个视频值得三连。' }]

const content = ref('')
const comments = ref([...fallbackComments])

async function load() {
  try {
    const data = await loadCommentApi({ videoId: props.videoId, pageNo: 1, orderType: 0 })
    if (Array.isArray(data?.list) && data.list.length) {
      comments.value = data.list
      return
    }
    if (Array.isArray(data) && data.length) {
      comments.value = data
      return
    }
    comments.value = [...fallbackComments]
  } catch {
    comments.value = [...fallbackComments]
  }
}

async function post() {
  const nextContent = content.value.trim()
  if (!nextContent) return
  await postCommentApi({ videoId: props.videoId, content: nextContent, replyCommentId: '', imgPath: '' })
  comments.value.unshift({ userName: '我', content: nextContent })
  content.value = ''
}

watch(
  () => props.videoId,
  () => {
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

.post-row {
  display: grid;
  gap: 10px;
  margin-bottom: 18px;
}

.comment-item {
  padding: 14px 0;
  border-top: 1px solid var(--bil-border);
}

.comment-item p {
  margin: 6px 0 0;
  color: var(--bil-muted);
}
</style>
