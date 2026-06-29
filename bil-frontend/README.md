# BilBil Frontend

Vue 3 + Vite 5 前端 MVP，面向 BilBil UGC 视频平台。覆盖视频浏览、搜索、弹幕、评论、用户系统、创作中心等核心场景。

## 技术栈

| 领域 | 方案 |
|------|------|
| 框架 | Vue 3（`<script setup>`） |
| 构建 | Vite 5 |
| UI 组件库 | Element Plus + `@element-plus/icons-vue` |
| 状态管理 | Pinia |
| 路由 | Vue Router 4（history 模式） |
| HTTP | Axios（封装 `request.js`，表单序列化 + 401 拦截） |
| 事件总线 | Mitt |
| 测试 | Vitest + jsdom + `@vue/test-utils` |

## 目录结构

```
bil-frontend/
├── index.html                        # 入口 HTML
├── vite.config.js                    # Vite 配置（含 /api 代理）
├── vitest.config.js                  # 测试配置
├── tsconfig.json                     # TS 类型检查（JS-only 项目，仅用于 IDE 辅助）
├── package.json
├── .env.example                      # 环境变量模板
├── .gitignore
└── src/
    ├── main.js                       # 应用入口：挂载 Vue + Pinia + Router + ElementPlus
    ├── App.vue                       # 根组件：初始化主题、自动登录、监听登录弹窗事件
    ├── api/
    │   ├── request.js                # Axios 实例：拦截器、token 注入、401 响应处理
    │   └── modules/                  # 按业务域拆分的 API 封装
    │       ├── account.js            #   登录、注册、自动登录、验证码
    │       ├── category.js           #   分区
    │       ├── comment.js            #   评论
    │       ├── danmu.js              #   弹幕
    │       ├── file.js               #   文件上传（图片、视频分片）
    │       ├── user.js               #   用户信息、创作中心
    │       └── video.js              #   视频、搜索、推荐、互动
    ├── components/
    │   ├── auth/
    │   │   └── LoginDialog.vue       # 登录/注册弹窗（含验证码）
    │   ├── layout/
    │   │   ├── AppHeader.vue         # 顶部导航：搜索、投稿、主题切换、消息
    │   │   └── SideCategoryNav.vue   # 侧栏分区导航
    │   ├── player/
    │   │   └── GlobalMiniPlayer.vue  # 底部迷你播放器（固定悬浮）
    │   └── video/
    │       ├── VideoCard.vue         # 视频卡片（封面、标题、播放/弹幕数）
    │       ├── InteractionBar.vue    # 互动操作栏（点赞、投币、收藏）
    │       ├── CommentList.vue       # 评论列表 + 发表
    │       ├── DanmuPanel.vue        # 弹幕面板（展示 + 发送）
    │       ├── VideoUploader.vue     # 视频上传（分片 5MB）
    │       └── ImageUploader.vue     # 图片/封面上传（拖拽支持）
    ├── layouts/
    │   └── MainLayout.vue           # 主布局：Header + Sidebar + Content + MiniPlayer
    ├── router/
    │   └── index.js                  # 路由表 + beforeEach 鉴权守卫
    ├── stores/
    │   ├── user.js                   # 用户状态：登录/注册/自动登录/弹窗
    │   ├── player.js                 # 播放器状态：队列、播放控制、localStorage 持久化
    │   └── theme.js                  # 主题：light / dark 切换
    ├── styles/
    │   ├── variables.css             # CSS 变量（主题色、间距、暗色主题变量）
    │   ├── base.css                  # 全局重置 + body 基础样式
    │   └── element-plus-overrides.css # Element Plus 样式覆盖（渐变按钮等）
    ├── utils/
    │   ├── eventBus.js               # Mitt 事件总线实例
    │   ├── mockData.js               # 模拟视频数据（开发/降级用）
    │   ├── token.js                  # Token 读写（localStorage）
    │   └── videoList.js              # API 响应列表归一化
    ├── views/
    │   ├── HomeView.vue              # 首页：推荐视频 + 按分区筛选
    │   ├── VideoDetailView.vue       # 视频详情：播放器 + 互动 + 评论 + 弹幕 + 推荐
    │   ├── SearchView.vue            # 搜索页：关键词搜索 + 热搜词
    │   ├── UserHomeView.vue          # 用户主页：个人信息 + 投稿列表
    │   └── CreatorCenterView.vue     # 创作中心：投稿表单 + 稿件管理
    └── test/
        └── setup.js                  # 测试全局 setup（清 localStorage / class）
```

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev

# 运行单元测试
npm run test:unit

# 监听模式
npm run test:watch

# 构建生产包
npm run build

