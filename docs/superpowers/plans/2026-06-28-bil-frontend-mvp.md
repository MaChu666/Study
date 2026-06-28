# BilBil Frontend MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an independent Vue 3 foreground MVP for the BilBil UGC video platform.

**Architecture:** Create a new `bil-frontend` Vite 5 app beside the Spring Boot modules. The app uses feature-oriented folders, Pinia stores for user/player/theme state, Axios API modules for backend access, Mitt for cross-component events, and a Bilibili-inspired Element Plus UI shell.

**Tech Stack:** Vue 3 with Composition API and `<script setup>`, Vite 5, Element Plus, Pinia, Vue Router, Axios, Mitt, Vitest, Vue Test Utils, jsdom.

## Global Constraints

- New frontend code must live under `D:\Git\bil-frontend`.
- Use Vue 3 Composition API and `<script setup>` for Vue single-file components.
- Use Element Plus and customize internals with `:deep()` where needed.
- Use Pinia for user information, global player state, and theme state.
- Use Mitt for login and video interaction events.
- Use Axios for API requests, token injection, and unified error handling.
- Token header defaults to `thoken` because the current backend reads `Constants.TOKEN_WEB = "thoken"`; override `VITE_TOKEN_HEADER` only when the backend token header changes.
- Vite API proxy target and `/api` rewrite behavior must be configurable through env values to avoid conflicts with backend context paths.
- Use brand blue `#00A1D6`, brand pink `#FB7299`, and gradient `linear-gradient(135deg, #00A1D6 0%, #FB7299 100%)`.
- Support light background `#F4F6F8` and dark background `#1a1a1a`.
- Video cards use `12px` border radius and hover lift shadow.
- The MVP excludes a full admin dashboard and full production upload pipeline UI.

---

## File Structure

Create this structure during implementation:

```text
D:\Git\bil-frontend\
  index.html
  package.json
  vite.config.js
  vitest.config.js
  .env.example
  src\
    App.vue
    main.js
    api\
      request.js
      modules\
        account.js
        category.js
        video.js
        comment.js
        danmu.js
        user.js
        file.js
    components\
      auth\LoginDialog.vue
      layout\AppHeader.vue
      layout\SideCategoryNav.vue
      player\GlobalMiniPlayer.vue
      video\CommentList.vue
      video\DanmuPanel.vue
      video\InteractionBar.vue
      video\VideoCard.vue
    layouts\MainLayout.vue
    router\index.js
    stores\
      user.js
      player.js
      theme.js
    styles\
      base.css
      element-plus-overrides.css
      variables.css
    test\
      setup.js
    utils\
      eventBus.js
      mockData.js
      token.js
    views\
      CreatorCenterView.vue
      HomeView.vue
      SearchView.vue
      UserHomeView.vue
      VideoDetailView.vue
  tests\
    api\request.test.js
    components\appHeader.test.js
    stores\player.test.js
    stores\theme.test.js
    stores\user.test.js
```

---

### Task 1: Vite App Scaffold And Test Harness

**Files:**
- Create: `D:\Git\bil-frontend\package.json`
- Create: `D:\Git\bil-frontend\index.html`
- Create: `D:\Git\bil-frontend\vite.config.js`
- Create: `D:\Git\bil-frontend\vitest.config.js`
- Create: `D:\Git\bil-frontend\.env.example`
- Create: `D:\Git\bil-frontend\src\main.js`
- Create: `D:\Git\bil-frontend\src\App.vue`
- Create: `D:\Git\bil-frontend\src\test\setup.js`

**Interfaces:**
- Produces: `npm run dev`, `npm run test:unit`, and `npm run build`.
- Produces: Vite alias `@` mapped to `D:\Git\bil-frontend\src`.

- [ ] **Step 1: Create directories**

Run:

```powershell
New-Item -ItemType Directory -Force -Path bil-frontend\src\test
```

Expected: PowerShell reports or confirms `D:\Git\bil-frontend\src\test`.

- [ ] **Step 2: Create package and config files**

Create `D:\Git\bil-frontend\package.json`:

```json
{
  "name": "bil-frontend",
  "version": "0.1.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite --host 0.0.0.0",
    "build": "vite build",
    "preview": "vite preview --host 0.0.0.0",
    "test:unit": "vitest run",
    "test:watch": "vitest"
  },
  "dependencies": {
    "@element-plus/icons-vue": "^2.3.1",
    "@vitejs/plugin-vue": "^5.2.4",
    "axios": "^1.7.9",
    "element-plus": "^2.9.6",
    "mitt": "^3.0.1",
    "pinia": "^2.3.1",
    "vue": "^3.5.13",
    "vue-router": "^4.5.0"
  },
  "devDependencies": {
    "@vue/test-utils": "^2.4.6",
    "jsdom": "^25.0.1",
    "vite": "^5.4.14",
    "vitest": "^2.1.8"
  }
}
```

Create `D:\Git\bil-frontend\index.html`:

```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>BilBil</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

Create `D:\Git\bil-frontend\vite.config.js`:

```js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const rewriteApiPrefix = env.VITE_API_PROXY_REWRITE !== 'false'

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:7071',
          changeOrigin: true,
          rewrite: rewriteApiPrefix ? (path) => path.replace(/^\/api/, '') : (path) => path
        }
      }
    }
  }
})
```

Create `D:\Git\bil-frontend\vitest.config.js`:

```js
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.js']
  }
})
```

Create `D:\Git\bil-frontend\.env.example`:

```text
VITE_API_BASE_URL=/api
VITE_TOKEN_HEADER=thoken
VITE_API_PROXY_TARGET=http://localhost:7071
VITE_API_PROXY_REWRITE=true
```

- [ ] **Step 3: Create minimal Vue entry files**

Create `D:\Git\bil-frontend\src\main.js`:

```js
import { createApp } from 'vue'
import App from './App.vue'

createApp(App).mount('#app')
```

Create `D:\Git\bil-frontend\src\App.vue`:

```vue
<template>
  <main class="app-shell">
    BilBil Frontend Ready
  </main>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  font: 600 20px/1.4 system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}
</style>
```

Create `D:\Git\bil-frontend\src\test\setup.js`:

```js
import { afterEach } from 'vitest'

afterEach(() => {
  localStorage.clear()
  document.documentElement.className = ''
})
```

- [ ] **Step 4: Install dependencies**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm install
```

Expected: `node_modules` and `package-lock.json` are created without dependency resolution errors.

- [ ] **Step 5: Verify scaffold builds**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run build
```

Expected: Vite reports a successful production build and creates `D:\Git\bil-frontend\dist`.

- [ ] **Step 6: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend
git commit -m "feat: scaffold vue frontend"
```

---

### Task 2: Token Utilities, Event Bus, API Request Layer, And Stores

**Files:**
- Create: `D:\Git\bil-frontend\src\utils\token.js`
- Create: `D:\Git\bil-frontend\src\utils\eventBus.js`
- Create: `D:\Git\bil-frontend\src\api\request.js`
- Create: `D:\Git\bil-frontend\src\api\modules\account.js`
- Create: `D:\Git\bil-frontend\src\api\modules\category.js`
- Create: `D:\Git\bil-frontend\src\api\modules\video.js`
- Create: `D:\Git\bil-frontend\src\api\modules\comment.js`
- Create: `D:\Git\bil-frontend\src\api\modules\danmu.js`
- Create: `D:\Git\bil-frontend\src\api\modules\user.js`
- Create: `D:\Git\bil-frontend\src\api\modules\file.js`
- Create: `D:\Git\bil-frontend\src\stores\user.js`
- Create: `D:\Git\bil-frontend\src\stores\player.js`
- Create: `D:\Git\bil-frontend\src\stores\theme.js`
- Test: `D:\Git\bil-frontend\tests\api\request.test.js`
- Test: `D:\Git\bil-frontend\tests\stores\user.test.js`
- Test: `D:\Git\bil-frontend\tests\stores\player.test.js`
- Test: `D:\Git\bil-frontend\tests\stores\theme.test.js`

