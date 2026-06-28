# BilBil Frontend MVP Design

## Goal

Build an independent Vue 3 frontend for the BilBil UGC video platform. The first version focuses on the foreground user experience: browsing videos, searching, watching video details, interacting with videos, login/register state, a lightweight creator center, global mini player state, and light/dark theme switching.

The frontend will live in a new `bil-frontend` directory and communicate with the existing Spring Boot APIs through Axios.

## Scope

Included in this MVP:

- Home video feed with top navigation and left category navigation.
- Search page with keyword input, hot keywords, and video result cards.
- Video detail page with player area, danmu panel, interaction bar, comments, and related videos.
- Login/register dialog with captcha support and token persistence.
- Lightweight user home / creator center pages for profile, submissions, and publish entry.
- Global bottom mini player controlled by Pinia.
- Light/dark theme controlled by Pinia and persisted locally.
- Axios request layer with token injection and error handling.
- Mitt event bus for video interaction notifications and login state events.

Not included in this MVP:

- Full admin dashboard.
- Full production video upload pipeline UI beyond a publish-entry form shell.
- Real HLS playback dependency integration unless the existing API/resource behavior is confirmed during implementation.
- Full Elasticsearch-specific UI behavior beyond current search API calls.

## Technical Stack

- Vue 3 with Composition API and `<script setup>`.
- Vite 5.
- Element Plus.
- Pinia.
- Vue Router.
- Axios.
- Mitt.

## Architecture

The frontend will use a feature-oriented structure:

```text
bil-frontend/
  src/
    api/
      request.js
      modules/
        account.js
        category.js
        video.js
        comment.js
        danmu.js
        user.js
        file.js
    assets/
    components/
      auth/
      layout/
      player/
      video/
    layouts/
      MainLayout.vue
    router/
      index.js
    stores/
      user.js
      player.js
      theme.js
    styles/
      variables.css
      base.css
      element-plus-overrides.css
    utils/
      eventBus.js
      token.js
    views/
      HomeView.vue
      SearchView.vue
      VideoDetailView.vue
      UserHomeView.vue
      CreatorCenterView.vue
```

`MainLayout.vue` owns the Bilibili-like shell: sticky top navigation, compact left category navigation, right content area, and bottom global mini player.

## Routes

- `/` renders `HomeView`.
- `/search?keyword=` renders `SearchView`.
- `/video/:videoId` renders `VideoDetailView`.
- `/user/:userId?` renders `UserHomeView`.
- `/creator` renders `CreatorCenterView` and requires login.

Routes that require login use the Pinia user store. If no token/user exists, the login dialog opens and navigation is cancelled or redirected to `/`.

## API Layer

`src/api/request.js` creates one Axios instance:

- `baseURL` reads from `import.meta.env.VITE_API_BASE_URL`, defaulting to `http://localhost:7071`.
- Request interceptor injects token from the user store/local storage into the configured token header.
- Response interceptor unwraps the backend response shape `{ status, code, info, data }`.
- `code === 200` returns `data`.
- Unauthorized-like codes or HTTP 401 clear user state, emit `auth:required`, and show login.
- Business errors show an Element Plus message and reject with a normalized error.

API modules map to backend domains and keep views free of URL strings.

## State Management

### User Store

The user store owns:

- `token`
- `profile`
- `isLogin`
- `notificationDot`
- `loginDialogVisible`

Actions:

- `login(payload)`
- `register(payload)`
- `logout()`
- `autoLogin()`
- `openLoginDialog()`
- `markNotificationDot(value)`

### Player Store

The player store owns:

- `queue`
- `current`
- `isPlaying`
- `currentTime`
- `muted`
- `volume`

Actions:

- `play(video)`
- `enqueue(video)`
- `removeFromQueue(videoId)`
- `togglePlay()`
- `clearQueue()`

The global mini player appears when `current` exists.

### Theme Store

The theme store owns:

- `mode`: `light` or `dark`

Actions:

- `toggleTheme()`
- `applyTheme()`

The store writes a theme class to the document root and persists mode in local storage.

## Mitt Events

`src/utils/eventBus.js` exports a single Mitt instance.

Events:

- `auth:required`: opens the login dialog.
- `auth:changed`: header refreshes avatar/login state.
- `video:liked`: header turns on notification dot.
- `video:collected`: header turns on notification dot.
- `danmu:posted`: video detail refreshes danmu list.
- `player:play-video`: global mini player updates to a selected video.

The video detail page emits `video:liked` and `video:collected` after successful API calls. The header listens and updates its red dot through the user store.

## Visual Design

The interface uses a Bilibili-inspired product UI, not a marketing page.

Core tokens:

- Brand blue: `#00A1D6`.
- Brand pink: `#FB7299`.
- Brand gradient: `linear-gradient(135deg, #00A1D6 0%, #FB7299 100%)`.
- Light background: `#F4F6F8`.
- Dark background: `#1a1a1a`.
- Card radius: `12px`.
- Base font: system UI stack.
- Body text: `14px` to `16px`.

Element Plus customization:

- Buttons, tabs, active nav items, switches, and selected controls use the brand gradient.
- Dialog and input styling are customized with scoped `:deep()` selectors where component internals require it.
- Video cards use a stable aspect ratio, rounded cover image, compact metadata, and hover lift shadow.

Responsive behavior:

- Desktop uses top navigation plus left category navigation.
- Tablet keeps top navigation and collapses category labels.
- Mobile switches category navigation into a horizontal scroll strip and keeps the mini player compact.

## Core Components

- `AppHeader.vue`: logo, search box, publish entry, theme toggle, notification dot, avatar/login button.
- `SideCategoryNav.vue`: category list and active category state.
- `VideoCard.vue`: cover, title, author, play count, danmu/comment count, duration.
- `GlobalMiniPlayer.vue`: bottom fixed mini player with play/pause, title, queue count, close.
- `LoginDialog.vue`: login/register tabs, captcha image, form validation.
- `InteractionBar.vue`: like, collect, coin, share actions.
- `DanmuPanel.vue`: danmu list and input.
- `CommentList.vue`: comments and post form.

## Error Handling

- API business errors show Element Plus messages.
- Login-required errors emit `auth:required`.
- Empty states render clear but compact in-content panels.
- Loading states use Element Plus skeletons for cards and detail sections.
- Route-level failures keep the shell visible and show an in-content error state.

## Testing And Verification

The implementation will include unit tests for behavior-heavy modules where practical:

- Axios response normalization and unauthorized handling.
- Pinia user store login/logout state.
- Pinia player queue behavior.
- Theme store persistence and class application.
- Mitt event handling for video interaction red dot updates.

Build verification:

- Install dependencies.
- Run unit tests.
- Run production build.
- Start local dev server and verify the main routes render.

## Delivery

The MVP is complete when:

- `bil-frontend` can install, run, test, and build.
- The main foreground routes render with a polished Bilibili-inspired UI.
- API modules exist for the backend endpoints used by the pages.
- Token injection and unified error handling are wired.
- Pinia stores drive user, player, and theme state.
- Mitt is used for video interaction-to-header notification updates.
- Light and dark modes are visually coherent.
