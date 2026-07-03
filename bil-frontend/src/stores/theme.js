import { defineStore } from 'pinia'

const THEME_KEY = 'bil-theme'

function applyThemeMode(mode) {
  document.documentElement.setAttribute('data-theme', mode === 'dark' ? 'dark' : 'light')
}

export const useThemeStore = defineStore('theme', {
  state: () => {
    const mode = localStorage.getItem(THEME_KEY) || 'light'
    applyThemeMode(mode)

    return {
      mode
    }
  },
  actions: {
    toggleTheme() {
      this.mode = this.mode === 'light' ? 'dark' : 'light'
      this.applyTheme()
    },
    applyTheme() {
      applyThemeMode(this.mode)
      localStorage.setItem(THEME_KEY, this.mode)
    }
  }
})
