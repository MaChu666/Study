import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import VideoDetailView from '@/views/VideoDetailView.vue'
import { mockVideos } from '@/utils/mockData'

const { getVideoInfoApi, loadVideoPListApi, getVideoRecommendApi } = vi.hoisted(() => ({
  getVideoInfoApi: vi.fn(),
  loadVideoPListApi: vi.fn(),
  getVideoRecommendApi: vi.fn()
}))

vi.mock('@/api/modules/video', () => ({
  getVideoInfoApi,
  loadVideoPListApi,
  getVideoRecommendApi
}))

function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/video/:videoId', name: 'video-detail', component: VideoDetailView }
    ]
  })
}

async function flushWork() {
  await Promise.resolve()
  await Promise.resolve()
  await new Promise((resolve) => setTimeout(resolve, 0))
}

describe('VideoDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    getVideoInfoApi.mockReset()
    loadVideoPListApi.mockReset()
    getVideoRecommendApi.mockReset()
  })

  it('binds the playable video src from videoUrl when detail data provides it', async () => {
    const router = createTestRouter()
    getVideoInfoApi.mockResolvedValue({
      ...mockVideos[0],
      videoId: 'BV3001',
      videoUrl: 'https://cdn.example.com/video.mp4'
    })
    loadVideoPListApi.mockResolvedValue([])
    getVideoRecommendApi.mockResolvedValue([])

    await router.push({ name: 'video-detail', params: { videoId: 'BV3001' } })
    await router.isReady()

    const wrapper = mount(VideoDetailView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          CommentList: { template: '<div class="comment-list-stub" />' },
          DanmuPanel: { template: '<div class="danmu-panel-stub" />' },
          InteractionBar: { template: '<div class="interaction-bar-stub" />' },
          VideoCard: { template: '<div class="video-card-stub" />' },
          ElInput: { template: '<input />' },
          ElButton: { template: '<button type="button"><slot /></button>' }
        }
      }
    })

    await flushWork()

    expect(wrapper.get('video').attributes('src')).toBe('https://cdn.example.com/video.mp4')
  })

  it('falls back to playlist filePath when videoUrl is absent', async () => {
    const router = createTestRouter()
    getVideoInfoApi.mockResolvedValue({
      videoId: 'BV3002',
      videoName: 'Playlist-backed video',
      videoCover: 'cover',
      likeCount: 4,
      collectCount: 5,
      coinCount: 6
    })
    loadVideoPListApi.mockResolvedValue([{ fileId: 'FILE-1', filePath: 'https://cdn.example.com/from-playlist.mp4' }])
    getVideoRecommendApi.mockResolvedValue([])

    await router.push({ name: 'video-detail', params: { videoId: 'BV3002' } })
    await router.isReady()

    const wrapper = mount(VideoDetailView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          CommentList: { template: '<div class="comment-list-stub" />' },
          DanmuPanel: { template: '<div class="danmu-panel-stub" />' },
          InteractionBar: { template: '<div class="interaction-bar-stub" />' },
          VideoCard: { template: '<div class="video-card-stub" />' },
          ElInput: { template: '<input />' },
          ElButton: { template: '<button type="button"><slot /></button>' }
        }
      }
    })

    await flushWork()

    expect(wrapper.get('video').attributes('src')).toBe('https://cdn.example.com/from-playlist.mp4')
  })

  it('does not use the backend JSON file resource endpoint as a media src', async () => {
    const router = createTestRouter()
    getVideoInfoApi.mockResolvedValue({
      videoId: 'BV3003',
      videoName: 'File id only video',
      videoCover: 'cover'
    })
    loadVideoPListApi.mockResolvedValue([{ fileId: 'FILE-2' }])
    getVideoRecommendApi.mockResolvedValue([])

    await router.push({ name: 'video-detail', params: { videoId: 'BV3003' } })
    await router.isReady()

    const wrapper = mount(VideoDetailView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          CommentList: { template: '<div class="comment-list-stub" />' },
          DanmuPanel: { template: '<div class="danmu-panel-stub" />' },
          InteractionBar: { template: '<div class="interaction-bar-stub" />' },
          VideoCard: { template: '<div class="video-card-stub" />' },
          ElInput: { template: '<input />' },
          ElButton: { template: '<button type="button"><slot /></button>' }
        }
      }
    })

    await flushWork()

    expect(wrapper.get('video').attributes('src')).toBe(mockVideos[0].videoUrl)
  })
})
