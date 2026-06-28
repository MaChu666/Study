import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import SearchView from '@/views/SearchView.vue'
import { eventBus } from '@/utils/eventBus'
import { mockVideos } from '@/utils/mockData'

const { getSearchKeywordTopApi, searchVideoApi } = vi.hoisted(() => ({
  getSearchKeywordTopApi: vi.fn(),
  searchVideoApi: vi.fn()
}))

vi.mock('@/api/modules/video', () => ({
  getSearchKeywordTopApi,
  searchVideoApi
}))

function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/search', name: 'search', component: SearchView },
      { path: '/video/:videoId', name: 'video-detail', component: { template: '<div />' } }
    ]
  })
}

async function flushWork() {
  await Promise.resolve()
  await Promise.resolve()
  await new Promise((resolve) => setTimeout(resolve, 0))
}

describe('SearchView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    getSearchKeywordTopApi.mockReset()
    searchVideoApi.mockReset()
  })

  it('loads hot keywords and runs an initial query-backed search', async () => {
    const router = createTestRouter()
    getSearchKeywordTopApi.mockResolvedValue(['games', 'music'])
    searchVideoApi.mockResolvedValue([])
    await router.push({ name: 'search', query: { keyword: 'games' } })
    await router.isReady()

    const wrapper = mount(SearchView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          ElButton: { template: '<button type="button"><slot /></button>' },
          ElInput: {
            props: ['modelValue'],
            emits: ['update:modelValue'],
            template: '<input :value="modelValue" @input="$emit(`update:modelValue`, $event.target.value)" />'
          }
        }
      }
    })

    await flushWork()

    expect(getSearchKeywordTopApi).toHaveBeenCalledTimes(1)
    expect(searchVideoApi).toHaveBeenCalledWith({ keyword: 'games' })
    expect(wrapper.text()).toContain(mockVideos[0].videoName)
  })

  it('uses a hot keyword button to update the route and search again', async () => {
    const router = createTestRouter()
    getSearchKeywordTopApi.mockResolvedValue(['games', 'music'])
    searchVideoApi.mockResolvedValue([{ videoId: 'BV3001', videoName: 'Games Result', videoCover: 'cover', userName: 'UP', playCount: 1, danmuCount: 2, duration: '01:00' }])
    await router.push({ name: 'search' })
    await router.isReady()

    const wrapper = mount(SearchView, {
      global: {
        plugins: [createPinia(), router],
        stubs: {
          ElButton: { template: '<button type="button"><slot /></button>' },
          ElInput: {
            props: ['modelValue'],
            emits: ['update:modelValue'],
            template: '<input :value="modelValue" @input="$emit(`update:modelValue`, $event.target.value)" />'
          }
        }
      }
    })

    await flushWork()
    await wrapper.get('.hotwords button').trigger('click')
    await flushWork()

    expect(router.currentRoute.value.query.keyword).toBe('games')
    expect(searchVideoApi).toHaveBeenLastCalledWith({ keyword: 'games' })
    expect(wrapper.text()).toContain('Games Result')
  })

  it('plays through the global player event bus when a search result is selected', async () => {
    const router = createTestRouter()
    const played = vi.fn()
    getSearchKeywordTopApi.mockResolvedValue(['游戏'])
    searchVideoApi.mockResolvedValue([])
    await router.push({ name: 'search' })
    await router.isReady()
    eventBus.on('player:play-video', played)

    try {
      const wrapper = mount(SearchView, {
        global: {
          plugins: [createPinia(), router],
          stubs: {
            ElButton: { template: '<button type="button"><slot /></button>' },
            ElInput: {
              props: ['modelValue'],
              emits: ['update:modelValue'],
              template: '<input :value="modelValue" @input="$emit(`update:modelValue`, $event.target.value)" />'
            }
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
