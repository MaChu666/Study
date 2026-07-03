<template>
  <section class="creator-center">
    <div class="dashboard">
      <div class="dashboard-copy">
        <h1>创作中心</h1>
        <p>管理投稿、查看状态，并继续发布新的内容灵感。</p>
      </div>
      <el-button class="bil-gradient-button" :loading="submitting" :disabled="submitting" @click="submitVideo">
        {{ submitting ? '保存中...' : '保存投稿草稿' }}
      </el-button>
    </div>

    <el-form :model="form" label-position="top" class="publish-form">
      <div class="form-grid">
        <el-form-item label="视频封面">
          <ImageUploader ref="coverUploaderRef" @uploaded="onCoverUploaded" @removed="onCoverRemoved" />
        </el-form-item>
        <el-form-item label="视频标题">
          <el-input v-model="form.videoName" placeholder="给你的作品起个标题" />
        </el-form-item>
      </div>

      <el-form-item label="标签">
        <el-input v-model="form.tags" placeholder="例如：动画、音乐、科技" />
      </el-form-item>

      <div class="form-grid">
        <el-form-item label="发布类型">
          <el-select v-model="form.postType">
            <el-option value="1" label="自制" />
            <el-option value="2" label="转载" />
          </el-select>
        </el-form-item>
        <el-form-item label="分区">
          <el-select v-model="form.pCategoryId" placeholder="选择分区" @change="form.categoryId = form.pCategoryId">
            <el-option v-for="cat in parentCategories" :key="cat.id" :label="cat.name" :value="String(cat.id)" />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="简介">
        <el-input
          v-model="form.introduction"
          type="textarea"
          :rows="4"
          placeholder="写点你想告诉观众的话"
        />
      </el-form-item>
    </el-form>

    <div class="publish-form">
      <h3 style="margin:0 0 8px 0;font-size:15px">视频文件</h3>
      <VideoUploader
        ref="uploaderRef"
        @uploaded="onFileUploaded"
        @removed="onFileRemoved"
      />
    </div>

    <div class="section-head">
      <h2>我的投稿</h2>
      <div style="display:flex;align-items:center;gap:8px">
        <el-select v-model="videoStatus" size="small" @change="loadVideos" style="width:120px">
          <el-option :value="-1" label="全部" />
          <el-option :value="0" label="审核中" />
          <el-option :value="1" label="已通过" />
          <el-option :value="2" label="已驳回" />
        </el-select>
        <span>{{ videos.length }} 个稿件</span>
      </div>
    </div>

    <div class="video-grid">
      <div v-for="video in videos" :key="video.videoId" class="video-card-wrap">
        <el-tag
          :type="statusTagType(video.status)"
          size="small"
          class="status-badge"
        >
          {{ statusText(video.status) }}
        </el-tag>
        <VideoCard :video="video" @play="openVideo" />
        <el-button class="delete-btn" text type="danger" size="small" @click.stop="handleDelete(video.videoId)">删除</el-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import VideoCard from '@/components/video/VideoCard.vue'
import VideoUploader from '@/components/video/VideoUploader.vue'
import ImageUploader from '@/components/video/ImageUploader.vue'
import { loadCreatorVideoListApi, postVideoApi } from '@/api/modules/user'
import { deleteMyVideoApi } from '@/api/modules/user'
import { usePlayerStore } from '@/stores/player'
import { normalizeVideoList } from '@/utils/videoList'

const router = useRouter()
const playerStore = usePlayerStore()
const uploaderRef = ref(null)
const coverUploaderRef = ref(null)
const videos = ref([])
const uploadedFileIdList = ref([])
const form = reactive({
  videoCover: '',
  videoName: '',
  pCategoryId: '1',
  categoryId: '1',
  postType: '1',
  tags: '',
  introduction: '',
  interaction: '1',
  uploadFileList: ''
})

const parentCategories = [
  { id: 0, name: '全部' },
  { id: 1, name: '动画' },
  { id: 2, name: '音乐' },
  { id: 3, name: '游戏' },
  { id: 4, name: '知识' },
  { id: 5, name: '科技' },
  { id: 6, name: '运动' },
  { id: 7, name: '生活' },
  { id: 8, name: '电影' },
  { id: 9, name: '电视剧' },
  { id: 10, name: '纪录片' }
]

const videoStatus = ref(-1)
const submitting = ref(false)

async function loadVideos() {
  try {
    const params = { pageNo: 1 }
    if (videoStatus.value >= 0) params.status = videoStatus.value
    const data = await loadCreatorVideoListApi(params)
    videos.value = normalizeVideoList(data)
  } catch {
    videos.value = []
  }
}

function onCoverUploaded(coverPath) {
  form.videoCover = coverPath
}

function onCoverRemoved() {
  form.videoCover = ''
}

function onFileUploaded(fileId) {
  if (!uploadedFileIdList.value.includes(fileId)) {
    uploadedFileIdList.value.push(fileId)
  }
}

function onFileRemoved() {
  uploadedFileIdList.value = []
}

async function submitVideo() {
  if (uploadedFileIdList.value.length === 0) {
    ElMessage.warning('请先上传视频文件')
    return
  }
  submitting.value = true
  try {
    const payload = { ...form, uploadFileList: uploadedFileIdList.value.join(',') }
    await postVideoApi(payload)
    ElMessage.success('投稿已发布')
    Object.assign(form, {
      videoCover: '',
      videoName: '',
      pCategoryId: '1',
      categoryId: '1',
      postType: '1',
      tags: '',
      introduction: '',
      interaction: '1',
      uploadFileList: ''
    })
    uploadedFileIdList.value = []
    uploaderRef.value?.reset()
    coverUploaderRef.value?.reset()
    await loadVideos()
  } catch {
    ElMessage.error('保存失败，请稍后再试')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(videoId) {
  try {
    await deleteMyVideoApi({ videoId })
    ElMessage.success('已删除')
    loadVideos()
  } catch {
    ElMessage.error('删除失败')
  }
}

function statusTagType(status) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

function statusText(status) {
  const map = { 0: '审核中', 1: '已通过', 2: '已驳回' }
  return map[status] || '未知'
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(() => { loadVideos() })
</script>

<style scoped>
.creator-center {
  display: grid;
  gap: 18px;
}

.dashboard,
.publish-form {
  border-radius: 12px;
  padding: 22px;
  background: var(--bil-surface);
}

.dashboard {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.dashboard-copy,
.section-head {
  min-width: 0;
}

.dashboard-copy h1,
.section-head h2,
.dashboard-copy p {
  margin: 0;
}

.dashboard-copy p {
  margin-top: 6px;
  color: var(--bil-muted);
  line-height: 1.6;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}

.section-head span {
  color: var(--bil-muted);
  font-size: 14px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 22px;
}

.video-card-wrap {
  position: relative;
}

.status-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

@media (max-width: 768px) {
  .creator-center {
    gap: 12px;
  }

  .dashboard,
  .publish-form {
    padding: 16px;
  }

  .dashboard {
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
  }

  .dashboard .bil-gradient-button {
    width: 100%;
  }

  .section-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 6px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .publish-form :deep(.el-form-item) {
    width: 100%;
  }

  .publish-form :deep(.el-input),
  .publish-form :deep(.el-select) {
    width: 100%;
  }

  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
}
</style>