**Interfaces:**
- Produces: `getToken(): string`, `setToken(token: string): void`, `clearToken(): void`.
- Produces: `eventBus` with Mitt methods `on`, `off`, `emit`.
- Produces: `request(config): Promise<any>`.
- Produces: Pinia stores `useUserStore`, `usePlayerStore`, `useThemeStore`.

- [ ] **Step 1: Write failing tests for token, request, and stores**

Create `D:\Git\bil-frontend\tests\api\request.test.js`:

```js
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

  it('unwraps backend success responses', async () => {
    await import('@/api/request')
    const instance = axios.create.mock.results[0].value
    const result = instance.responseSuccess({ data: { code: 200, data: { ok: true }, info: 'success' } })
    expect(result).toEqual({ ok: true })
  })
})
```

Create `D:\Git\bil-frontend\tests\stores\player.test.js`:

```js
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { usePlayerStore } from '@/stores/player'

describe('player store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('plays a video and keeps it in the queue once', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    expect(store.current.videoId).toBe('BV1')
    expect(store.isPlaying).toBe(true)
    expect(store.queue).toHaveLength(1)
  })

  it('removes the current video and clears playback', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo' })
    store.removeFromQueue('BV1')
    expect(store.current).toBe(null)
    expect(store.isPlaying).toBe(false)
  })

  it('persists and restores the current queue across refreshes', () => {
    const store = usePlayerStore()
    store.play({ videoId: 'BV1', videoName: 'Demo', videoUrl: '/demo.mp4' })
    setActivePinia(createPinia())
    const restored = usePlayerStore()
    expect(restored.current.videoId).toBe('BV1')
    expect(restored.queue).toHaveLength(1)
  })
})
```

Create `D:\Git\bil-frontend\tests\stores\theme.test.js`:

```js
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, it } from 'vitest'
import { useThemeStore } from '@/stores/theme'

describe('theme store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('toggles theme and applies the root class', () => {
    const store = useThemeStore()
    store.toggleTheme()
    expect(store.mode).toBe('dark')
    expect(document.documentElement.classList.contains('theme-dark')).toBe(true)
    expect(localStorage.getItem('bil-theme')).toBe('dark')
  })
})
```

Create `D:\Git\bil-frontend\tests\stores\user.test.js`:

```js
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
```

- [ ] **Step 2: Run tests and verify RED**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit
```

Expected: tests fail because `@/utils/token`, `@/api/request`, and stores do not exist.

- [ ] **Step 3: Implement utilities, API modules, and stores**

Create `D:\Git\bil-frontend\src\utils\token.js`:

```js
const TOKEN_KEY = 'bil-token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (!token) return
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}
```

Create `D:\Git\bil-frontend\src\utils\eventBus.js`:

```js
import mitt from 'mitt'

export const eventBus = mitt()
```

Create `D:\Git\bil-frontend\src\api\request.js`:

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { eventBus } from '@/utils/eventBus'
import { clearToken, getToken } from '@/utils/token'

const tokenHeader = import.meta.env.VITE_TOKEN_HEADER || 'thoken'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  withCredentials: true
})

service.interceptors.request.use((config) => {
  const token = getToken()
  config.headers = config.headers || {}
  if (token) {
    config.headers[tokenHeader] = token
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (!payload || typeof payload.code === 'undefined') {
      return payload
    }
    if (payload.code === 200) {
      return payload.data
    }
    if (response.status === 401 || payload.code === 401) {
      clearToken()
      eventBus.emit('auth:required')
    }
    ElMessage.error(payload.info || '请求失败')
    return Promise.reject(new Error(payload.info || '请求失败'))
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      clearToken()
      eventBus.emit('auth:required')
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
```

Create API modules with named functions:

```js
// D:\Git\bil-frontend\src\api\modules\account.js
import request from '@/api/request'

export const getCheckCodeApi = () => request({ url: '/account/checkCode', method: 'post' })
export const registerApi = (data) => request({ url: '/account/register', method: 'post', data })
export const loginApi = (data) => request({ url: '/account/login', method: 'post', data })
export const autoLoginApi = () => request({ url: '/account/autologin', method: 'post' })
export const logoutApi = () => request({ url: '/account/logout', method: 'post' })
export const getUserCountInfoApi = () => request({ url: '/account/getUserCountInfo', method: 'post' })
```

```js
// D:\Git\bil-frontend\src\api\modules\category.js
import request from '@/api/request'

export const loadAllCategoryApi = () => request({ url: '/category/loadAllCategory', method: 'post' })
```

```js
// D:\Git\bil-frontend\src\api\modules\video.js
import request from '@/api/request'

export const loadRecommendVideoApi = () => request({ url: '/video/loadRecommendVideo', method: 'post' })
export const loadVideoApi = (data) => request({ url: '/video/loadVideo', method: 'post', data })
export const getVideoInfoApi = (data) => request({ url: '/video/getVideoInfo', method: 'post', data })
export const loadVideoPListApi = (data) => request({ url: '/video/loadVideoPList', method: 'post', data })
export const doActionApi = (data) => request({ url: '/userAction/doAction', method: 'post', data })
export const searchVideoApi = (data) => request({ url: '/video/search', method: 'post', data })
export const getSearchKeywordTopApi = () => request({ url: '/video/getSearchKeywordTop', method: 'post' })
export const getVideoRecommendApi = (data) => request({ url: '/video/getVideoRecommend', method: 'post', data })
export const loadHotVideoListApi = () => request({ url: '/video/loadHotVideoList', method: 'post' })
export const reportVideoPlayOnlineApi = (data) => request({ url: '/video/reportVideoPlayOnline', method: 'post', data })
```

```js
// D:\Git\bil-frontend\src\api\modules\comment.js
import request from '@/api/request'

export const postCommentApi = (data) => request({ url: '/comment/postComment', method: 'post', data })
export const loadCommentApi = (data) => request({ url: '/comment/loadComment', method: 'post', data })
export const topCommentApi = (data) => request({ url: '/comment/topComment', method: 'post', data })
export const cancelTopCommentApi = (data) => request({ url: '/comment/cancelTopComment', method: 'post', data })
export const userDelCommentApi = (data) => request({ url: '/comment/userDelComment', method: 'post', data })
```

```js
// D:\Git\bil-frontend\src\api\modules\danmu.js
import request from '@/api/request'

export const postDanmuApi = (data) => request({ url: '/danmu/postDanmu', method: 'post', data })
export const loadDanmuApi = (data) => request({ url: '/danmu/loadDanmu', method: 'post', data })
```

```js
// D:\Git\bil-frontend\src\api\modules\user.js
import request from '@/api/request'

export const updateUserInfoApi = (data) => request({ url: '/uhome/updateUserInfo', method: 'post', data })
export const loadUserVideoListApi = (data) => request({ url: '/uhome/loadVideoList', method: 'post', data })
export const getUserInfoApi = (data) => request({ url: '/uhome/getUserInfo', method: 'post', data })
export const focusApi = (data) => request({ url: '/uhome/focus', method: 'post', data })
export const cancelFocusApi = (data) => request({ url: '/uhome/cancelFocus', method: 'post', data })
export const loadUserCollectionApi = (data) => request({ url: '/uhome/loadUserCollection', method: 'post', data })
export const saveThemeApi = (data) => request({ url: '/uhome/saveTheme', method: 'post', data })
export const postVideoApi = (data) => request({ url: '/ucenter/postVideo', method: 'post', data })
export const loadCreatorVideoListApi = (data) => request({ url: '/ucenter/loadVideoList', method: 'post', data })
```

