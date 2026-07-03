import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { useThemeStore } from '@/stores/theme'

describe('theme store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    document.documentElement.setAttribute('data-theme', 'light')
  })

  it('toggles theme and applies the data attribute', () => {
    const store = useThemeStore()
    store.toggleTheme()
    expect(store.mode).toBe('dark')
    expect(document.documentElement.getAttribute('data-theme')).toBe('dark')
    expect(localStorage.getItem('bil-theme')).toBe('dark')
  })

  it('applies persisted dark theme when the store initializes', () => {
    localStorage.setItem('bil-theme', 'dark')

    useThemeStore()

    expect(document.documentElement.getAttribute('data-theme')).toBe('dark')
  })
})
