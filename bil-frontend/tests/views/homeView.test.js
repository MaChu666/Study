import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import HomeView from '@/views/HomeView.vue'
import { eventBus } from '@/utils/eventBus'
import { mockVideos } from '@/utils/mockData'

const { loadRecommendVideoApi, loadVideoApi } = vi.hoisted(() => ({
  loadRecommendVideoApi: vi.fn(),
  loadVideoApi: vi.fn()
}))

vi.mock('@/api/modules/video', () => ({
  loadRecommendVideoApi,
  loadVideoApi
}))

function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', name: 'home', component: HomeView },
      { path: '/video/:videoId', name: 'video-detail', component: { template: '<div />' } }
    ]
  })
}

async function flushWork() {
  await Promise.resolve()
  await Promise.resolve()
  await new Promise((resolve) => setTimeout(resolve, 0))
}

describe('HomeView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    loadRecommendVideoApi.mockReset()
    loadVideoApi.mockReset()
  })

  it('loads category videos from route.query.pCategoryId and falls back to mock data', async () => {
    const router = createTestRouter()
    loadVideoApi.mockResolvedValue([])
    await router.push({ name: 'home', query: { pCategoryId: '42' } })
    await router.isReady()

    const wrapper = mount(HomeView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          ElButton: { template: '<button type="button"><slot /></button>' },
          ElSkeleton: { template: '<div class="skeleton" />' }
        }
      }
    })

    await Promise.resolve()
    await Promise.resolve()

    expect(loadVideoApi).toHaveBeenCalledWith({ pCategoryId: '42', categoryId: '0', pageNo: 1 })
    expect(loadRecommendVideoApi).not.toHaveBeenCalled()
    expect(wrapper.text()).toContain(mockVideos[0].videoName)
  })

  it('reloads when the category query changes', async () => {
    const router = createTestRouter()
    loadRecommendVideoApi.mockResolvedValue([{ videoId: 'BV2001', videoName: 'Recommended', videoCover: 'cover', userName: 'UP', playCount: 1, danmuCount: 2, duration: '00:12' }])
    loadVideoApi.mockResolvedValue([{ videoId: 'BV2002', videoName: 'Category Pick', videoCover: 'cover', userName: 'UP', playCount: 3, danmuCount: 4, duration: '00:34' }])
    await router.push({ name: 'home' })
    await router.isReady()

    mount(HomeView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          ElButton: { template: '<button type="button"><slot /></button>' },
          ElSkeleton: { template: '<div class="skeleton" />' }
        }
      }
    })

    await Promise.resolve()
    await router.push({ name: 'home', query: { pCategoryId: '9' } })
    await Promise.resolve()
    await Promise.resolve()

    expect(loadRecommendVideoApi).toHaveBeenCalledTimes(1)
    expect(loadVideoApi).toHaveBeenCalledWith({ pCategoryId: '9', categoryId: '0', pageNo: 1 })
  })

  it('plays through the global player event bus when a video card is selected', async () => {
    const router = createTestRouter()
    const played = vi.fn()
    loadRecommendVideoApi.mockResolvedValue([])
    await router.push({ name: 'home' })
    await router.isReady()
    eventBus.on('player:play-video', played)

    try {
      const wrapper = mount(HomeView, {
        global: {
          plugins: [createPinia(), router],
          stubs: {
            ElButton: { template: '<button type="button"><slot /></button>' },
            ElSkeleton: { template: '<div class="skeleton" />' }
          }
        }
      })

      await flushWork()
      await wrapper.get('.video-card').trigger('click')
      await flushWork()

      expect(played).toHaveBeenCalledWith(mockVideos[0])
      expect(router.currentRoute.value.name).toBe('video-detail')
    } finally {
      eventBus.off('player:play-video', played)
    }
  })
})
