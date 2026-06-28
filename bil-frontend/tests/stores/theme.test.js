import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { useThemeStore } from '@/stores/theme'

describe('theme store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    document.documentElement.classList.remove('theme-dark')
  })

  it('toggles theme and applies the root class', () => {
    const store = useThemeStore()
    store.toggleTheme()
    expect(store.mode).toBe('dark')
    expect(document.documentElement.classList.contains('theme-dark')).toBe(true)
    expect(localStorage.getItem('bil-theme')).toBe('dark')
  })

  it('applies persisted dark theme when the store initializes', () => {
    localStorage.setItem('bil-theme', 'dark')

    useThemeStore()

    expect(document.documentElement.classList.contains('theme-dark')).toBe(true)
  })
})
