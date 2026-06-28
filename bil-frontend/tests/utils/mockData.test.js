import { describe, expect, it } from 'vitest'
import { mockVideos } from '@/utils/mockData'

describe('mockVideos', () => {
  it('provides rich fallback video data for later playback and interactions', () => {
    expect(mockVideos.length).toBeGreaterThan(0)
    for (const video of mockVideos) {
      expect(video.videoId).toBeTruthy()
      expect(video.videoName).toBeTruthy()
      expect(video.videoCover.startsWith('data:image/svg+xml')).toBe(true)
      expect(video.videoUrl).toBeTruthy()
      expect(typeof video.likeCount).toBe('number')
      expect(typeof video.coinCount).toBe('number')
      expect(typeof video.collectCount).toBe('number')
    }
  })
})
