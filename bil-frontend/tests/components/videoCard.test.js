import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import VideoCard from '@/components/video/VideoCard.vue'
import { mockVideos } from '@/utils/mockData'

describe('VideoCard', () => {
  it('emits play with its video when selected', async () => {
    const wrapper = mount(VideoCard, {
      props: {
        video: mockVideos[0]
      }
    })

    await wrapper.trigger('click')

    expect(wrapper.emitted('play')?.[0]).toEqual([mockVideos[0]])
  })
})
