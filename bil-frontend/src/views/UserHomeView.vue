<template>
  <section class="user-home">
    <div class="profile-band">
      <el-avatar :size="72" class="profile-avatar">
        {{ profile.useName?.slice(0, 1) || 'B' }}
      </el-avatar>
      <div class="profile-copy">
        <h1>{{ profile.useName || 'BilBil UP主' }}</h1>
        <p>{{ profile.introduction || '这个人还没有写简介' }}</p>
      </div>
    </div>

    <div class="section-head">
      <h2>投稿视频</h2>
      <span>{{ videos.length }} 个作品</span>
    </div>

    <div class="video-grid">
      <VideoCard
        v-for="video in videos"
        :key="video.videoId"
        :video="video"
        @play="openVideo"
      />
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import VideoCard from '@/components/video/VideoCard.vue'
import { getUserInfoApi, loadUserVideoListApi } from '@/api/modules/user'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'
import { normalizeVideoList } from '@/utils/videoList'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()

const profile = ref({})
const videos = ref(mockVideos)

async function load() {
  const userId = route.params.userId || ''

  try {
    profile.value = await getUserInfoApi({ userId })
  } catch {
    profile.value = {
      useName: 'BilBil UP主',
      introduction: '分享热爱的内容创作'
    }
  }

  try {
    const data = await loadUserVideoListApi({ userId, pageNo: 1 })
    const normalized = normalizeVideoList(data)
    videos.value = normalized.length ? normalized : mockVideos
  } catch {
    videos.value = mockVideos
  }
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(load)
</script>

<style scoped>
.user-home {
  display: grid;
  gap: 24px;
}

.profile-band {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 28px;
  border-radius: 12px;
  background: var(--bil-gradient);
  color: #fff;
}

.profile-avatar {
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.32);
}

.profile-copy {
  min-width: 0;
}

.profile-copy h1,
.section-head h2 {
  margin: 0;
}

.profile-copy p {
  margin: 8px 0 0;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
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

@media (max-width: 640px) {
  .profile-band,
  .section-head {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
