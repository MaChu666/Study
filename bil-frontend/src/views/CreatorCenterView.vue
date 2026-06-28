<template>
  <section class="creator-center">
    <div class="dashboard">
      <div class="dashboard-copy">
        <h1>创作中心</h1>
        <p>管理投稿、查看状态，并继续发布新的内容灵感。</p>
      </div>
      <el-button class="bil-gradient-button" @click="submitVideo">保存投稿草稿</el-button>
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

      <div class="form-grid">
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="例如：动画、音乐、科技" />
        </el-form-item>
        <el-form-item label="投稿分区">
          <el-input v-model="form.categoryId" placeholder="先保留默认分区" />
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
      <span>{{ videos.length }} 个稿件</span>
    </div>

    <div class="video-grid">
      <VideoCard v-for="video in videos" :key="video.videoId" :video="video" @play="openVideo" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import VideoCard from '@/components/video/VideoCard.vue'
import VideoUploader from '@/components/video/VideoUploader.vue'
import ImageUploader from '@/components/video/ImageUploader.vue'
import { loadCreatorVideoListApi, postVideoApi } from '@/api/modules/user'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'
import { normalizeVideoList } from '@/utils/videoList'

const router = useRouter()
const playerStore = usePlayerStore()
const uploaderRef = ref(null)
const coverUploaderRef = ref(null)
const videos = ref(mockVideos)
const uploadedFileIdList = ref([])
const form = reactive({
  videoCover: '',
  videoName: '',
  pCategoryId: '1',
  categoryId: '1',
  postType: '0',
  tags: '',
  introduction: '',
  interaction: '1',
  uploadFileList: ''
})

async function loadVideos() {
  try {
    const data = await loadCreatorVideoListApi({ status: 0, pageNo: 1 })
    const normalized = normalizeVideoList(data)
    videos.value = normalized.length ? normalized : mockVideos
  } catch {
    videos.value = mockVideos
  }
}

function onCoverUploaded(fileId) {
  form.videoCover = fileId
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
  try {
    const payload = { ...form, uploadFileList: uploadedFileIdList.value.join(',') }
    await postVideoApi(payload)
    ElMessage.success('投稿已发布')
    uploadedFileIdList.value = []
    uploaderRef.value?.reset()
    await loadVideos()
  } catch {
    ElMessage.error('保存失败，请稍后再试')
  }
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(loadVideos)
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

@media (max-width: 768px) {
  .dashboard,
  .section-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