```js
// D:\Git\bil-frontend\src\api\modules\file.js
import request from '@/api/request'

export const getResourceApi = (data) => request({ url: '/file/getResource', method: 'get', params: data })
export const uploadImageApi = (data) => request({ url: '/file/uploadImage', method: 'post', data })
export const preUploadVideoApi = (data) => request({ url: '/file/preUploadVideo', method: 'post', data })
export const uploadVideoApi = (data) => request({ url: '/file/uploadVideo', method: 'post', data })
export const delUploadVideoApi = (data) => request({ url: '/file/delUploadVideo', method: 'post', data })
```

Create stores:

```js
// D:\Git\bil-frontend\src\stores\user.js
import { defineStore } from 'pinia'
import { autoLoginApi, loginApi, logoutApi, registerApi } from '@/api/modules/account'
import { clearToken, getToken, setToken } from '@/utils/token'
import { eventBus } from '@/utils/eventBus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    profile: null,
    notificationDot: false,
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
    openLoginDialog() {
      this.loginDialogVisible = true
    },
    markNotificationDot(value) {
      this.notificationDot = Boolean(value)
    }
  }
})
```

```js
// D:\Git\bil-frontend\src\stores\player.js
import { defineStore } from 'pinia'

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
    },
    enqueue(video) {
      if (!video?.videoId) return
      const exists = this.queue.some((item) => item.videoId === video.videoId)
      if (!exists) {
        this.queue.push(video)
        savePlayerState(this)
      }
    },
    removeFromQueue(videoId) {
      this.queue = this.queue.filter((item) => item.videoId !== videoId)
      if (this.current?.videoId === videoId) {
        this.current = this.queue[0] || null
        this.isPlaying = Boolean(this.current)
      }
      savePlayerState(this)
    },
    togglePlay() {
      if (this.current) {
        this.isPlaying = !this.isPlaying
      }
    },
    clearQueue() {
      this.queue = []
      this.current = null
      this.isPlaying = false
      savePlayerState(this)
    }
  }
})
```

```js
// D:\Git\bil-frontend\src\stores\theme.js
import { defineStore } from 'pinia'

const THEME_KEY = 'bil-theme'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: localStorage.getItem(THEME_KEY) || 'light'
  }),
  actions: {
    toggleTheme() {
      this.mode = this.mode === 'light' ? 'dark' : 'light'
      this.applyTheme()
    },
    applyTheme() {
      document.documentElement.classList.toggle('theme-dark', this.mode === 'dark')
      localStorage.setItem(THEME_KEY, this.mode)
    }
  }
})
```

- [ ] **Step 4: Run tests and verify GREEN**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit
```

Expected: request, user store, player store, and theme store tests pass.

- [ ] **Step 5: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend\src\utils bil-frontend\src\api bil-frontend\src\stores bil-frontend\tests
git commit -m "feat: add frontend api and stores"
```

---

### Task 3: Router, Theme CSS, Main Layout, Header, Category Nav, And Mini Player

**Files:**
- Modify: `D:\Git\bil-frontend\src\main.js`
- Modify: `D:\Git\bil-frontend\src\App.vue`
- Create: `D:\Git\bil-frontend\src\router\index.js`
- Create: `D:\Git\bil-frontend\src\styles\variables.css`
- Create: `D:\Git\bil-frontend\src\styles\base.css`
- Create: `D:\Git\bil-frontend\src\styles\element-plus-overrides.css`
- Create: `D:\Git\bil-frontend\src\layouts\MainLayout.vue`
- Create: `D:\Git\bil-frontend\src\components\layout\AppHeader.vue`
- Create: `D:\Git\bil-frontend\src\components\layout\SideCategoryNav.vue`
- Create: `D:\Git\bil-frontend\src\components\player\GlobalMiniPlayer.vue`
- Create: initial route files `HomeView.vue`, `SearchView.vue`, `VideoDetailView.vue`, `UserHomeView.vue`, `CreatorCenterView.vue`
- Test: `D:\Git\bil-frontend\tests\components\appHeader.test.js`

**Interfaces:**
- Consumes: `useUserStore`, `useThemeStore`, `usePlayerStore`, and `eventBus`.
- Produces: route shell with `router-view`.
- Produces: `AppHeader` listening to `video:liked` and `video:collected`.

- [ ] **Step 1: Write failing header event test**

Create `D:\Git\bil-frontend\tests\components\appHeader.test.js`:

```js
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { describe, expect, it } from 'vitest'
import AppHeader from '@/components/layout/AppHeader.vue'
import { useUserStore } from '@/stores/user'
import { eventBus } from '@/utils/eventBus'

describe('AppHeader', () => {
  it('turns on the notification dot after a video interaction event', async () => {
    const pinia = createPinia()
    setActivePinia(pinia)
    mount(AppHeader, {
      global: {
        plugins: [pinia],
        stubs: ['router-link']
      }
    })
    const userStore = useUserStore()
    eventBus.emit('video:liked')
    await Promise.resolve()
    expect(userStore.notificationDot).toBe(true)
  })
})
```

- [ ] **Step 2: Run test and verify RED**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit -- tests/components/appHeader.test.js
```

Expected: fails because `AppHeader.vue` does not exist.

- [ ] **Step 3: Implement router and app entry**

Modify `D:\Git\bil-frontend\src\main.js`:

```js
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/variables.css'
import './styles/base.css'
import './styles/element-plus-overrides.css'

createApp(App)
  .use(createPinia())
  .use(router)
  .use(ElementPlus)
  .mount('#app')
```

Modify `D:\Git\bil-frontend\src\App.vue`:

```vue
<template>
  <router-view />
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { eventBus } from '@/utils/eventBus'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'

const themeStore = useThemeStore()
const userStore = useUserStore()

onMounted(() => {
  themeStore.applyTheme()
  eventBus.on('auth:required', userStore.openLoginDialog)
  userStore.autoLogin().catch(() => {
    userStore.openLoginDialog()
  })
})

onUnmounted(() => {
  eventBus.off('auth:required', userStore.openLoginDialog)
})
</script>
```

Create `D:\Git\bil-frontend\src\router\index.js`:

```js
import { createRouter, createWebHistory } from 'vue-router'
import { eventBus } from '@/utils/eventBus'
import { getToken } from '@/utils/token'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/HomeView.vue') },
      { path: 'search', name: 'search', component: () => import('@/views/SearchView.vue') },
      { path: 'video/:videoId', name: 'video-detail', component: () => import('@/views/VideoDetailView.vue') },
      { path: 'user/:userId?', name: 'user-home', component: () => import('@/views/UserHomeView.vue') },
      { path: 'creator', name: 'creator', meta: { requiresAuth: true }, component: () => import('@/views/CreatorCenterView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !getToken()) {
    eventBus.emit('auth:required')
    return { name: 'home' }
  }
  return true
})

