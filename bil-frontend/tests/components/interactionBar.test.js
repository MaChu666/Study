import { mount } from '@vue/test-utils'
import { afterEach, describe, expect, it, vi } from 'vitest'
import InteractionBar from '@/components/video/InteractionBar.vue'
import { eventBus } from '@/utils/eventBus'

const { doActionApi } = vi.hoisted(() => ({
  doActionApi: vi.fn(() => Promise.resolve())
}))

vi.mock('@/api/modules/video', () => ({
  doActionApi
}))

describe('InteractionBar', () => {
  afterEach(() => {
    doActionApi.mockClear()
  })

  it('renders initial interaction counts from the video prop', () => {
    const wrapper = mount(InteractionBar, {
      props: {
        video: {
          videoId: 'BV1001',
          likeCount: 12,
          collectCount: 34,
          coinCount: 56
        }
      }
    })

    const text = wrapper.text()
    expect(text).toContain('12')
    expect(text).toContain('34')
    expect(text).toContain('56')
  })

  it('emits liked and collected events after performing actions', async () => {
    const liked = vi.fn()
    const collected = vi.fn()
    eventBus.on('video:liked', liked)
    eventBus.on('video:collected', collected)

    try {
      const wrapper = mount(InteractionBar, {
        props: {
          video: {
            videoId: 'BV2002',
            likeCount: 1,
            collectCount: 2,
            coinCount: 3
          }
        }
      })

      const buttons = wrapper.findAll('button')
      await buttons[0].trigger('click')
      await buttons[1].trigger('click')

      expect(doActionApi).toHaveBeenNthCalledWith(1, { videoId: 'BV2002', actionType: 0, actionCount: 1, commentId: 0 })
      expect(doActionApi).toHaveBeenNthCalledWith(2, { videoId: 'BV2002', actionType: 2, actionCount: 1, commentId: 0 })
      expect(liked).toHaveBeenCalledWith('BV2002')
      expect(collected).toHaveBeenCalledWith('BV2002')
    } finally {
      eventBus.off('video:liked', liked)
      eventBus.off('video:collected', collected)
    }
  })

  it('keeps counts unchanged when an action request fails', async () => {
    doActionApi.mockRejectedValueOnce(new Error('network down'))
    const wrapper = mount(InteractionBar, {
      props: {
        video: {
          videoId: 'BV3003',
          likeCount: 7,
          collectCount: 8,
          coinCount: 9
        }
      }
    })

    await wrapper.findAll('button')[0].trigger('click')
    await Promise.resolve()

    expect(wrapper.text()).toContain('点赞 7')
  })
})
