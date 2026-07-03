import { defineStore } from 'pinia'
import { autoLoginApi, loginApi, logoutApi, registerApi, getNoReadCountApi } from '@/api/modules/account'
import { clearToken, getToken, setToken } from '@/utils/token'
import { eventBus } from '@/utils/eventBus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    profile: null,
    notificationDot: false,
    unreadCount: 0,
    loginDialogVisible: false
  }),
  getters: {
    isLogin: (state) => Boolean(state.token && state.profile)
  },
  actions: {
    async login(payload) {
      const profile = await loginApi(payload)
      this.profile = profile
      this.token = profile?.token || ''
      setToken(this.token)
      this.loginDialogVisible = false
      eventBus.emit('auth:changed', this.profile)
      return profile
    },
    async register(payload) {
      return registerApi(payload)
    },
    async autoLogin() {
      if (!this.token) {
        return null
      }
      const profile = await autoLoginApi()
      if (profile?.token) {
        this.profile = profile
        this.token = profile.token
        setToken(profile.token)
        eventBus.emit('auth:changed', this.profile)
      }
      return profile
    },
    async logout() {
      await logoutApi()
      this.profile = null
      this.token = ''
      clearToken()
      eventBus.emit('auth:changed', null)
    },
    async fetchUnreadCount() {
      if (!this.isLogin) {
        this.notificationDot = false
        this.unreadCount = 0
        return
      }
      try {
        const count = await getNoReadCountApi()
        this.unreadCount = Number(count) || 0
        this.notificationDot = this.unreadCount > 0
      } catch {
        this.notificationDot = false
        this.unreadCount = 0
      }
    },
    openLoginDialog() {
      this.loginDialogVisible = true
    },
    markNotificationDot(value) {
      this.notificationDot = Boolean(value)
    }
  }
})
