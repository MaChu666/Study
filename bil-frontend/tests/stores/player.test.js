import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { usePlayerStore } from '@/stores/player'
import { eventBus } from '@/utils/eventBus'

describe('player store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    eventBus.all.clear()
  })

  it('plays a video and keeps it in the queue once', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    expect(store.current.videoId).toBe('BV1')
    expect(store.isPlaying).toBe(true)
    expect(store.queue).toHaveLength(1)
  })

  it('emits a play event when playing a video', () => {
    const events = []
    const video = { videoId: 'BV1', videoName: 'Demo' }
    eventBus.on('player:play-video', (payload) => events.push(payload))

    const store = usePlayerStore()
    store.play(video)

    expect(events).toEqual([video])
  })

  it('removes the current video and clears playback', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    store.removeFromQueue('BV1')
    expect(store.current).toBe(null)
    expect(store.isPlaying).toBe(false)
  })

  it('emits state-change events when removing current playback', () => {
    const queueEvents = []
    const playbackEvents = []
    eventBus.on('player:queue-changed', (payload) => queueEvents.push(payload))
    eventBus.on('player:playback-changed', (payload) => playbackEvents.push(payload))

    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    queueEvents.length = 0
    playbackEvents.length = 0

    store.removeFromQueue('BV1')

    expect(queueEvents).toEqual([[]])
    expect(playbackEvents).toEqual([{ current: null, isPlaying: false }])
  })

  it('persists and restores the current queue across refreshes', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo', videoUrl: '/demo.mp4' })
    setActivePinia(createPinia())
    const restored = usePlayerStore()
    expect(restored.current.videoId).toBe('BV1')
    expect(restored.queue).toHaveLength(1)
  })
})
