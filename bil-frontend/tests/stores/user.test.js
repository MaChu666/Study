import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { useUserStore } from '@/stores/user'

vi.mock('@/api/modules/account', () => ({
  loginApi: vi.fn(async () => ({ token: 'login-token', userId: 'U1', useName: 'Tester' })),
  registerApi: vi.fn(async () => null),
  logoutApi: vi.fn(async () => null),
  autoLoginApi: vi.fn(async () => ({ token: 'auto-token', userId: 'U2', useName: 'Auto' }))
}))

describe('user store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('stores token and profile after login', async () => {
    const store = useUserStore()
    await store.login({ email: 'a@test.com', password: 'abc123' })
    expect(store.isLogin).toBe(true)
    expect(store.token).toBe('login-token')
    expect(store.profile.useName).toBe('Tester')
  })

  it('clears state on logout', async () => {
    const store = useUserStore()
    await store.login({ email: 'a@test.com', password: 'abc123' })
    await store.logout()
    expect(store.isLogin).toBe(false)
    expect(store.token).toBe('')
  })

  it('restores profile state through auto login when a token exists', async () => {
    localStorage.setItem('bil-token', 'saved-token')
    const store = useUserStore()
    await store.autoLogin()
    expect(store.isLogin).toBe(true)
    expect(store.profile.useName).toBe('Auto')
    expect(store.token).toBe('auto-token')
  })
})