export default router
```

- [ ] **Step 4: Implement global styles**

Create `D:\Git\bil-frontend\src\styles\variables.css`:

```css
:root {
  --bil-blue: #00A1D6;
  --bil-pink: #FB7299;
  --bil-gradient: linear-gradient(135deg, #00A1D6 0%, #FB7299 100%);
  --bil-bg: #F4F6F8;
  --bil-surface: #ffffff;
  --bil-text: #1f2329;
  --bil-muted: #7b8494;
  --bil-border: #e6eaf0;
  --bil-shadow: 0 10px 28px rgba(31, 35, 41, 0.08);
}

:root.theme-dark {
  --bil-bg: #1a1a1a;
  --bil-surface: #24262b;
  --bil-text: #f4f6f8;
  --bil-muted: #a8afbd;
  --bil-border: #363a42;
  --bil-shadow: 0 12px 30px rgba(0, 0, 0, 0.28);
}
```

Create `D:\Git\bil-frontend\src\styles\base.css`:

```css
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  background: var(--bil-bg);
  color: var(--bil-text);
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  font-size: 14px;
}

a {
  color: inherit;
  text-decoration: none;
}
```

Create `D:\Git\bil-frontend\src\styles\element-plus-overrides.css`:

```css
.bil-gradient-button {
  border: 0;
  color: #fff;
  background: var(--bil-gradient);
}

.el-message {
  border-radius: 10px;
}
```

- [ ] **Step 5: Implement layout components**

Create `D:\Git\bil-frontend\src\layouts\MainLayout.vue`:

```vue
<template>
  <div class="main-layout">
    <AppHeader />
    <div class="layout-body">
      <SideCategoryNav />
      <main class="content">
        <router-view />
      </main>
    </div>
    <GlobalMiniPlayer />
  </div>
</template>

<script setup>
import AppHeader from '@/components/layout/AppHeader.vue'
import SideCategoryNav from '@/components/layout/SideCategoryNav.vue'
import GlobalMiniPlayer from '@/components/player/GlobalMiniPlayer.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: var(--bil-bg);
}

.layout-body {
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 18px;
  max-width: 1440px;
  margin: 0 auto;
  padding: 84px 24px 96px;
}

.content {
  min-width: 0;
}

@media (max-width: 760px) {
  .layout-body {
    display: block;
    padding: 72px 14px 90px;
  }
}
</style>
```

Create `D:\Git\bil-frontend\src\components\layout\AppHeader.vue`:

```vue
<template>
  <header class="app-header">
    <router-link class="brand" to="/">BilBil</router-link>
    <el-input v-model="keyword" class="search" aria-label="搜索视频、UP主、番剧" @keyup.enter="goSearch" />
    <nav class="actions">
      <el-button class="bil-gradient-button" round @click="goCreator">投稿</el-button>
      <el-button circle @click="themeStore.toggleTheme">{{ themeStore.mode === 'light' ? '夜' : '日' }}</el-button>
      <button class="notice" type="button" @click="userStore.markNotificationDot(false)">
        消息
        <span v-if="userStore.notificationDot" class="dot" />
      </button>
      <el-avatar v-if="userStore.isLogin" :size="34">{{ userStore.profile?.useName?.slice(0, 1) }}</el-avatar>
      <el-button v-else text @click="userStore.openLoginDialog">登录</el-button>
    </nav>
  </header>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { eventBus } from '@/utils/eventBus'
import { useThemeStore } from '@/stores/theme'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const keyword = ref('')
const userStore = useUserStore()
const themeStore = useThemeStore()

function goSearch() {
  router.push({ name: 'search', query: { keyword: keyword.value } })
}

function goCreator() {
  router.push({ name: 'creator' })
}

function markDot() {
  userStore.markNotificationDot(true)
}

onMounted(() => {
  eventBus.on('video:liked', markDot)
  eventBus.on('video:collected', markDot)
})

onUnmounted(() => {
  eventBus.off('video:liked', markDot)
  eventBus.off('video:collected', markDot)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  inset: 0 0 auto;
  z-index: 20;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 24px;
  background: color-mix(in srgb, var(--bil-surface) 92%, transparent);
  border-bottom: 1px solid var(--bil-border);
  backdrop-filter: blur(16px);
}

.brand {
  font-size: 24px;
  font-weight: 800;
  background: var(--bil-gradient);
  -webkit-background-clip: text;
  color: transparent;
}

.search {
  max-width: 520px;
}

.actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.notice {
  position: relative;
  border: 0;
  background: transparent;
  color: var(--bil-text);
  cursor: pointer;
}

.dot {
  position: absolute;
  top: -2px;
  right: -5px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--bil-pink);
}

:deep(.el-input__wrapper) {
  border-radius: 999px;
}

@media (max-width: 760px) {
  .search {
    display: none;
  }
}
</style>
```

Create `D:\Git\bil-frontend\src\components\layout\SideCategoryNav.vue`:

```vue
<template>
  <aside class="side-nav">
    <button
      v-for="item in categories"
      :key="item.categoryId"
      type="button"
      class="nav-item"
      :class="{ active: String(route.query.pCategoryId || '0') === String(item.categoryId) }"
      @click="selectCategory(item)"
    >
      {{ item.categoryName }}
    </button>
  </aside>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadAllCategoryApi } from '@/api/modules/category'

const route = useRoute()
const router = useRouter()
const fallbackCategories = [
  { categoryId: 0, categoryName: '推荐' },
  { categoryId: 1, categoryName: '动画' },
  { categoryId: 2, categoryName: '游戏' },
  { categoryId: 3, categoryName: '音乐' },
  { categoryId: 4, categoryName: '科技' },
  { categoryId: 5, categoryName: '生活' }
]
const categories = ref(fallbackCategories)

async function loadCategories() {
  try {
    const data = await loadAllCategoryApi()
    categories.value = Array.isArray(data) && data.length ? [{ categoryId: 0, categoryName: '推荐' }, ...data] : fallbackCategories
  } catch {
    categories.value = fallbackCategories
  }
}

function selectCategory(item) {
  router.push({
    name: 'home',
    query: item.categoryId ? { pCategoryId: item.categoryId } : {}
  })
}

onMounted(loadCategories)
</script>

<style scoped>
.side-nav {
  position: sticky;
  top: 84px;
  height: fit-content;
  display: grid;
  gap: 10px;
}

.nav-item {
  height: 42px;
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
}

.nav-item.active {
  color: #fff;
  border: 0;
  background: var(--bil-gradient);
}

@media (max-width: 760px) {
  .side-nav {
    position: static;
    display: flex;
    overflow-x: auto;
    margin-bottom: 14px;
  }

  .nav-item {
    flex: 0 0 auto;
    padding: 0 16px;
  }
}
</style>
```

Create `D:\Git\bil-frontend\src\components\player\GlobalMiniPlayer.vue`:

```vue
<template>
  <section v-if="player.current" class="mini-player">
    <button type="button" class="play" @click="player.togglePlay">{{ player.isPlaying ? '暂停' : '播放' }}</button>
    <div class="meta">
      <strong>{{ player.current.videoName }}</strong>
      <span>{{ player.queue.length }} 个视频在队列中</span>
    </div>
    <button type="button" class="close" @click="player.clearQueue">关闭</button>
  </section>
</template>

<script setup>
import { usePlayerStore } from '@/stores/player'

const player = usePlayerStore()
</script>

<style scoped>
.mini-player {
  position: fixed;
  left: 50%;
  bottom: 18px;
  z-index: 30;
  width: min(560px, calc(100vw - 28px));
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  border: 1px solid var(--bil-border);
  border-radius: 16px;
  background: var(--bil-surface);
  box-shadow: var(--bil-shadow);
}

.play {
  border: 0;
  border-radius: 999px;
  padding: 9px 16px;
  color: #fff;
  background: var(--bil-gradient);
}

.meta {
  min-width: 0;
  display: grid;
  gap: 2px;
}

.meta strong,
.meta span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta span {
  color: var(--bil-muted);
  font-size: 12px;
}

