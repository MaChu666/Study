import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import CreatorCenterView from '@/views/CreatorCenterView.vue'
import { eventBus } from '@/utils/eventBus'
import { mockVideos } from '@/utils/mockData'

const { loadCreatorVideoListApi, postVideoApi } = vi.hoisted(() => ({
  loadCreatorVideoListApi: vi.fn(),
  postVideoApi: vi.fn()
}))

vi.mock('@/api/modules/user', () => ({
  loadCreatorVideoListApi,
  postVideoApi
}))

function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/creator', name: 'creator', component: CreatorCenterView },
      { path: '/video/:videoId', name: 'video-detail', component: { template: '<div />' } }
    ]
  })
}

async function flushWork() {
  await Promise.resolve()
  await Promise.resolve()
  await new Promise((resolve) => setTimeout(resolve, 0))
}

function mountView(router) {
  return mount(CreatorCenterView, {
    global: {
      plugins: [createPinia(), router],
      stubs: {
        ElButton: { template: '<button type="button"><slot /></button>' },
        ElForm: { template: '<form><slot /></form>' },
        ElFormItem: { template: '<label><slot /></label>' },
        ElInput: { template: '<input />' }
      }
    }
  })
}

describe('CreatorCenterView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    loadCreatorVideoListApi.mockReset()
    postVideoApi.mockReset()
  })

  it('opens creator videos through the global player and detail route', async () => {
    const router = createTestRouter()
    const played = vi.fn()
    loadCreatorVideoListApi.mockResolvedValue([])
    await router.push({ name: 'creator' })
    await router.isReady()
    eventBus.on('player:play-video', played)

    try {
      const wrapper = mountView(router)
      await flushWork()
      await wrapper.get('.video-card').trigger('click')
      await flushWork()

      expect(played).toHaveBeenCalledWith(mockVideos[0])
      expect(router.currentRoute.value.name).toBe('video-detail')
    } finally {
      eventBus.off('player:play-video', played)
    }
  })

  it('handles publish failures without rethrowing', async () => {
    const router = createTestRouter()
    loadCreatorVideoListApi.mockResolvedValue([])
    postVideoApi.mockRejectedValue(new Error('save failed'))
    await router.push({ name: 'creator' })
    await router.isReady()

    const wrapper = mountView(router)
    await flushWork()
    await wrapper.get('.dashboard button').trigger('click')
    await flushWork()

    expect(postVideoApi).toHaveBeenCalledTimes(1)
    expect(router.currentRoute.value.name).toBe('creator')
  })
})
