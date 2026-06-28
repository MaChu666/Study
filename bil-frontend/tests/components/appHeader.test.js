import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory, createRouter } from 'vue-router'
import { beforeEach, describe, expect, it } from 'vitest'
import AppHeader from '@/components/layout/AppHeader.vue'
import { useUserStore } from '@/stores/user'
import { eventBus } from '@/utils/eventBus'

function createTestRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/', name: 'home', component: { template: '<div />' } },
      { path: '/search', name: 'search', component: { template: '<div />' } },
      { path: '/creator', name: 'creator', component: { template: '<div />' } }
    ]
  })
}

describe('AppHeader', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('turns on the notification dot after a liked event', async () => {
    const pinia = createPinia()
    const router = createTestRouter()
    setActivePinia(pinia)
    router.push('/')
    await router.isReady()

    mount(AppHeader, {
      global: {
        plugins: [pinia, router]
      }
    })

    const userStore = useUserStore()
    eventBus.emit('video:liked')
    await Promise.resolve()

    expect(userStore.notificationDot).toBe(true)
  })

  it('shows the notification dot after a collected event', async () => {
    const pinia = createPinia()
    const router = createTestRouter()
    setActivePinia(pinia)
    router.push('/')
    await router.isReady()

    const wrapper = mount(AppHeader, {
      global: {
        plugins: [pinia, router]
      }
    })

    const userStore = useUserStore()
    expect(wrapper.find('.dot').exists()).toBe(false)

    eventBus.emit('video:collected')
    await Promise.resolve()

    expect(userStore.notificationDot).toBe(true)
    expect(wrapper.find('.dot').exists()).toBe(true)
  })
})