.close {
  margin-left: auto;
  border: 0;
  background: transparent;
  color: var(--bil-muted);
}
</style>
```

- [ ] **Step 6: Create initial route views**

Create each view with a simple title so router build succeeds:

```vue
<template>
  <section class="view-empty">
    <h1>页面建设中</h1>
  </section>
</template>

<style scoped>
.view-empty {
  min-height: 320px;
  padding: 28px;
  border-radius: 12px;
  background: var(--bil-surface);
}
</style>
```

Use page-specific text for each file: `首页`, `搜索`, `视频详情`, `个人主页`, `创作中心`.

- [ ] **Step 7: Run tests and build**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit
npm run build
```

Expected: AppHeader test passes and Vite build succeeds.

- [ ] **Step 8: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend\src bil-frontend\tests
git commit -m "feat: add frontend layout shell"
```

---

### Task 4: Video Cards, Mock Fallback Data, Home Page, And Search Page

**Files:**
- Create: `D:\Git\bil-frontend\src\utils\mockData.js`
- Create: `D:\Git\bil-frontend\src\components\video\VideoCard.vue`
- Modify: `D:\Git\bil-frontend\src\views\HomeView.vue`
- Modify: `D:\Git\bil-frontend\src\views\SearchView.vue`

**Interfaces:**
- Produces: `mockVideos: Array<{ videoId, videoName, videoCover, userName, playCount, danmuCount, duration }>`
- Produces: `VideoCard` props `{ video: Object }` and emits `play`.

- [ ] **Step 1: Implement mock fallback data**

Create `D:\Git\bil-frontend\src\utils\mockData.js`:

```js
function cover(title, colorA, colorB) {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="960" height="540" viewBox="0 0 960 540"><defs><linearGradient id="g" x1="0" x2="1" y1="0" y2="1"><stop stop-color="${colorA}"/><stop offset="1" stop-color="${colorB}"/></linearGradient></defs><rect width="960" height="540" fill="url(#g)"/><circle cx="760" cy="120" r="96" fill="rgba(255,255,255,.18)"/><circle cx="130" cy="430" r="132" fill="rgba(255,255,255,.14)"/><text x="72" y="296" fill="white" font-size="54" font-family="Arial, sans-serif" font-weight="700">${title}</text></svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

export const mockVideos = [
  {
    videoId: 'BV1001',
    videoName: '夏日游戏混剪：高燃名场面合集',
    videoCover: cover('GAME MIX', '#00A1D6', '#FB7299'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: 'MachU',
    playCount: 128000,
    likeCount: 9300,
    coinCount: 1600,
    collectCount: 4200,
    danmuCount: 3420,
    duration: '12:48'
  },
  {
    videoId: 'BV1002',
    videoName: '从零开始写一个弹幕播放器',
    videoCover: cover('DANMU DEV', '#00A1D6', '#6c7bff'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: '前端研究所',
    playCount: 86000,
    likeCount: 5100,
    coinCount: 880,
    collectCount: 2300,
    danmuCount: 1180,
    duration: '18:22'
  },
  {
    videoId: 'BV1003',
    videoName: '一天吃遍城市里的宝藏小店',
    videoCover: cover('CITY FOOD', '#FB7299', '#ffb86c'),
    videoUrl: 'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    userName: '生活观察员',
    playCount: 214000,
    likeCount: 18800,
    coinCount: 3900,
    collectCount: 7600,
    danmuCount: 5680,
    duration: '09:37'
  }
]
```

- [ ] **Step 2: Implement `VideoCard.vue`**

Create `D:\Git\bil-frontend\src\components\video\VideoCard.vue`:

```vue
<template>
  <article class="video-card" @click="$emit('play', video)">
    <div class="cover-wrap">
      <img class="cover" :src="video.videoCover" :alt="video.videoName" />
      <span class="duration">{{ video.duration || '00:00' }}</span>
    </div>
    <h3>{{ video.videoName }}</h3>
    <p>{{ video.userName || video.useName || 'BilBil UP主' }}</p>
    <footer>
      <span>{{ formatCount(video.playCount) }} 播放</span>
      <span>{{ formatCount(video.danmuCount) }} 弹幕</span>
    </footer>
  </article>
</template>

<script setup>
defineProps({
  video: {
    type: Object,
    required: true
  }
})

defineEmits(['play'])

function formatCount(value) {
  const count = Number(value || 0)
  if (count >= 10000) return `${(count / 10000).toFixed(1)}万`
  return `${count}`
}
</script>

<style scoped>
.video-card {
  min-width: 0;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--bil-shadow);
}

.cover-wrap {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 12px;
  background: var(--bil-border);
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.duration {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 2px 7px;
  border-radius: 6px;
  color: #fff;
  background: rgba(0, 0, 0, 0.58);
  font-size: 12px;
}

h3 {
  margin: 10px 0 4px;
  font-size: 15px;
  line-height: 1.45;
}

p,
footer {
  margin: 0;
  color: var(--bil-muted);
  font-size: 13px;
}

footer {
  display: flex;
  gap: 12px;
  margin-top: 6px;
}
</style>
```

- [ ] **Step 3: Implement home and search pages**

Modify `D:\Git\bil-frontend\src\views\HomeView.vue`:

```vue
<template>
  <section class="home-view">
    <div class="section-head">
      <div>
        <h1>推荐视频</h1>
        <p>发现今天值得点开的内容</p>
      </div>
      <el-button text @click="loadVideos">刷新</el-button>
    </div>
    <el-skeleton v-if="loading" :rows="6" animated />
    <div v-else class="video-grid">
      <VideoCard v-for="video in videos" :key="video.videoId" :video="video" @play="openVideo" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadRecommendVideoApi, loadVideoApi } from '@/api/modules/video'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'

const router = useRouter()
const route = useRoute()
const playerStore = usePlayerStore()
const loading = ref(false)
const videos = ref(mockVideos)

async function loadVideos() {
  loading.value = true
  try {
    const data = route.query.pCategoryId
      ? await loadVideoApi({ pCategoryId: route.query.pCategoryId, pageNo: 1 })
      : await loadRecommendVideoApi()
    const list = Array.isArray(data?.list) ? data.list : data
    videos.value = Array.isArray(list) && list.length ? list : mockVideos
  } catch {
    videos.value = mockVideos
  } finally {
    loading.value = false
  }
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(loadVideos)
watch(() => route.query.pCategoryId, loadVideos)
</script>

<style scoped>
.section-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 18px;
}

h1 {
  margin: 0;
  font-size: 26px;
}

p {
  margin: 6px 0 0;
  color: var(--bil-muted);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 22px;
}
</style>
```

Modify `D:\Git\bil-frontend\src\views\SearchView.vue`:

```vue
<template>
  <section class="search-view">
    <div class="search-panel">
      <el-input v-model="keyword" aria-label="输入关键词搜索" @keyup.enter="runSearch" />
      <el-button class="bil-gradient-button" @click="runSearch">搜索</el-button>
    </div>
    <div class="hotwords">
      <span>热词</span>
      <button v-for="word in hotwords" :key="word" type="button" @click="useWord(word)">{{ word }}</button>
    </div>
    <div class="video-grid">
      <VideoCard v-for="video in results" :key="video.videoId" :video="video" @play="openVideo" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import VideoCard from '@/components/video/VideoCard.vue'
import { getSearchKeywordTopApi, searchVideoApi } from '@/api/modules/video'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const keyword = ref(route.query.keyword || '')
const hotwords = ref(['游戏', '弹幕', '前端', '美食'])
const results = ref(mockVideos)

async function loadHotwords() {
  try {
    const data = await getSearchKeywordTopApi()
    if (Array.isArray(data) && data.length) hotwords.value = data
  } catch {
    hotwords.value = ['游戏', '弹幕', '前端', '美食']
  }
}

async function runSearch() {
  router.replace({ name: 'search', query: { keyword: keyword.value } })
  try {
    const data = await searchVideoApi({ keyword: keyword.value })
    results.value = Array.isArray(data) && data.length ? data : mockVideos
  } catch {
    results.value = mockVideos
  }
}

function useWord(word) {
  keyword.value = word
  runSearch()
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(() => {
  loadHotwords()
  if (keyword.value) runSearch()
})
</script>

<style scoped>
.search-panel {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  margin-bottom: 14px;
}

.hotwords {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  margin-bottom: 22px;
  color: var(--bil-muted);
}

.hotwords button {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 6px 12px;
  background: var(--bil-surface);
  color: var(--bil-text);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 22px;
}
</style>
```

- [ ] **Step 4: Build verify**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run build
```

Expected: build succeeds and no text overflow warnings are introduced by tooling.

- [ ] **Step 5: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend\src\components\video bil-frontend\src\views bil-frontend\src\utils\mockData.js
git commit -m "feat: add home and search video pages"
```

---

### Task 5: Login Dialog, Video Detail, Interactions, Danmu, And Comments

**Files:**
- Create: `D:\Git\bil-frontend\src\components\auth\LoginDialog.vue`
- Create: `D:\Git\bil-frontend\src\components\video\InteractionBar.vue`
- Create: `D:\Git\bil-frontend\src\components\video\DanmuPanel.vue`
- Create: `D:\Git\bil-frontend\src\components\video\CommentList.vue`
- Modify: `D:\Git\bil-frontend\src\layouts\MainLayout.vue`
- Modify: `D:\Git\bil-frontend\src\views\VideoDetailView.vue`

**Interfaces:**
- Consumes: `useUserStore.openLoginDialog()`, `usePlayerStore.play(video)`, `eventBus`.
- Produces: interaction events `video:liked`, `video:collected`, and `danmu:posted`.
- `InteractionBar` consumes the full `video` object so initial like, collect, and coin counts render from backend video detail data.

- [ ] **Step 1: Implement login dialog**

Create `D:\Git\bil-frontend\src\components\auth\LoginDialog.vue`:

```vue
<template>
  <el-dialog v-model="userStore.loginDialogVisible" width="420px" class="login-dialog" title="登录 BilBil">
    <el-tabs v-model="mode">
      <el-tab-pane label="登录" name="login">
        <el-form :model="loginForm" label-position="top">
          <el-form-item label="邮箱"><el-input v-model="loginForm.email" /></el-form-item>
          <el-form-item label="密码"><el-input v-model="loginForm.password" show-password /></el-form-item>
          <el-form-item label="验证码">
            <div class="captcha-row">
              <el-input v-model="loginForm.checkCode" />
              <button type="button" @click="loadCaptcha">
                <img v-if="captcha.checkCode" :src="captcha.checkCode" alt="验证码" />
                <span v-else>刷新</span>
              </button>
            </div>
          </el-form-item>
          <el-button class="bil-gradient-button submit" @click="submitLogin">登录</el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="注册" name="register">
        <el-form :model="registerForm" label-position="top">
          <el-form-item label="邮箱"><el-input v-model="registerForm.email" /></el-form-item>
          <el-form-item label="用户名"><el-input v-model="registerForm.useName" /></el-form-item>
          <el-form-item label="密码"><el-input v-model="registerForm.registerPassword" show-password /></el-form-item>
          <el-form-item label="验证码"><el-input v-model="registerForm.checkCode" /></el-form-item>
          <el-button class="bil-gradient-button submit" @click="submitRegister">注册</el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckCodeApi } from '@/api/modules/account'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const mode = ref('login')
const captcha = reactive({ checkCode: '', checkCodeKey: '' })
const loginForm = reactive({ email: '', password: '', checkCode: '', checkCodeKey: '' })
const registerForm = reactive({ email: '', useName: '', registerPassword: '', checkCode: '', checkCodeKey: '' })

async function loadCaptcha() {
  const data = await getCheckCodeApi()
  captcha.checkCode = data.checkCode
  captcha.checkCodeKey = data.checkCodeKey
  loginForm.checkCodeKey = data.checkCodeKey
  registerForm.checkCodeKey = data.checkCodeKey
}

async function submitLogin() {
  await userStore.login(loginForm)
  ElMessage.success('欢迎回来')
}

async function submitRegister() {
  await userStore.register(registerForm)
  ElMessage.success('注册成功，请登录')
  mode.value = 'login'
  await loadCaptcha()
}

onMounted(loadCaptcha)
</script>

<style scoped>
.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 108px;
  gap: 10px;
}

.captcha-row button {
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  background: var(--bil-surface);
  overflow: hidden;
}

.captcha-row img {
  width: 100%;
  height: 36px;
  object-fit: cover;
}

.submit {
  width: 100%;
}

:deep(.el-dialog) {
  border-radius: 14px;
}
</style>
```

Modify `D:\Git\bil-frontend\src\layouts\MainLayout.vue` to include `<LoginDialog />` after `<GlobalMiniPlayer />`, and import it.

- [ ] **Step 2: Implement video interaction components**

Create `D:\Git\bil-frontend\src\components\video\InteractionBar.vue`:

```vue
<template>
  <div class="interaction-bar">
    <button type="button" @click="like">点赞 {{ counts.like }}</button>
    <button type="button" @click="collect">收藏 {{ counts.collect }}</button>
    <button type="button" @click="coin">投币 {{ counts.coin }}</button>
    <button type="button">分享</button>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { doActionApi } from '@/api/modules/video'
import { eventBus } from '@/utils/eventBus'

const props = defineProps({
  video: {
    type: Object,
    required: true
  }
})

const counts = reactive({ like: 0, collect: 0, coin: 0 })

watch(
  () => props.video,
  (video) => {
    counts.like = Number(video?.likeCount || 0)
    counts.collect = Number(video?.collectCount || 0)
    counts.coin = Number(video?.coinCount || 0)
  },
  { immediate: true }
)

async function like() {
  counts.like += 1
  await doActionApi({ videoId: props.video.videoId, actionType: 0, actionCount: 1 })
  eventBus.emit('video:liked', props.video.videoId)
}

async function collect() {
  counts.collect += 1
  await doActionApi({ videoId: props.video.videoId, actionType: 2, actionCount: 1 })
  eventBus.emit('video:collected', props.video.videoId)
}

async function coin() {
  counts.coin += 1
  await doActionApi({ videoId: props.video.videoId, actionType: 1, actionCount: 1 })
}
</script>

<style scoped>
.interaction-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 18px 0;
}