# 预览构建结果
npm run preview
```

## 环境变量

从 `.env.example` 复制为 `.env.local` 进行本地配置：

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `VITE_API_BASE_URL` | `/api` | 前端请求的 API 基础路径 |
| `VITE_TOKEN_HEADER` | `thoken` | 自定义 token 请求头名称 |
| `VITE_API_PROXY_TARGET` | `http://localhost:7071` | 代理目标后端地址 |
| `VITE_API_PROXY_REWRITE` | `true` | `true` 时将 `/api/xxx` 重写为后端 `/xxx` |

**代理逻辑**：Vite dev server 将 `/api` 前缀的请求代理到 `VITE_API_PROXY_TARGET`。当后端接口不以 `/api` 为前缀时（如 `/account/login`），保持 `VITE_API_PROXY_REWRITE=true`；若后端挂载在 `/api` 下，设为 `false`。

## API 接口

所有接口均为 POST（少数为 GET），请求体经拦截器自动转为 `application/x-www-form-urlencoded`。响应格式约定 `{ code: 200, data: ..., info: ... }`。

### /account — 账户模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `getCheckCodeApi` | POST `/account/checkCode` | 获取图形验证码 |
| `registerApi` | POST `/account/register` | 注册 |
| `loginApi` | POST `/account/login` | 登录（密码 MD5） |
| `autoLoginApi` | POST `/account/autologin` | Token 自动登录 |
| `logoutApi` | POST `/account/logout` | 登出 |
| `getUserCountInfoApi` | POST `/account/getUserCountInfo` | 用户统计信息 |

### /category — 分区模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `loadAllCategoryApi` | POST `/category/loadAllCategory` | 加载全部分区 |

### /comment — 评论模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `postCommentApi` | POST `/comment/postComment` | 发表评论 |
| `loadCommentApi` | POST `/comment/loadComment` | 加载评论列表 |
| `topCommentApi` | POST `/comment/topComment` | 置顶评论 |
| `cancelTopCommentApi` | POST `/comment/cancelTopComment` | 取消置顶 |
| `userDelCommentApi` | POST `/comment/userDelComment` | 删除评论 |

### /danmu — 弹幕模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `postDanmuApi` | POST `/danmu/postDanmu` | 发送弹幕 |
| `loadDanmuApi` | POST `/danmu/loadDanmu` | 加载弹幕列表 |

### /file — 文件模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `getResourceApi` | GET `/file/getResource` | 获取资源 |
| `uploadImageApi` | POST `/file/uploadImage` | 上传图片（封面） |
| `preUploadVideoApi` | POST `/file/preUploadVideo` | 预上传视频（获取 uploadId） |
| `uploadVideoApi` | POST `/file/uploadVideo` | 上传视频分片 |
| `delUploadVideoApi` | POST `/file/delUploadVideo` | 取消/删除上传 |

视频上传流程：`preUploadVideoApi` 获取分片信息 → 循环上传各分片（5MB/chunk） → 提交完成。

### /uhome + /ucenter — 用户模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `getUserInfoApi` | POST `/uhome/getUserInfo` | 获取用户信息 |
| `updateUserInfoApi` | POST `/uhome/updateUserInfo` | 更新个人信息 |
| `loadUserVideoListApi` | POST `/uhome/loadVideoList` | 用户投稿列表 |
| `loadUserCollectionApi` | POST `/uhome/loadUserCollection` | 用户收藏 |
| `focusApi` | POST `/uhome/focus` | 关注用户 |
| `cancelFocusApi` | POST `/uhome/cancelFocus` | 取消关注 |
| `saveThemeApi` | POST `/uhome/saveTheme` | 保存用户主题 |
| `postVideoApi` | POST `/ucenter/postVideo` | 发布视频 |
| `loadCreatorVideoListApi` | POST `/ucenter/loadVideoList` | 创作中心视频列表 |

### /video — 视频模块
| 函数 | 接口 | 说明 |
|------|------|------|
| `loadRecommendVideoApi` | POST `/video/loadRecommendVideo` | 推荐视频 |
| `loadHotVideoListApi` | POST `/video/loadHotVideoList` | 热门视频 |
| `loadVideoApi` | POST `/video/loadVideo` | 按分区加载视频 |
| `getVideoInfoApi` | POST `/video/getVideoInfo` | 视频详情 |
| `loadVideoPListApi` | POST `/video/loadVideoPList` | 视频分 P 列表 |
| `searchVideoApi` | POST `/video/search` | 关键词搜索 |
| `getSearchKeywordTopApi` | POST `/video/getSearchKeywordTop` | 热搜关键词 |
| `getVideoRecommendApi` | POST `/video/getVideoRecommend` | 相关推荐 |
| `reportVideoPlayOnlineApi` | POST `/video/reportVideoPlayOnline` | 上报播放数据 |
| `doActionApi` | POST `/userAction/doAction` | 互动操作（点赞1/投币2/收藏3） |

## 路由设计

