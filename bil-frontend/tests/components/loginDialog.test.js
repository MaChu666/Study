import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import LoginDialog from '@/components/auth/LoginDialog.vue'
import { useUserStore } from '@/stores/user'

const { getCheckCodeApi, loginSpy } = vi.hoisted(() => ({
  getCheckCodeApi: vi.fn(async () => ({ checkCode: 'data:image/png;base64,abc', checkCodeKey: 'KEY' })),
  loginSpy: vi.fn(async () => ({ token: 'login-token', useName: 'Tester' }))
}))

vi.mock('@/api/modules/account', async (importOriginal) => ({
  ...(await importOriginal()),
  getCheckCodeApi
}))

function mountDialog() {
  return mount(LoginDialog, {
    global: {
      plugins: [createPinia()],
      stubs: {
        ElDialog: { template: '<section><slot /></section>' },
        ElTabs: { template: '<div><slot /></div>' },
        ElTabPane: { template: '<div><slot /></div>' },
        ElForm: { template: '<form><slot /></form>' },
        ElFormItem: { template: '<label><slot /></label>' },
        ElInput: { template: '<input />' },
        ElButton: { template: '<button type="button"><slot /></button>' }
      }
    }
  })
}

describe('LoginDialog', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    getCheckCodeApi.mockReset()
    getCheckCodeApi.mockResolvedValue({ checkCode: 'data:image/png;base64,abc', checkCodeKey: 'KEY' })
    loginSpy.mockClear()
  })

  it('waits until the dialog is opened before loading captcha', async () => {
    mountDialog()
    await Promise.resolve()
    expect(getCheckCodeApi).not.toHaveBeenCalled()

    const userStore = useUserStore()
    userStore.openLoginDialog()
    await Promise.resolve()
    await Promise.resolve()

    expect(getCheckCodeApi).toHaveBeenCalledTimes(1)
  })

  it('clears stale captcha keys before submit when captcha reload fails', async () => {
    getCheckCodeApi
      .mockResolvedValueOnce({ checkCode: 'data:image/png;base64,abc', checkCodeKey: 'OLD-KEY' })
      .mockRejectedValueOnce(new Error('captcha down'))

    const wrapper = mountDialog()
    const userStore = useUserStore()
    userStore.login = loginSpy
    userStore.openLoginDialog()
    await Promise.resolve()
    await Promise.resolve()

    await wrapper.find('.captcha-row button').trigger('click')
    await Promise.resolve()
    await Promise.resolve()
    await wrapper.find('.submit').trigger('click')
    await Promise.resolve()

    expect(loginSpy).toHaveBeenCalledWith(expect.objectContaining({ checkCodeKey: '' }))
  })
})