button {
  border: 1px solid var(--bil-border);
  border-radius: 999px;
  padding: 9px 16px;
  background: var(--bil-surface);
  color: var(--bil-text);
  cursor: pointer;
}

button:first-child {
  border: 0;
  color: #fff;
  background: var(--bil-gradient);
}
</style>
```

Create `D:\Git\bil-frontend\src\components\video\DanmuPanel.vue`:

```vue
<template>
  <aside class="danmu-panel">
    <h2>弹幕</h2>
    <div class="danmu-list">
      <p v-for="item in list" :key="item.danmuId || item.text">{{ item.text }}</p>
    </div>
    <div class="send-row">
      <el-input v-model="text" aria-label="发一条友善的弹幕" />
      <el-button class="bil-gradient-button" @click="post">发送</el-button>
    </div>
  </aside>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { loadDanmuApi, postDanmuApi } from '@/api/modules/danmu'
import { eventBus } from '@/utils/eventBus'

const props = defineProps({
  videoId: { type: String, required: true },
  fileId: { type: String, default: '' }
})

const list = ref([{ text: '这个开场太熟悉了' }, { text: '前方高能' }])
const text = ref('')

async function load() {
  try {
    const data = await loadDanmuApi({ videoId: props.videoId, fileId: props.fileId })
    if (Array.isArray(data)) list.value = data
  } catch {
    list.value = [{ text: '这个开场太熟悉了' }, { text: '前方高能' }]
  }
}