| 路径 | 名称 | 组件 | 鉴权 | 说明 |
|------|------|------|------|------|
| `/` | `home` | HomeView | — | 首页推荐（支持 `?pCategoryId` 分区筛选） |
| `/search` | `search` | SearchView | — | 搜索（支持 `?keyword`） |
| `/video/:videoId` | `video-detail` | VideoDetailView | — | 视频详情页 |
| `/user/:userId?` | `user-home` | UserHomeView | — | 用户主页（无参数时需确认逻辑） |
| `/creator` | `creator` | CreatorCenterView | `requiresAuth` | 创作中心（未登录跳首页弹登录框） |

鉴权通过 `router.beforeEach` 检查 `localStorage` 中的 token，若无 token 则通过事件总线触发登录弹窗并重定向首页。

## 状态管理

### useUserStore（用户状态）
- `token` / `profile`：从 localStorage 恢复登录态
- `loginDialogVisible`：控制全局登录弹窗显隐
- 关键 actions：`login`、`register`、`autoLogin`、`logout`、`openLoginDialog`
- 事件：认证变更时发出 `auth:changed`，全局弹窗监听 `auth:required`

### usePlayerStore（播放器状态）
- 播放队列 `queue`、当前视频 `current`、播放状态 `isPlaying`
- 持久化到 `localStorage`（key: `bil-player-state`），刷新后恢复
- 关键 actions：`play`、`enqueue`、`removeFromQueue`、`togglePlay`、`clearQueue`
- 事件：`player:play-video`（通知播放）、`player:queue-changed`、`player:playback-changed`

### useThemeStore（主题状态）
- `mode`: `light` | `dark`，默认为 `light`，持久化到 `localStorage`
- `toggleTheme()` 切换并写回 DOM class `theme-dark` + localStorage

## 事件总线

通过 Mitt 实现组件间松耦合通信：

| 事件 | 触发方 | 监听方 | 说明 |
|------|--------|--------|------|
| `auth:required` | router 鉴权守卫、request 401 拦截 | App.vue、LoginDialog | 触发登录弹窗 |
| `auth:changed` | userStore | AppHeader 等 | 用户状态变更通知 |
| `player:play-video` | playerStore | VideoDetailView | 开始播放 |
| `player:queue-changed` | playerStore | GlobalMiniPlayer | 队列变更 |
| `player:playback-changed` | playerStore | GlobalMiniPlayer | 播放状态变更 |
| `video:liked` | InteractionBar | AppHeader | 点赞通知（红点） |
| `video:collected` | InteractionBar | AppHeader | 收藏通知（红点） |
| `danmu:posted` | DanmuPanel | — | 弹幕发送通知 |

## 组件通信

```
App.vue
└── router-view
    └── MainLayout.vue
        ├── AppHeader.vue          # → userStore, themeStore, eventBus
        ├── SideCategoryNav.vue    # → category API, router
        ├── <router-view />
        │   ├── HomeView.vue       # → VideoCard, playerStore
        │   ├── SearchView.vue     # → VideoCard, playerStore
        │   ├── VideoDetailView.vue # → InteractionBar, CommentList, DanmuPanel, VideoCard
        │   ├── UserHomeView.vue   # → VideoCard, playerStore
        │   └── CreatorCenterView.vue # → VideoUploader, ImageUploader, VideoCard
        ├── GlobalMiniPlayer.vue   # → playerStore
        └── LoginDialog.vue        # → userStore, account API, eventBus
```

## 测试

测试目录结构与 `src/` 平行：

```
tests/
├── scaffold.test.js               # 构建契约（入口 HTML、Vite alias）
├── api/request.test.js            # request 模块
├── router/index.test.js           # 路由守卫
├── stores/
│   ├── user.test.js               # 用户状态
│   ├── player.test.js             # 播放器状态
│   └── theme.test.js              # 主题切换
├── components/
│   ├── appHeader.test.js
│   ├── loginDialog.test.js
│   ├── videoCard.test.js
│   └── interactionBar.test.js
├── utils/
│   ├── mockData.test.js
│   └── videoList.test.js
└── views/
    ├── homeView.test.js
    ├── searchView.test.js
    ├── videoDetailView.test.js
    └── creatorCenterView.test.js
```

运行测试：

```bash
npm run test:unit          # 单次运行
npm run test:watch         # 监听模式
```

## 部署

构建产物输出到 `dist/`，部署到任何静态文件服务即可。

```bash
npm run build
```

构建后的 `dist/` 目录由 `index.html` + 静态资源组成，Vite 自动处理代码分割、资源哈希、CSS 提取。

由于使用了 Vue Router 的 `createWebHistory`，需要在 Web 服务器上配置 SPA fallback（所有路径返回 `index.html`）。

### Nginx 示例

```nginx
server {
    listen 80;
    server_name example.com;
    root /var/www/bil-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:7071;
        proxy_set_header Host $host;
    }
}
```
