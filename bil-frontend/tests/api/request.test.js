import { describe, expect, it, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { getToken, setToken } from '@/utils/token'

vi.mock('axios', () => {
  const instance = {
    interceptors: {
      request: { use: vi.fn((handler) => { instance.requestHandler = handler }) },
      response: { use: vi.fn((success, failure) => { instance.responseSuccess = success; instance.responseFailure = failure }) }
    }
  }
  return {
    default: {
      create: vi.fn(() => instance)
    }
  }
})

describe('request layer', () => {
  beforeEach(() => {
    vi.resetModules()
    localStorage.clear()
  })

  it('stores and reads the token used by request interceptors', async () => {
    setToken('abc-token')
    expect(getToken()).toBe('abc-token')
    await import('@/api/request')
    const instance = axios.create.mock.results[0].value
    const config = instance.requestHandler({ headers: {} })
    expect(config.headers.thoken).toBe('abc-token')
  })

  it('serializes plain POST data as form params for Spring controllers', async () => {
    await import('@/api/request')
    const instance = axios.create.mock.results[0].value
    const config = instance.requestHandler({
      method: 'post',
      data: { email: 'a@test.com', pageNo: 1 },
      headers: {}
    })

    expect(config.data).toBeInstanceOf(URLSearchParams)
    expect(config.data.get('email')).toBe('a@test.com')
    expect(config.data.get('pageNo')).toBe('1')
    expect(config.headers['Content-Type']).toBe('application/x-www-form-urlencoded;charset=UTF-8')
  })

  it('unwraps backend success responses', async () => {
    await import('@/api/request')
    const instance = axios.create.mock.results[0].value
    const result = instance.responseSuccess({ data: { code: 200, data: { ok: true }, info: 'success' } })
    expect(result).toEqual({ ok: true })
  })

  it('emits auth required for backend login-required business errors', async () => {
    setToken('expired-token')
    const { eventBus } = await import('@/utils/eventBus')
    const authRequired = vi.fn()
    eventBus.on('auth:required', authRequired)

    try {
      await import('@/api/request')
      const instance = axios.create.mock.results[0].value
      await expect(instance.responseSuccess({ status: 200, data: { code: 600, info: '请先登录' } })).rejects.toThrow('请先登录')

      expect(getToken()).toBe('')
      expect(authRequired).toHaveBeenCalledTimes(1)
    } finally {
      eventBus.off('auth:required', authRequired)
    }
  })
})