async function post() {
  if (!text.value.trim()) return
  await postDanmuApi({ videoId: props.videoId, fileId: props.fileId, text: text.value, mode: 1, color: '#ffffff', time: 0 })
  list.value.push({ text: text.value })
  text.value = ''
  eventBus.emit('danmu:posted', props.videoId)
}

onMounted(load)
</script>

<style scoped>
.danmu-panel {
  border-radius: 12px;
  padding: 18px;
  background: var(--bil-surface);
}

h2 {
  margin: 0 0 12px;
  font-size: 18px;
}

.danmu-list {
  display: grid;
  gap: 8px;
  max-height: 280px;
  overflow: auto;
  color: var(--bil-muted);
}

.send-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 10px;
  margin-top: 14px;
}
</style>
```

Create `D:\Git\bil-frontend\src\components\video\CommentList.vue`:

```vue
<template>
  <section class="comment-list">
    <h2>评论</h2>
    <div class="post-row">
      <el-input v-model="content" type="textarea" :rows="3" aria-label="说点什么吧" />
      <el-button class="bil-gradient-button" @click="post">发布</el-button>
    </div>
    <article v-for="item in comments" :key="item.commentId || item.content" class="comment-item">
      <strong>{{ item.userName || 'BilBil 用户' }}</strong>
      <p>{{ item.content }}</p>
    </article>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { loadCommentApi, postCommentApi } from '@/api/modules/comment'

const props = defineProps({
  videoId: { type: String, required: true }
})

const content = ref('')
const comments = ref([{ userName: '游客', content: '这个视频值得三连' }])

async function load() {
  try {
    const data = await loadCommentApi({ videoId: props.videoId, pageNo: 1, orderType: 0 })
    if (Array.isArray(data?.list)) comments.value = data.list
    else if (Array.isArray(data)) comments.value = data
  } catch {
    comments.value = [{ userName: '游客', content: '这个视频值得三连' }]
  }
}

async function post() {
  if (!content.value.trim()) return
  await postCommentApi({ videoId: props.videoId, content: content.value, replyCommentId: '', imgPath: '' })
  comments.value.unshift({ userName: '我', content: content.value })
  content.value = ''
}

onMounted(load)
</script>

<style scoped>
.comment-list {
  margin-top: 24px;
  border-radius: 12px;
  padding: 20px;
  background: var(--bil-surface);
}

h2 {
  margin: 0 0 14px;
}

.post-row {
  display: grid;
  gap: 10px;
  margin-bottom: 18px;
}

.comment-item {
  padding: 14px 0;
  border-top: 1px solid var(--bil-border);
}

.comment-item p {
  margin: 6px 0 0;
  color: var(--bil-muted);
}
</style>
```

- [ ] **Step 3: Implement video detail page**

Modify `D:\Git\bil-frontend\src\views\VideoDetailView.vue`:

```vue
<template>
  <section class="detail-view">
    <div class="main-column">
      <div class="player-box">
        <video controls :poster="video.videoCover" :src="videoSource" />
      </div>
      <h1>{{ video.videoName }}</h1>
      <p class="meta">{{ video.playCount || 0 }} 播放 · {{ video.danmuCount || 0 }} 弹幕</p>
      <InteractionBar :video="video" />
      <CommentList :video-id="videoId" />
    </div>
    <div class="side-column">
      <DanmuPanel :video-id="videoId" :file-id="fileId" />
      <section class="related">
        <h2>相关推荐</h2>
        <VideoCard v-for="item in related" :key="item.videoId" :video="item" @play="openVideo" />
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getVideoInfoApi, getVideoRecommendApi, loadVideoPListApi } from '@/api/modules/video'
import DanmuPanel from '@/components/video/DanmuPanel.vue'
import CommentList from '@/components/video/CommentList.vue'
import InteractionBar from '@/components/video/InteractionBar.vue'
import VideoCard from '@/components/video/VideoCard.vue'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const videoId = computed(() => route.params.videoId)
const video = ref(mockVideos[0])
const related = ref(mockVideos.slice(1))
const fileId = ref('')
const videoSource = computed(() => {
  if (video.value.videoUrl) return video.value.videoUrl
  if (video.value.filePath) return video.value.filePath
  if (fileId.value) return `/api/file/videoResource/${fileId.value}`
  return mockVideos[0].videoUrl
})

async function loadDetail() {
  try {
    const data = await getVideoInfoApi({ videoId: videoId.value })
    video.value = { ...mockVideos[0], ...(data || {}) }
    playerStore.play(video.value)
  } catch {
    video.value = mockVideos.find((item) => item.videoId === videoId.value) || mockVideos[0]
  }
  try {
    const files = await loadVideoPListApi({ videoId: videoId.value })
    fileId.value = Array.isArray(files) && files[0] ? files[0].fileId : ''
    if (Array.isArray(files) && files[0]?.filePath && !video.value.videoUrl) {
      video.value = { ...video.value, filePath: files[0].filePath }
      playerStore.play(video.value)
    }
  } catch {
    fileId.value = ''
  }
  try {
    const data = await getVideoRecommendApi({ videoId: videoId.value })
    if (Array.isArray(data) && data.length) related.value = data
  } catch {
    related.value = mockVideos.slice(1)
  }
}

function openVideo(item) {
  playerStore.play(item)
  router.push({ name: 'video-detail', params: { videoId: item.videoId } })
}

onMounted(loadDetail)
</script>

<style scoped>
.detail-view {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 22px;
}

.player-box {
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 12px;
  background: #050505;
}

video {
  width: 100%;
  height: 100%;
}

h1 {
  margin: 18px 0 6px;
  font-size: 24px;
}

.meta {
  color: var(--bil-muted);
}

.related {
  display: grid;
  gap: 16px;
  margin-top: 18px;
}

.related h2 {
  margin: 0;
  font-size: 18px;
}

@media (max-width: 980px) {
  .detail-view {
    grid-template-columns: 1fr;
  }
}
</style>
```

- [ ] **Step 4: Run tests and build**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit
npm run build
```

Expected: tests pass and build succeeds.

- [ ] **Step 5: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend\src
git commit -m "feat: add video detail interactions"
```

---

### Task 6: User Home And Creator Center MVP Pages

**Files:**
- Modify: `D:\Git\bil-frontend\src\views\UserHomeView.vue`
- Modify: `D:\Git\bil-frontend\src\views\CreatorCenterView.vue`

**Interfaces:**
- Consumes: `loadUserVideoListApi`, `getUserInfoApi`, `postVideoApi`, and `loadCreatorVideoListApi`.
- Produces: profile summary, submission grid, and publish-entry form shell.

- [ ] **Step 1: Implement user home page**

Modify `D:\Git\bil-frontend\src\views\UserHomeView.vue`:

```vue
<template>
  <section class="user-home">
    <div class="profile-band">
      <el-avatar :size="72">{{ profile.useName?.slice(0, 1) || 'B' }}</el-avatar>
      <div>
        <h1>{{ profile.useName || 'BilBil UP主' }}</h1>
        <p>{{ profile.introduction || '这个人还没有写简介' }}</p>
      </div>
    </div>
    <h2>投稿视频</h2>
    <div class="video-grid">
      <VideoCard v-for="video in videos" :key="video.videoId" :video="video" @play="openVideo" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import VideoCard from '@/components/video/VideoCard.vue'
