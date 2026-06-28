import { beforeEach, describe, expect, it, vi } from 'vitest'
import router from '@/router'
import { eventBus } from '@/utils/eventBus'

describe('router auth guard', () => {
  beforeEach(async () => {
    localStorage.clear()
    await router.push('/')
  })

  it('emits auth:required and redirects home when /creator is opened without a token', async () => {
    const authRequired = vi.fn()
    eventBus.on('auth:required', authRequired)

    try {
      await router.push('/creator')

      expect(authRequired).toHaveBeenCalledTimes(1)
      expect(router.currentRoute.value.name).toBe('home')
      expect(router.currentRoute.value.fullPath).toBe('/')
    } finally {
      eventBus.off('auth:required', authRequired)
    }
  })
})
