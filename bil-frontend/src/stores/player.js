import { defineStore } from 'pinia'
import { eventBus } from '@/utils/eventBus'

const PLAYER_KEY = 'bil-player-state'

function loadPlayerState() {
  try {
    return JSON.parse(localStorage.getItem(PLAYER_KEY) || '{}')
  } catch {
    return {}
  }
}

function savePlayerState(state) {
  localStorage.setItem(PLAYER_KEY, JSON.stringify({
    queue: state.queue,
    current: state.current,
    currentTime: state.currentTime,
    muted: state.muted,
    volume: state.volume
  }))
}

function emitQueueChanged(queue) {
  eventBus.emit('player:queue-changed', [...queue])
}

function emitPlaybackChanged(state) {
  eventBus.emit('player:playback-changed', {
    current: state.current,
    isPlaying: state.isPlaying
  })
}

export const usePlayerStore = defineStore('player', {
  state: () => {
    const saved = loadPlayerState()
    return {
      queue: saved.queue || [],
      current: saved.current || null,
      isPlaying: false,
      currentTime: saved.currentTime || 0,
      muted: saved.muted || false,
      volume: saved.volume ?? 0.8
    }
  },
  actions: {
    play(video) {
      this.current = video
      this.isPlaying = true
      this.enqueue(video)
      savePlayerState(this)
      eventBus.emit('player:play-video', video)
      emitPlaybackChanged(this)
    },
    enqueue(video) {
      if (!video?.videoId) return
      const exists = this.queue.some((item) => item.videoId === video.videoId)
      if (!exists) {
        this.queue.push(video)
        savePlayerState(this)
        emitQueueChanged(this.queue)
      }
    },
    removeFromQueue(videoId) {
      const previousLength = this.queue.length
      this.queue = this.queue.filter((item) => item.videoId !== videoId)
      const queueChanged = this.queue.length !== previousLength
      let playbackChanged = false
      if (this.current?.videoId === videoId) {
        this.current = this.queue[0] || null
        this.isPlaying = Boolean(this.current)
        playbackChanged = true
      }
      savePlayerState(this)
      if (queueChanged) {
        emitQueueChanged(this.queue)
      }
      if (playbackChanged) {
        emitPlaybackChanged(this)
      }
    },
    togglePlay() {
      if (this.current) {
        this.isPlaying = !this.isPlaying
        emitPlaybackChanged(this)
      }
    },
    clearQueue() {
      const hadQueue = this.queue.length > 0
      const hadPlayback = Boolean(this.current) || this.isPlaying
      this.queue = []
      this.current = null
      this.isPlaying = false
      savePlayerState(this)
      if (hadQueue) {
        emitQueueChanged(this.queue)
      }
      if (hadPlayback) {
        emitPlaybackChanged(this)
      }
    }
  }
})