import { getUserInfoApi, loadUserVideoListApi } from '@/api/modules/user'
import { usePlayerStore } from '@/stores/player'
import { mockVideos } from '@/utils/mockData'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const profile = ref({})
const videos = ref(mockVideos)

async function load() {
  const userId = route.params.userId || ''
  try {
    profile.value = await getUserInfoApi({ userId })
  } catch {
    profile.value = { useName: 'BilBil UP主', introduction: '分享热爱的内容创作者' }
  }
  try {
    const data = await loadUserVideoListApi({ userId, pageNo: 1 })
    videos.value = Array.isArray(data?.list) ? data.list : mockVideos
  } catch {
    videos.value = mockVideos
  }
}

function openVideo(video) {
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

onMounted(load)
</script>

<style scoped>
.profile-band {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 28px;
  border-radius: 12px;
  background: var(--bil-gradient);
  color: #fff;
}

h1,
h2 {
  margin: 0;
}

.profile-band p {
  margin: 8px 0 0;
}

h2 {
  margin: 24px 0 16px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 22px;
}
</style>
```

- [ ] **Step 2: Implement creator center page**

Modify `D:\Git\bil-frontend\src\views\CreatorCenterView.vue`:

```vue
<template>
  <section class="creator-center">
    <div class="dashboard">
      <div>
        <h1>创作中心</h1>
        <p>管理投稿、查看状态、继续发布新内容</p>
      </div>
      <el-button class="bil-gradient-button" @click="submitVideo">保存投稿草稿</el-button>
    </div>
    <el-form :model="form" label-position="top" class="publish-form">
      <el-form-item label="封面地址"><el-input v-model="form.videoCover" /></el-form-item>
      <el-form-item label="视频标题"><el-input v-model="form.videoName" /></el-form-item>
      <el-form-item label="标签"><el-input v-model="form.tags" /></el-form-item>
      <el-form-item label="简介"><el-input v-model="form.introduction" type="textarea" :rows="4" /></el-form-item>
    </el-form>
    <h2>我的投稿</h2>
    <div class="video-grid">
      <VideoCard v-for="video in videos" :key="video.videoId" :video="video" />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import VideoCard from '@/components/video/VideoCard.vue'
import { loadCreatorVideoListApi, postVideoApi } from '@/api/modules/user'
import { mockVideos } from '@/utils/mockData'

const videos = ref(mockVideos)
const form = reactive({
  videoCover: '',
  videoName: '',
  pCategoryId: '1',
  categoryId: '1',
  postType: '0',
  tags: '',
  introduction: '',
  interaction: '1',
  uploadFileList: ''
})

async function loadVideos() {
  try {
    const data = await loadCreatorVideoListApi({ status: 0, pageNo: 1 })
    videos.value = Array.isArray(data?.list) ? data.list : mockVideos
  } catch {
    videos.value = mockVideos
  }
}

async function submitVideo() {
  await postVideoApi(form)
  ElMessage.success('投稿信息已保存')
  await loadVideos()
}

onMounted(loadVideos)
</script>

<style scoped>
.dashboard,
.publish-form {
  border-radius: 12px;
  padding: 22px;
  background: var(--bil-surface);
}

.dashboard {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

h1,
h2,
p {
  margin: 0;
}

p {
  margin-top: 6px;
  color: var(--bil-muted);
}

h2 {
  margin: 24px 0 16px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 22px;
}
</style>
```

- [ ] **Step 3: Run build**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run build
```

Expected: Vite build succeeds.

- [ ] **Step 4: Commit**

```powershell
Set-Location D:\Git
git add bil-frontend\src\views\UserHomeView.vue bil-frontend\src\views\CreatorCenterView.vue
git commit -m "feat: add user and creator pages"
```

---

### Task 7: Final Verification, Dev Server, And Documentation

**Files:**
- Create: `D:\Git\bil-frontend\README.md`
- Modify: `D:\Git\.gitignore`

**Interfaces:**
- Produces: README commands and API environment instructions.
- Produces: local dev URL from `npm run dev`.

- [ ] **Step 1: Add frontend ignore entries**

Modify `D:\Git\.gitignore` to include:

```text
bil-frontend/node_modules/
bil-frontend/dist/
bil-frontend/.env.local
```

- [ ] **Step 2: Add frontend README**

Create `D:\Git\bil-frontend\README.md`:

```markdown
# BilBil Frontend

Vue 3 + Vite 5 foreground MVP for the BilBil UGC video platform.

## Stack

- Vue 3 with `<script setup>`
- Element Plus
- Pinia
- Vue Router
- Axios
- Mitt
- Vitest

## Commands

```bash
npm install
npm run dev
npm run test:unit
npm run build
```

## Environment

Copy `.env.example` to `.env.local` when local API settings differ.

```text
VITE_API_BASE_URL=/api
VITE_TOKEN_HEADER=thoken
VITE_API_PROXY_TARGET=http://localhost:7071
VITE_API_PROXY_REWRITE=true
```

The Vite dev server proxies `/api` to `VITE_API_PROXY_TARGET`.
Keep `VITE_API_PROXY_REWRITE=true` for the current backend where frontend `/api/account/login` should become backend `/account/login`.
Set `VITE_API_PROXY_REWRITE=false` only if the backend itself is mounted under `/api`.
```

- [ ] **Step 3: Run full verification**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run test:unit
npm run build
```

Expected: all tests pass and production build succeeds.

- [ ] **Step 4: Start dev server**

Run:

```powershell
Set-Location D:\Git\bil-frontend
npm run dev
```

Expected: Vite prints a local URL, usually `http://localhost:5173/`.

- [ ] **Step 5: Smoke check routes**

Open or inspect these routes in the dev server:

```text
http://localhost:5173/
http://localhost:5173/search?keyword=游戏
http://localhost:5173/video/BV1001
http://localhost:5173/user
http://localhost:5173/creator
```

Expected: each route renders the app shell, no blank page, and the mini player appears after selecting a video.

- [ ] **Step 6: Commit**

```powershell
Set-Location D:\Git
git add .gitignore bil-frontend\README.md
git commit -m "docs: add frontend usage notes"
```

---

## Self-Review

Spec coverage:

- Independent `bil-frontend` Vite app: Task 1.
- Vue 3, Element Plus, Pinia, Router, Axios, Mitt: Tasks 1 through 3.
- Axios token injection and unified response handling: Task 2.
- Pinia user, player, and theme stores: Task 2.
- Mitt login and interaction events: Tasks 2, 3, and 5.
- Top navigation plus left category navigation: Task 3.
- Bilibili color tokens, light/dark themes, card radius, hover shadow, responsive shell: Tasks 3 and 4.
- Home, search, video detail, login/register, user home, creator center, mini player: Tasks 3 through 6.
- Tests and final verification: Tasks 2, 3, and 7.

Red-flag scan:

- The plan contains no incomplete task markers. Every task defines concrete files, code, commands, and expected outcomes.

Type and interface consistency:

- Store names are consistent: `useUserStore`, `usePlayerStore`, `useThemeStore`.
- Event names are consistent: `auth:required`, `auth:changed`, `video:liked`, `video:collected`, `danmu:posted`.
- Video object keys are consistent: `videoId`, `videoName`, `videoCover`, `playCount`, `danmuCount`.
