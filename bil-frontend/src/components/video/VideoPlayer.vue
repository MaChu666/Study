<template>
  <div class="video-player-container">
    <div
      class="video-player-wrapper"
      ref="wrapperRef"
      @click="closeDanmuMenu"
      tabindex="0"
      @keydown="onKeydown"
      @dblclick="onDblClick"
    >
      <video
        ref="videoRef"
        :src="src"
        :poster="posterSrc"
        crossorigin="anonymous"
        playsinline
        @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoaded"
        @play="onPlay"
        @pause="onPause"
        @click="togglePlay"
        @error="onVideoError"
        @waiting="loading = true"
        @canplay="loading = false"
        @ended="onVideoEnded"
        @enterpictureinpicture="isPip = true"
        @leavepictureinpicture="isPip = false"
      />



      <!-- Loading overlay -->
      <div v-if="loading" class="video-overlay loading-overlay">
        <div class="spinner" />
        <span class="loading-text">加载中...</span>
      </div>

      <!-- Error overlay -->
      <div v-if="errorMsg" class="video-overlay error-overlay">
        <span class="error-icon">!</span>
        <span class="error-text">{{ errorMsg }}</span>
        <button class="retry-btn" @click="retryLoad">重试</button>
      </div>

      <!-- Resume prompt overlay -->
      <div v-if="showResumePrompt" class="video-overlay resume-overlay" @click.stop>
        <div class="resume-content">
          <span class="resume-text">继续播放 (从 {{ formatResumeTime(savedProgressTime) }} 开始)</span>
          <div class="resume-actions">
            <button class="resume-accept-btn" @click="onResumeAccept">继续</button>
            <button class="resume-dismiss-btn" @click="onResumeDismiss">重新开始</button>
          </div>
        </div>
      </div>

      <!-- Next video overlay -->
      <div v-if="showNextOverlay" class="video-overlay next-overlay" @click.stop>
        <div class="next-content">
          <div v-if="nextVideo" class="next-info">
            <img
              v-if="nextVideo.videoCover"
              :src="nextVideo.videoCover"
              class="next-cover"
              alt=""
            />
            <div class="next-text">
              <span class="next-label">即将播放</span>
              <span class="next-title">{{ nextVideo.videoName }}</span>
            </div>
          </div>
          <div v-else class="next-info">
            <span class="next-label">即将播放</span>
          </div>
          <div class="next-actions">
            <button class="next-skip-btn" @click="goNextVideo">
              {{ countdown }}s 跳过
            </button>
            <button class="next-cancel-btn" @click="cancelNext">取消</button>
          </div>
        </div>
      </div>

      <!-- Controls layer -->
      <div
        class="controls-layer"
        :class="{ visible: showControls }"
        @mousemove="wakeControls"
      >
        <!-- Progress bar with time tooltip -->
        <div
          class="progress-bar"
          @mousemove="onProgressHover"
          @mouseleave="progressHoverTime = null"
          @click="seekVideo"
        >
          <div
            v-if="progressHoverTime !== null"
            class="progress-tooltip"
            :style="{ left: Math.min(97, Math.max(3, progressHoverPercent)) + '%' }"
          >
            {{ formatTime(progressHoverTime) }}
          </div>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: progress + '%' }" />
            <div class="bar-thumb" :style="{ left: progress + '%' }" />
          </div>
        </div>

        <div class="controls-row">
          <div class="left-group">
            <button class="ctrl-btn" @click="togglePlay" :title="playing ? '暂停' : '播放'">
              <svg v-if="playing" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <rect x="6" y="4" width="4" height="16" rx="1" />
                <rect x="14" y="4" width="4" height="16" rx="1" />
              </svg>
              <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M8 5v14l11-7z" />
              </svg>
            </button>
            <span class="time-display">
              {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
            </span>

            <!-- Volume control -->
            <div
              class="volume-control"
              @mouseenter="volumeOpen = true"
              @mouseleave="closeVolumeDelayed"
            >
              <button
                class="ctrl-btn"
                @click="toggleMute"
                :title="muted || volume === 0 ? '取消静音' : '静音'"
              >
                <span v-html="volumeIconSvg" class="volume-svg" />
              </button>
              <div
                class="volume-popup"
                v-show="volumeOpen"
                @mouseenter="volumeOpen = true"
                @mouseleave="volumeOpen = false"
                @click.stop
              >
                <input
                  type="range"
                  min="0"
                  max="100"
                  :value="Math.round(volume * 100)"
                  @input="onVolumeInput"
                  class="volume-slider"
                />
                <span class="volume-value">{{ Math.round(volume * 100) }}</span>
              </div>
            </div>
          </div>

          <div class="right-group">
            <!-- Speed -->
            <div
              class="dropdown"
              @mouseenter="openDropdown('speed')"
              @mouseleave="closeDropdown('speed')"
            >
              <button class="ctrl-btn">倍速 {{ playbackRate }}x</button>
              <div
                class="dropdown-menu"
                v-show="speedOpen"
                @mouseenter="openDropdown('speed')"
                @mouseleave="closeDropdown('speed')"
              >
                <div
                  v-for="s in speeds"
                  :key="s"
                  class="dropdown-item"
                  :class="{ active: playbackRate === s }"
                  @click="setSpeed(s)"
                >{{ s }}x</div>
              </div>
            </div>

            <!-- Quality -->
            <div
              class="dropdown"
              @mouseenter="openDropdown('quality')"
              @mouseleave="closeDropdown('quality')"
            >
              <button class="ctrl-btn">{{ currentQuality.label }}</button>
              <div
                class="dropdown-menu"
                v-show="qualityOpen"
                @mouseenter="openDropdown('quality')"
                @mouseleave="closeDropdown('quality')"
              >
                <div
                  v-for="q in qualities"
                  :key="q.value"
                  class="dropdown-item"
                  :class="{ active: currentQuality.value === q.value }"
                  @click="setQuality(q)"
                >{{ q.label }}</div>
              </div>
            </div>

            <!-- Danmu settings -->
            <div
              class="dropdown"
              @mouseenter="openDropdown('danmu')"
              @mouseleave="closeDropdown('danmu')"
            >
              <button class="ctrl-btn">弹幕设置</button>
              <div
                class="dropdown-menu danmu-settings"
                v-show="danmuOpen"
                @mouseenter="openDropdown('danmu')"
                @mouseleave="closeDropdown('danmu')"
                @click.stop
              >
                <div class="setting-row">
                  <span>不透明度</span>
                  <input
                    type="range"
                    min="10"
                    max="100"
                    v-model.number="danmuOpacity"
                    @input="emitDanmuSettings"
                  />
                  <span>{{ danmuOpacity }}%</span>
                </div>
                <div class="setting-row">
                  <span>字号</span>
                  <select v-model="danmuFontSize" @change="onDanmuStyleChange">
                    <option value="small">小</option>
                    <option value="normal">中</option>
                    <option value="large">大</option>
                  </select>
                </div>
                <div class="setting-row">
                  <span>显示区域</span>
                  <select v-model="danmuArea" @change="onDanmuStyleChange">
                    <option value="full">全屏</option>
                    <option value="top3">上 1/3</option>
                    <option value="mid3">中 1/3</option>
                    <option value="bottom3">下 1/3</option>
                  </select>
                </div>
                <div class="setting-row">
                  <span>弹幕速度</span>
                  <select v-model="danmuSpeed" @change="onDanmuStyleChange">
                    <option value="slow">慢</option>
                    <option value="normal">中</option>
                    <option value="fast">快</option>
                  </select>
                </div>
                <div class="setting-row">
                  <span>禁止重叠</span>
                  <button
                    type="button"
                    class="mini-toggle"
                    :class="{ on: avoidOverlap }"
                    @click="toggleAvoidOverlap"
                  >{{ avoidOverlap ? '开' : '关' }}</button>
                </div>
                <div class="setting-row">
                  <span>描边样式</span>
                  <select v-model="strokeStyleId" @change="onStrokeStyleChange">
                    <option value="none">无</option>
                    <option value="stroke">描边</option>
                    <option value="shadow">阴影</option>
                    <option value="both">描边+阴影</option>
                  </select>
                </div>
                <div class="setting-row">
                  <span>屏蔽类型</span>
                </div>
                <div class="shield-types">
                  <label v-for="item in barrageRenderList" :key="item.key" class="shield-type-item">
                    <input type="checkbox" v-model="item.value" @change="emitDanmuSettings" />
                    <span>{{ item.label }}</span>
                  </label>
                </div>
                <div class="setting-row">
                  <span>屏蔽词</span>
                </div>
                <div class="shield-words">
                  <span v-for="(word, index) in shieldWords" :key="word" class="shield-word-tag">
                    {{ word }}
                    <button type="button" class="shield-word-del" @click="removeShieldWord(index)">×</button>
                  </span>
                  <span v-if="shieldWords.length === 0" class="shield-word-empty">暂无屏蔽词</span>
                </div>
                <div class="shield-word-add">
                  <input
                    v-model="shieldWordInput"
                    type="text"
                    placeholder="输入屏蔽词后回车"
                    maxlength="20"
                    @keydown.enter="addShieldWord"
                  />
                  <button type="button" @click="addShieldWord">添加</button>
                </div>
                <button
                  class="ctrl-btn danmu-toggle-btn"
                  @click="toggleDanmu"
                >{{ danmuOn ? '关闭弹幕' : '开启弹幕' }}</button>
              </div>
            </div>

            <!-- 稍后再看 -->
            <button
              class="ctrl-btn"
              :title="isWatchLater ? '取消稍后再看' : '稍后再看'"
              @click="toggleWatchLater"
            >
              <svg viewBox="0 0 24 24" width="18" height="18" :fill="isWatchLater ? 'var(--bil-primary)' : 'currentColor'">
                <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
              </svg>
            </button>

            <!-- 画中画 (PIP) -->
            <button
              v-if="pipSupported"
              class="ctrl-btn"
              :title="isPip ? '退出画中画' : '画中画'"
              @click="togglePip"
            >
              <svg viewBox="0 0 24 24" width="18" height="18" :fill="isPip ? 'var(--bil-primary)' : 'currentColor'">
                <rect x="10" y="10" width="12" height="9" rx="2" stroke="currentColor" stroke-width="2" fill="none" />
                <path d="M2 5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v4H2V5z" fill="currentColor" opacity="0.3" />
                <rect x="2" y="3" width="20" height="6" rx="1" stroke="currentColor" stroke-width="2" fill="none" />
              </svg>
            </button>

            <!-- Fullscreen -->
            <button
              class="ctrl-btn"
              @click="toggleFullscreen"
              :title="isFullscreen ? '退出全屏' : '全屏'"
            >
              <svg v-if="isFullscreen" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Danmu input (below player) -->
    <div class="danmu-input-row">
      <div class="danmu-mode-group">
        <button
          v-for="m in danmuModes"
          :key="m.value"
          type="button"
          class="mode-btn"
          :class="{ active: danmuMode === m.value }"
          @click="danmuMode = m.value"
        >{{ m.label }}</button>
      </div>

      <button
        type="button"
        class="mode-btn jump-btn"
        title="空降弹幕：输入 [空降]MM:SS 发送，点击弹幕跳转到对应时间"
        @click="sendJumpDanmu"
      >⏱ 空降</button>
      <div class="danmu-color-picker" title="弹幕颜色">
        <span
          v-for="c in danmuColors"
          :key="c"
          class="color-dot"
          :class="{ active: danmuColor === c }"
          :style="{ background: c }"
          @click="danmuColor = c"
        />
      </div>
      <select v-model="danmuSendFontSize" class="danmu-font-select" title="弹幕字号">
        <option value="small">小</option>
        <option value="normal">中</option>
        <option value="large">大</option>
      </select>
      <input
        ref="danmuInputRef"
        v-model="danmuText"
        class="danmu-input"
        type="text"
        placeholder="发送弹幕，精彩马上出现"
        maxlength="100"
        @keydown.enter="sendDanmu"
      />
      <button class="danmu-send-btn" @click="sendDanmu">发送</button>
    </div>

    <!-- Danmu context menu (click barrage to open) -->
    <div
      v-if="danmuMenu.visible"
      class="danmu-context-menu"
      :style="{ left: danmuMenu.x + 'px', top: danmuMenu.y + 'px' }"
      @click.stop
    >
      <div class="danmu-menu-item" @click="copyDanmuText">复制弹幕</div>
      <div class="danmu-menu-item" @click="reportDanmu">举报弹幕</div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import Hls from 'hls.js'
import { loadDanmuApi, postDanmuApi } from '@/api/modules/danmu'
import BarrageRenderer from '@/libs/fly-barrage/fly-barrage.js'
import { reportVideoPlayOnlineApi } from '@/api/modules/video'
import { eventBus } from '@/utils/eventBus'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { submitReportApi } from '@/api/modules/report'

/* ============================
   Props
   ============================ */
const props = defineProps({
  src: { type: String, default: '' },
  poster: { type: String, default: '' },
  /** 视频ID，用于加载弹幕 */
  videoId: { type: String, default: '' },
  /** 视频文件ID，用于加载弹幕 */
  fileId: { type: String, default: '' },
  /** 下一集/下一个推荐视频信息 */
  nextVideo: { type: Object, default: null }
})

/* ============================
   Emits
   ============================ */
const emit = defineEmits([
  'danmuSettings',
  'timeUpdate',
  'nextVideo',
  'videoEnded'
])

/* ============================
   Core video refs & state
   ============================ */
const videoRef = ref(null)
const wrapperRef = ref(null)
const playing = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const progress = computed(() =>
  duration.value ? (currentTime.value / duration.value) * 100 : 0
)
const playbackRate = ref(1)
const showControls = ref(true)
const loading = ref(false)
const errorMsg = ref('')
let hideTimer = null

const posterSrc = computed(() => {
  const p = props.poster
  if (!p || p === 'null' || p === 'undefined' || (typeof p === 'object' && p !== null)) {
    return ''
  }
  return p
})

/* ============================
   Speed
   ============================ */
const speeds = [0.5, 0.75, 1, 1.25, 1.5, 2]

/* ============================
   Quality
   ============================ */
const qualities = [
  { label: '自动', value: 'auto' },
  { label: '1080P', value: '1080' },
  { label: '720P', value: '720' },
  { label: '480P', value: '480' },
  { label: '360P', value: '360' }
]
const currentQuality = ref(qualities[0])

/* ============================
   Dropdown management (with delay to prevent flicker)
   ============================ */
const speedOpen = ref(false)
const qualityOpen = ref(false)
const danmuOpen = ref(false)
const volumeOpen = ref(false)
const dropdownTimers = {}

function openDropdown(name) {
  if (dropdownTimers[name]) {
    clearTimeout(dropdownTimers[name])
    dropdownTimers[name] = null
  }
  if (name === 'speed') speedOpen.value = true
  else if (name === 'quality') qualityOpen.value = true
  else if (name === 'danmu') danmuOpen.value = true
}

function closeDropdown(name) {
  dropdownTimers[name] = setTimeout(() => {
    if (name === 'speed') speedOpen.value = false
    else if (name === 'quality') qualityOpen.value = false
    else if (name === 'danmu') danmuOpen.value = false
  }, 250)
}

function closeVolumeDelayed() {
  setTimeout(() => {
    volumeOpen.value = false
  }, 200)
}

/* ============================
   Fullscreen
   ============================ */
const isFullscreen = ref(false)

function onFullscreenChange() {
  isFullscreen.value = Boolean(document.fullscreenElement)
}

function toggleFullscreen() {
  const el = wrapperRef.value
  if (!el) return
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    el.requestFullscreen()
  }
}

/* ============================
   Double-click to fullscreen
   ============================ */
function onDblClick(e) {
  // Ignore double-clicks on controls
  const target = e.target
  if (target.closest('.controls-layer') || target.closest('.next-overlay')) {
    return
  }
  toggleFullscreen()
}

/* ============================
   PIP (画中画)
   ============================ */
const pipSupported = ref(false)
const isPip = ref(false)

async function togglePip() {
  const v = videoRef.value
  if (!v) return
  try {
    if (document.pictureInPictureElement === v) {
      await document.exitPictureInPicture()
    } else {
      await v.requestPictureInPicture()
    }
  } catch {
    // PIP not available or denied
  }
}

/* ============================
   Volume
   ============================ */
const volume = ref(0.8)
const muted = ref(false)

const volumeIconSvg = computed(() => {
  if (muted.value || volume.value === 0) {
    // Muted
    return '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.47 4.47 0 0 0 2.5-3.5zM14 3.23v2.06a7.007 7.007 0 0 1 0 13.42v2.06A9.01 9.01 0 0 0 14 3.23z"/><line x1="3" y1="3" x2="21" y2="21" stroke="currentColor" stroke-width="2"/></svg>'
  }
  if (volume.value < 0.3) {
    // Low
    return '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.47 4.47 0 0 0 2.5-3.5z"/></svg>'
  }
  if (volume.value < 0.7) {
    // Medium
    return '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.47 4.47 0 0 0 2.5-3.5zM14 3.23v2.06a7.007 7.007 0 0 1 0 13.42v2.06A9.01 9.01 0 0 0 14 3.23z"/></svg>'
  }
  // High
  return '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3A4.5 4.5 0 0 0 14 8.5v7a4.47 4.47 0 0 0 2.5-3.5zM14 3.23v2.06a7.007 7.007 0 0 1 0 13.42v2.06A9.01 9.01 0 0 0 14 3.23z"/></svg>'
})

function toggleMute() {
  const v = videoRef.value
  if (!v) return
  muted.value = !muted.value
  v.muted = muted.value
}

function onVolumeInput(e) {
  const val = Number(e.target.value) / 100
  volume.value = val
  if (videoRef.value) {
    videoRef.value.volume = val
  }
  if (val > 0 && muted.value) {
    muted.value = false
    if (videoRef.value) videoRef.value.muted = false
  }
}

// Sync video element volume/muted on mount and when video element is ready
function syncVolume() {
  const v = videoRef.value
  if (!v) return
  v.volume = volume.value
  v.muted = muted.value
}

/* ============================
   Progress hover tooltip
   ============================ */
const progressHoverTime = ref(null)
const progressHoverPercent = ref(0)

function onProgressHover(e) {
  const bar = e.currentTarget
  const rect = bar.getBoundingClientRect()
  const pct = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width))
  progressHoverPercent.value = pct * 100
  progressHoverTime.value = pct * duration.value
}

/* ============================
   Next video overlay
   ============================ */
const showNextOverlay = ref(false)
const countdown = ref(5)
let countdownTimer = null

function onVideoEnded() {
  if (props.nextVideo && props.nextVideo.videoId) {
    showNextOverlay.value = true
    countdown.value = 5
    startCountdown()
  }
  emit('videoEnded')
}

function startCountdown() {
  clearCountdown()
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearCountdown()
      goNextVideo()
    }
  }, 1000)
}

function clearCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

function goNextVideo() {
  clearCountdown()
  showNextOverlay.value = false
  emit('nextVideo', props.nextVideo)
}

function cancelNext() {
  clearCountdown()
  showNextOverlay.value = false
}

/* ============================
   稍后再看 (Watch Later)
   ============================ */
const WATCH_LATER_KEY = 'bil-watch-later'
const isWatchLater = ref(false)

function getWatchLaterList() {
  try {
    return JSON.parse(localStorage.getItem(WATCH_LATER_KEY) || '[]')
  } catch {
    return []
  }
}

function saveWatchLaterList(list) {
  localStorage.setItem(WATCH_LATER_KEY, JSON.stringify(list))
}

function checkWatchLater() {
  if (!props.videoId) {
    isWatchLater.value = false
    return
  }
  const id = String(props.videoId)
  const list = getWatchLaterList()
  isWatchLater.value = list.some(
    (item) => (typeof item === 'string' ? item : String(item?.videoId || '')) === id
  )
}

function toggleWatchLater() {
  if (!props.videoId) return
  const id = String(props.videoId)
  const list = getWatchLaterList()
  const idx = list.findIndex(
    (item) => String(item.videoId || item) === id
  )
  if (idx >= 0) {
    list.splice(idx, 1)
    isWatchLater.value = false
  } else {
    list.unshift({
      videoId: id,
      videoName: props.nextVideo?.videoName || '',
      savedAt: Date.now()
    })
    isWatchLater.value = true
  }
  saveWatchLaterList(list)
  eventBus.emit('watchLater:changed', list)
}

/* ============================
   Play / Pause / Speed / Quality
   ============================ */
/* ============================
	   Device ID (for play reporting)
	   ============================ */
const DEVICE_ID_KEY = 'bil-device-id'

function getDeviceId() {
  let id = localStorage.getItem(DEVICE_ID_KEY)
  if (!id) {
    id = 'web-' + Date.now().toString(36) + '-' + Math.random().toString(36).slice(2, 10)
    localStorage.setItem(DEVICE_ID_KEY, id)
  }
  return id
}

/* ============================
   Progress save / resume
   ============================ */
const PROGRESS_PREFIX = 'bil-video-progress-'
const PROGRESS_THROTTLE_MS = 10000
const showResumePrompt = ref(false)
const savedProgressTime = ref(0)
let lastProgressSave = 0
let hasReportedPlay = false

function getProgressKey(videoId) {
  return PROGRESS_PREFIX + (videoId || 'unknown')
}

function saveProgress(videoId, time) {
  if (!videoId) return
  try {
    const entry = { time: Math.floor(time), savedAt: Date.now() }
    localStorage.setItem(getProgressKey(videoId), JSON.stringify(entry))
  } catch { /* ignore */ }
}

function loadSavedProgress(videoId) {
  if (!videoId) return 0
  try {
    const raw = localStorage.getItem(getProgressKey(videoId))
    if (raw) {
      const entry = JSON.parse(raw)
      return Number(entry.time) || 0
    }
  } catch { /* ignore */ }
  return 0
}

function clearProgress(videoId) {
  if (!videoId) return
  try {
    localStorage.removeItem(getProgressKey(videoId))
  } catch { /* ignore */ }
}

function formatResumeTime(s) {
  return formatTime(s)
}

function onResumeAccept() {
  const v = videoRef.value
  if (v && savedProgressTime.value > 0) {
    v.currentTime = savedProgressTime.value
  }
  showResumePrompt.value = false
  nextTick(() => wrapperRef.value?.focus())
}

function onResumeDismiss() {
  showResumePrompt.value = false
  clearProgress(props.videoId)
}

function togglePlay() {
  const v = videoRef.value
  if (!v) return
  if (v.paused) {
    v.play().catch(() => {})
  } else {
    v.pause()
  }
}

function setSpeed(s) {
  playbackRate.value = s
  if (videoRef.value) {
    videoRef.value.playbackRate = s
  }
}

function setQuality(q) {
  currentQuality.value = q
  const v = videoRef.value
  if (!v) return
  if (v.hls) {
    if (q.value === 'auto') {
      v.hls.currentLevel = -1
    } else {
      const levels = v.hls.levels
      const targetHeight = parseInt(q.value)
      const idx = levels.findIndex((l) => l.height === targetHeight)
      if (idx >= 0) {
        v.hls.currentLevel = idx
      } else if (levels.length > 0) {
        let bestIdx = 0
        let bestDiff = Math.abs(levels[0].height - targetHeight)
        for (let i = 1; i < levels.length; i++) {
          const diff = Math.abs(levels[i].height - targetHeight)
          if (diff < bestDiff) {
            bestDiff = diff
            bestIdx = i
          }
        }
        v.hls.currentLevel = bestIdx
      }
    }
  }
}

/* ============================
   Seek
   ============================ */
function seekVideo(e) {
  const bar = e.currentTarget
  const rect = bar.getBoundingClientRect()
  const pct = (e.clientX - rect.left) / rect.width
  if (videoRef.value && duration.value) {
    videoRef.value.currentTime = Math.max(0, Math.min(pct * duration.value, duration.value))
  }
}

/* ============================
   Controls auto-hide
   ============================ */
function wakeControls() {
  showControls.value = true
  clearTimeout(hideTimer)
  hideTimer = setTimeout(() => {
    if (playing.value) showControls.value = false
  }, 3000)
}

/* ============================
   Video event handlers
   ============================ */
function onPlay() {
  playing.value = true
  barrageRenderer?.play()
  // Auto-focus danmu input when video starts playing
  nextTick(() => {
    danmuInputRef.value?.focus()
  })
  // Report play online (once per video mount)
  if (!hasReportedPlay && props.fileId) {
    hasReportedPlay = true
    reportVideoPlayOnlineApi({
      fileId: props.fileId,
      deviceId: getDeviceId()
    }).catch(() => {})
  }
}

function onPause() {
  playing.value = false
  barrageRenderer?.pause()
  showControls.value = true
  clearTimeout(hideTimer)
}

function onTimeUpdate() {
  if (videoRef.value) {
    currentTime.value = videoRef.value.currentTime
    emit('timeUpdate', currentTime.value)
    // Throttle progress save every 10 seconds
    const now = Date.now()
    if (now - lastProgressSave >= PROGRESS_THROTTLE_MS) {
      lastProgressSave = now
      saveProgress(props.videoId, currentTime.value)
    }
  }
}

function onLoaded() {
  if (videoRef.value) {
    duration.value = videoRef.value.duration || 0
    syncVolume()
    // Check for saved progress to resume
    const saved = loadSavedProgress(props.videoId)
    if (saved > 0 && saved < duration.value - 5) {
      savedProgressTime.value = saved
      showResumePrompt.value = true
    }
  }
}

function onVideoError() {
  const v = videoRef.value
  if (!v) return
  const err = v.error
  if (err) {
    switch (err.code) {
      case MediaError.MEDIA_ERR_ABORTED:
        errorMsg.value = '视频加载已中止'
        break
      case MediaError.MEDIA_ERR_NETWORK:
        errorMsg.value = '网络错误，无法加载视频'
        break
      case MediaError.MEDIA_ERR_DECODE:
        errorMsg.value = '视频解码失败，格式可能不受支持'
        break
      case MediaError.MEDIA_ERR_SRC_NOT_SUPPORTED:
        errorMsg.value = '视频源不受支持或不存在'
        break
      default:
        errorMsg.value = '视频加载失败'
    }
  } else {
    errorMsg.value = '视频加载失败'
  }
  loading.value = false
}

function retryLoad() {
  errorMsg.value = ''
  loading.value = true
  const v = videoRef.value
  if (v && props.src) {
    v.load()
    v.play().catch(() => {})
  }
}

/* ============================
   Keyboard shortcuts
   ============================ */
function onKeydown(e) {
  const tag = document.activeElement?.tagName?.toLowerCase()
  if (tag === 'input' || tag === 'textarea' || tag === 'select') return

  switch (e.code) {
    case 'Space':
      e.preventDefault()
      togglePlay()
      break
    case 'ArrowLeft':
      e.preventDefault()
      seekByOffset(-5)
      break
    case 'ArrowRight':
      e.preventDefault()
      seekByOffset(5)
      break
    case 'KeyF':
      e.preventDefault()
      toggleFullscreen()
      break
    case 'KeyM':
      e.preventDefault()
      toggleMute()
      break
  }
}

function seekByOffset(seconds) {
  const v = videoRef.value
  if (v) {
    v.currentTime = Math.max(0, Math.min(v.currentTime + seconds, duration.value || Infinity))
  }
}

/* ============================
   Format time
   ============================ */
function formatTime(s) {
  if (!s || !isFinite(s)) s = 0
  const hrs = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = Math.floor(s % 60)
  if (hrs > 0) {
    return `${hrs}:${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`
  }
  return `${m}:${String(sec).padStart(2, '0')}`
}

/* ==========================================================
   Danmu canvas rendering
   ========================================================== */
const danmuInputRef = ref(null)
const danmuText = ref('')

// Settings (reactive, same as controls)
const danmuOn = ref(true)
const danmuOpacity = ref(80)
const danmuFontSize = ref('normal')
const danmuArea = ref('full')
const danmuSpeed = ref('normal')

// Convert fontSize setting to pixel size
const fontSizeMap = { small: 16, normal: 20, large: 26 }

// Convert speed setting to pixels per second
const speedMap = { slow: 60, normal: 110, fast: 170 }

// Send options
const danmuModes = [
  { label: '滚动', value: 'scroll' },
  { label: '顶部', value: 'top' },
  { label: '底部', value: 'bottom' }
]
const danmuMode = ref('scroll')
const danmuSendFontSize = ref('normal')
const danmuColor = ref('#FFFFFF')
const danmuColors = [
  '#FFFFFF', '#FE0302', '#FF7204', '#FFAA02', '#FFFF00', '#A0EE00', '#00CD00',
  '#019899', '#4266BE', '#89D5FF', '#CC0273', '#9B9B9B', '#222222'
]

const userStore = useUserStore()

// Danmu context menu state
const danmuMenu = ref({ visible: false, x: 0, y: 0, barrage: null })

// Render options
const avoidOverlap = ref(true)
const strokeStyleId = ref('both')
const strokeStyleMap = {
  none: { strokeStyle: 'rgba(0, 0, 0, 0)', lineWidth: 1, shadowColor: 'rgba(0, 0, 0, 0)', shadowBlur: 0, shadowOffsetX: 0, shadowOffsetY: 0 },
  stroke: { strokeStyle: 'rgba(0, 0, 0, 0.6)', lineWidth: 3, shadowColor: 'rgba(0, 0, 0, 0)', shadowBlur: 0, shadowOffsetX: 0, shadowOffsetY: 0 },
  shadow: { strokeStyle: 'rgba(0, 0, 0, 0)', lineWidth: 1, shadowColor: 'rgba(0, 0, 0, 0.8)', shadowBlur: 4, shadowOffsetX: 1, shadowOffsetY: 1 },
  both: { strokeStyle: 'rgba(0, 0, 0, 0.35)', lineWidth: 3, shadowColor: 'rgba(0, 0, 0, 0.5)', shadowBlur: 2, shadowOffsetX: 1, shadowOffsetY: 1 }
}
const barrageRenderList = ref([
  { key: 'scroll', label: '滚动', value: true },
  { key: 'top', label: '顶部', value: true },
  { key: 'bottom', label: '底部', value: true },
  { key: 'color', label: '彩色', value: true }
])
const shieldWords = ref(JSON.parse(localStorage.getItem('bil_shield_words') || '[]'))
const shieldWordInput = ref('')

function addShieldWord() {
  const word = shieldWordInput.value.trim()
  if (word && !shieldWords.value.includes(word)) {
    shieldWords.value.push(word)
    localStorage.setItem('bil_shield_words', JSON.stringify(shieldWords.value))
    emitDanmuSettings()
  }
  shieldWordInput.value = ''
}

function removeShieldWord(index) {
  shieldWords.value.splice(index, 1)
  localStorage.setItem('bil_shield_words', JSON.stringify(shieldWords.value))
  emitDanmuSettings()
}

function toggleAvoidOverlap() {
  avoidOverlap.value = !avoidOverlap.value
  emitDanmuSettings()
}

function onStrokeStyleChange() {
  emitDanmuSettings()
}

// Fly Barrage renderer instance (created on mount)
let barrageRenderer = null
let resizeObserver = null
let resizeTimer = null

function getFontSizePx() {
  return fontSizeMap[danmuFontSize.value] || 20
}

/**
 * Convert a danmu record from the API into a fly-barrage option
 */
function mapDanmuToOptions(dm) {
  const mode = Number(dm.mode) || 1
  const barrageType = mode === 2 ? 'top' : mode === 3 ? 'bottom' : 'scroll'
  const isJump = Number(dm.danmuType) === 1
  // 空降目标时间（点击跳转用）；旧数据未存 jump_time 时回退用 time
  const jumpTime = Number(dm.jumpTime) || (Number(dm.danmuType) === 1 ? (Number(dm.time) || 0) : 0)
  // 弹幕出现时间：发送时刻的视频播放位置
  const showTime = Number(dm.time) || 0
  const option = {
    id: String(dm.danmuId || ('dm-' + Date.now() + '-' + Math.random())),
    barrageType,
    time: showTime,
    text: isJump ? '▶ ' + formatDanmuClock(jumpTime) : String(dm.text || ''),
    fontSize: isJump ? 20 : (Number(dm.fontSize) || getFontSizePx()),
    lineHeight: 1.2,
    color: isJump ? '#00C2A8' : (dm.color || '#ffffff'),
    prior: isJump ? true : (Number(dm.isPrior) === 1),
    addition: { danmuType: isJump ? 1 : 0, jumpTime }
  }
  if (barrageType !== 'scroll') {
    option.duration = 6000
  } else {
    option.duration = getScrollDanmuDuration()
  }
  return option
}
function initBarrageRenderer() {
  if (barrageRenderer || !wrapperRef.value || !videoRef.value) return
  barrageRenderer = new BarrageRenderer({
    container: wrapperRef.value,
    video: videoRef.value,
    barrages: [],
    renderConfig: getRenderConfig()
  })
  barrageRenderer.onBarrageClick = handleBarrageClick
}

/**
 * Build render config from current danmu settings
 */
function getRenderConfig() {
  const stroke = strokeStyleMap[strokeStyleId.value] || strokeStyleMap.both
  return {
    heightReduce: 36,
    speed: speedMap[danmuSpeed.value] || 110,
    opacity: danmuOpacity.value / 100,
    fontFamily: '"Microsoft YaHei", "PingFang SC", sans-serif',
    fontWeight: 'bold',
    renderRegion: danmuArea.value === 'top3' ? 0.33 : 1,
    avoidOverlap: avoidOverlap.value,
    minSpace: 10,
    strokeStyle: stroke.strokeStyle,
    lineWidth: stroke.lineWidth,
    shadowColor: stroke.shadowColor,
    shadowBlur: stroke.shadowBlur,
    shadowOffsetX: stroke.shadowOffsetX,
    shadowOffsetY: stroke.shadowOffsetY,
    barrageFilter: (barrage) => {
      const typeItem = barrageRenderList.value.find((item) => item.key === barrage.barrageType)
      if (typeItem && !typeItem.value) return false
      const colorItem = barrageRenderList.value.find((item) => item.key === 'color')
      if (colorItem && !colorItem.value) {
        const color = String(barrage.color || '').toUpperCase()
        if (color && color !== '#FFFFFF' && color !== '#FFF' && color !== '#000000') return false
      }
      if (shieldWords.value.some((word) => String(barrage.text || '').includes(word))) return false
      return true
    }
  }
}

/**
 * Danmu posted from the side panel — reload and re-render
 */
function onDanmuPosted(videoId) {
  if (String(videoId) === String(props.videoId)) {
    loadDanmu()
  }
}

/**
 * Load danmu from API and hand them to the renderer
 */
async function loadDanmu() {
  if (!barrageRenderer || !props.videoId) return
  try {
    const data = await loadDanmuApi({
      videoId: props.videoId,
      fileId: props.fileId || ''
    })
    const barrages = Array.isArray(data)
      ? data.map(mapDanmuToOptions).filter((b) => b.text)
      : []
    barrageRenderer.setBarrages(barrages)
    barrageRenderer.renderFrame()
  } catch {
    barrageRenderer.setBarrages([])
  }
}

/**
 * Apply danmu style changes (font size / area / speed) to the renderer
 */
function onDanmuStyleChange() {
  emitDanmuSettings()
  loadDanmu()
}
/* ============================
   Danmu settings emit
   ============================ */
function emitDanmuSettings() {
  if (barrageRenderer) {
    barrageRenderer.setRenderConfig(getRenderConfig())
    barrageRenderer.renderFrame()
  }
  emit('danmuSettings', {
    on: danmuOn.value,
    opacity: danmuOpacity.value / 100,
    fontSize: danmuFontSize.value,
    area: danmuArea.value,
    speed: danmuSpeed.value
  })
}

function toggleDanmu() {
  danmuOn.value = !danmuOn.value
  barrageRenderer?.switch(danmuOn.value)
  emitDanmuSettings()
}

/* ============================
   Send danmu
   ============================ */
async function sendDanmu() {
  const raw = danmuText.value.trim()
  if (!raw) return

  const timeMs = Math.floor(currentTime.value * 1000)
  const isJump = isValidJumpDanmu(raw)
  const jumpTime = isJump ? parseJumpTime(raw) : 0
  const modeNum = isJump ? 1 : (danmuMode.value === 'top' ? 2 : danmuMode.value === 'bottom' ? 3 : 1)

  const option = {
    id: 'local-' + Date.now() + '-' + Math.random(),
    barrageType: isJump ? 'scroll' : danmuMode.value,
    time: timeMs,
    text: isJump ? '▶ ' + formatDanmuClock(jumpTime) : raw,
    fontSize: isJump ? 20 : (fontSizeMap[danmuSendFontSize.value] || 20),
    lineHeight: 1.2,
    color: isJump ? '#00C2A8' : danmuColor.value,
    prior: true,
    addition: { danmuType: isJump ? 1 : 0, jumpTime: isJump ? jumpTime : 0 }
  }
  if (danmuMode.value !== 'scroll') {
    option.duration = 6000
  } else {
    option.duration = getScrollDanmuDuration()
  }

  // Optimistic: show immediately through the renderer
  if (danmuOn.value) {
    barrageRenderer?.send(option)
  }

  danmuText.value = ''

  // Post to API
  if (props.videoId) {
    try {
      await postDanmuApi({
        videoId: props.videoId,
        fileId: props.fileId || '',
        text: raw,
        mode: modeNum,
        color: isJump ? '#00C2A8' : danmuColor.value,
        fontSize: isJump ? 20 : (fontSizeMap[danmuSendFontSize.value] || 20),
        isPrior: 1,
        time: timeMs,
        danmuType: isJump ? 1 : 0,
        jumpTime: isJump ? jumpTime : 0
      })
      eventBus.emit('danmu:posted', props.videoId)
    } catch {
      // Danmu posted optimistically already
    }
  }
}

function formatDanmuClock(ms) {
  const totalSeconds = Math.max(0, Math.floor((ms || 0) / 1000))
  const m = Math.floor(totalSeconds / 60)
  const s = totalSeconds % 60
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
}

function parseJumpTime(text) {
  const match = String(text || '').match(/\[空降\](\d{1,2}):(\d{2})/)
  if (!match) return 0
  const minutes = parseInt(match[1], 10)
  const seconds = parseInt(match[2], 10)
  return (minutes * 60 + seconds) * 1000
}

function isValidJumpDanmu(text) {
  // 仅纯 [空降]MM:SS / [空降]M:SS 格式才判定为空降弹幕
  const match = /^\[空降\](\d{1,2}):(\d{2})$/.exec(String(text || '').trim())
  if (!match) return false
  const minutes = parseInt(match[1], 10)
  const seconds = parseInt(match[2], 10)
  // 秒必须是合法时间（00-59）
  if (seconds > 59) return false
  // 时间刻度必须在视频时长范围内
  const totalSeconds = minutes * 60 + seconds
  if (duration.value > 0 && totalSeconds > duration.value) return false
  return true
}

function getScrollDanmuDuration() {
  return { slow: 12000, normal: 8000, fast: 6000 }[danmuSpeed.value] || 8000
}

function sendJumpDanmu() {
  const current = danmuText.value.trim()
  danmuText.value = current.startsWith('[空降]') ? current : ('[空降]' + current)
  danmuInputRef.value?.focus()
}

function handleBarrageClick(barrage) {
  if (!barrage) return
  // 空降弹幕：点击直接跳转到对应时间
  if (barrage.addition?.danmuType === 1) {
    const jumpTime = Number(barrage.addition?.jumpTime) || Number(barrage.time) || 0
    if (jumpTime > 0 && videoRef.value) {
      videoRef.value.currentTime = jumpTime / 1000
    }
    return
  }
  // 普通弹幕：弹出复制 / 举报菜单
  const wrapperRect = wrapperRef.value?.getBoundingClientRect()
  const menuWidth = 120
  const menuHeight = 92
  let x = (wrapperRect?.left || 0) + barrage.left
  let y = (wrapperRect?.top || 0) + barrage.top
  x = Math.max(8, Math.min(x, window.innerWidth - menuWidth - 8))
  y = Math.max(8, Math.min(y, window.innerHeight - menuHeight - 8))
  danmuMenu.value = { visible: true, x, y, barrage }
}

function copyDanmuText() {
  const barrage = danmuMenu.value.barrage
  if (barrage?.text) {
    navigator.clipboard?.writeText(barrage.text)
    ElMessage.success('弹幕已复制')
  }
  closeDanmuMenu()
}

async function reportDanmu() {
  const barrage = danmuMenu.value.barrage
  closeDanmuMenu()
  if (!barrage) return
  if (!userStore.isLogin) {
    userStore.openLoginDialog()
    return
  }
  try {
    await submitReportApi({
      targetType: 3,
      targetId: String(barrage.id || ''),
      reasonType: 1,
      reasonDesc: '弹幕违规举报'
    })
    ElMessage.success('举报成功，感谢你的反馈')
  } catch {
    ElMessage.error('举报失败，请稍后再试')
  }
}

function closeDanmuMenu() {
  danmuMenu.value.visible = false
  danmuMenu.value.barrage = null
}

/* ============================
   Watchers
   ============================ */
let hlsInstance = null

function destroyHls() {
  if (hlsInstance) {
    hlsInstance.destroy()
    hlsInstance = null
  }
}

function initHls() {
  destroyHls()
  const video = videoRef.value
  const src = props.src
  if (!video || !src) return

  if (src.endsWith('.m3u8')) {
    if (Hls.isSupported()) {
      hlsInstance = new Hls({ enableWorker: true, lowLatencyMode: false })
      hlsInstance.loadSource(src)
      hlsInstance.attachMedia(video)
      hlsInstance.on(Hls.Events.MANIFEST_PARSED, () => {
        loading.value = false
      })
      hlsInstance.on(Hls.Events.ERROR, (_event, data) => {
        if (data.fatal) {
          errorMsg.value = '视频加载失败'
          destroyHls()
        }
      })
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = src
      loading.value = false
    }
  } else {
    video.src = src
    loading.value = false
  }
}

watch(() => props.src, () => {
  errorMsg.value = ''
  loading.value = true
  currentTime.value = 0
  duration.value = 0
  playing.value = false
  showNextOverlay.value = false
  showResumePrompt.value = false
  hasReportedPlay = false
  lastProgressSave = 0
  clearCountdown()
  barrageRenderer?.setBarrages([])
  initHls()
})

watch(() => [props.videoId, props.fileId], () => {
  loadDanmu()
  checkWatchLater()
  showResumePrompt.value = false
  hasReportedPlay = false
  lastProgressSave = 0
}, { immediate: false })


/* ============================
   Lifecycle
   ============================ */
onMounted(() => {
  // Check PIP support
  pipSupported.value = Boolean(
    document.pictureInPictureEnabled &&
    HTMLVideoElement.prototype.requestPictureInPicture
  )

  // Listen to fullscreen changes
  document.addEventListener('fullscreenchange', onFullscreenChange)

  // Sync volume
  const v = videoRef.value
  if (v) {
    if (v.readyState >= 1 && v.duration && isFinite(v.duration)) {
      onLoaded()
    }
    if (props.src && v.readyState < 3) {
      loading.value = true
    }
    syncVolume()
    wrapperRef.value?.focus()
  }

  // Create the fly-barrage renderer over the player wrapper
  initBarrageRenderer()

  // Observe wrapper size to keep the danmu canvas in sync
  if (wrapperRef.value) {
    resizeObserver = new ResizeObserver(() => {
      if (resizeTimer) clearTimeout(resizeTimer)
      resizeTimer = setTimeout(() => barrageRenderer?.resize(), 100)
    })
    resizeObserver.observe(wrapperRef.value)
  }

  // Listen for danmu posted from the side panel
  eventBus.on('danmu:posted', onDanmuPosted)

  // Load danmu and check watch later
  loadDanmu()
  checkWatchLater()
})

onBeforeUnmount(() => {
  clearTimeout(hideTimer)
  if (resizeTimer) clearTimeout(resizeTimer)
  clearCountdown()
  eventBus.off('danmu:posted', onDanmuPosted)
  destroyHls()

  // Clear all dropdown timers
  Object.values(dropdownTimers).forEach((t) => clearTimeout(t))

  document.removeEventListener('fullscreenchange', onFullscreenChange)

  if (resizeObserver) {
    resizeObserver.disconnect()
    resizeObserver = null
  }
})
</script>

<style scoped>
/* ============================
   Container (wraps player + danmu input)
   ============================ */
.video-player-container {
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* ============================
   Player wrapper
   ============================ */
.video-player-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #000;
  cursor: pointer;
  outline: none;
  overflow: hidden;
  flex-shrink: 0;
}

video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}



/* ============================
   Overlays
   ============================ */
.video-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 5;
  pointer-events: none;
}

.loading-overlay {
  background: rgba(0, 0, 0, 0.5);
}

.error-overlay {
  background: rgba(0, 0, 0, 0.75);
  pointer-events: auto;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(255, 255, 255, 0.25);
  border-top-color: var(--bil-primary, #00a1d6);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: #ccc;
  font-size: 13px;
}

.error-icon {
  font-size: 32px;
  color: #f44;
  font-weight: bold;
}

.error-text {
  color: #ccc;
  font-size: 14px;
  text-align: center;
  max-width: 80%;
}

.retry-btn {
  background: var(--bil-primary, #00a1d6);
  color: #fff;
  border: none;
  padding: 6px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  pointer-events: auto;
}

.retry-btn:hover {
  opacity: 0.85;
}

/* ============================
   Next video overlay
   ============================ */
.next-overlay {
  background: rgba(0, 0, 0, 0.85);
  pointer-events: auto;
  gap: 20px;
}

.next-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.next-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.next-cover {
  width: 120px;
  height: 68px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.next-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 300px;
}

.next-label {
  color: #aaa;
  font-size: 13px;
}

.next-title {
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.next-actions {
  display: flex;
  gap: 12px;
}

.next-skip-btn {
  background: var(--bil-primary, #00a1d6);
  color: #fff;
  border: none;
  padding: 8px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.2s;
}

.next-skip-btn:hover {
  opacity: 0.85;
}

.next-cancel-btn {
  background: rgba(255, 255, 255, 0.15);
  color: #ccc;
  border: 1px solid rgba(255, 255, 255, 0.25);
  padding: 8px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.next-cancel-btn:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* Resume overlay */
.resume-overlay {
  background: rgba(0, 0, 0, 0.75);
  pointer-events: auto;
  gap: 16px;
}

.resume-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.resume-text {
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}

.resume-actions {
  display: flex;
  gap: 12px;
}

.resume-accept-btn {
  background: var(--bil-primary, #00a1d6);
  color: #fff;
  border: none;
  padding: 8px 28px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.2s;
}

.resume-accept-btn:hover {
  opacity: 0.85;
}

.resume-dismiss-btn {
  background: rgba(255, 255, 255, 0.15);
  color: #ccc;
  border: 1px solid rgba(255, 255, 255, 0.25);
  padding: 8px 28px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.resume-dismiss-btn:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* ============================
   Controls layer
   ============================ */
.controls-layer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 16px 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 4;
}

.controls-layer.visible {
  opacity: 1;
}

/* ============================
   Progress bar
   ============================ */
.progress-bar {
  position: relative;
  padding: 8px 0;
  cursor: pointer;
}

.progress-tooltip {
  position: absolute;
  bottom: 22px;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.85);
  color: #fff;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  pointer-events: none;
  z-index: 3;
}

.progress-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 4px solid transparent;
  border-top-color: rgba(0, 0, 0, 0.85);
}

.bar-track {
  height: 6px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  position: relative;
  transition: height 0.15s;
}

.bar-fill {
  height: 100%;
  background: var(--bil-primary, #00a1d6);
  border-radius: 3px;
  position: relative;
  transition: width 0.1s linear;
}

.bar-thumb {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #fff;
  position: absolute;
  top: -4px;
  transform: translateX(-50%);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
  transition: transform 0.1s;
}

.progress-bar:hover .bar-thumb {
  transform: translateX(-50%) scale(1.2);
}

.progress-bar:hover .bar-track {
  height: 8px;
}

/* ============================
   Controls row
   ============================ */
.controls-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
}

.left-group,
.right-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ctrl-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 14px;
  padding: 4px 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
  line-height: 1.4;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.ctrl-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.ctrl-btn svg {
  display: block;
  flex-shrink: 0;
}

.time-display {
  color: #ccc;
  font-size: 14px;
  font-variant-numeric: tabular-nums;
  user-select: none;
}

/* ============================
   Volume control
   ============================ */
.volume-control {
  position: relative;
  display: flex;
  align-items: center;
}

.volume-popup {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-bottom: 8px;
  background: rgba(30, 30, 30, 0.95);
  border-radius: 8px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.volume-slider {
  width: 80px;
  height: 4px;
  -webkit-appearance: none;
  appearance: none;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  outline: none;
  cursor: pointer;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  cursor: pointer;
}

.volume-slider::-moz-range-thumb {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  border: none;
  cursor: pointer;
}

.volume-value {
  color: #ccc;
  font-size: 12px;
  min-width: 28px;
  text-align: center;
}

/* ============================
   Dropdowns
   ============================ */
.dropdown {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  bottom: 100%;
  right: 0;
  margin-bottom: 6px;
  background: rgba(30, 30, 30, 0.95);
  border-radius: 8px;
  padding: 6px 0;
  min-width: 100px;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.dropdown-item {
  padding: 6px 16px;
  font-size: 13px;
  color: #ccc;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, color 0.15s;
}

.dropdown-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.dropdown-item.active {
  color: var(--bil-primary, #00a1d6);
}

/* Danmu settings dropdown */
.danmu-settings {
  min-width: 260px;
  padding: 12px;
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 4px 0;
  color: #ccc;
  font-size: 13px;
}

.setting-row input[type='range'] {
  flex: 1;
  accent-color: var(--bil-primary, #00a1d6);
}

.setting-row select {
  background: #333;
  color: #fff;
  border: 1px solid #555;
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 12px;
}

.mini-toggle {
  height: 24px;
  padding: 0 12px;
  font-size: 12px;
  color: #aaa;
  background: #2a2a2a;
  border: 1px solid #555;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.mini-toggle.on {
  color: #fff;
  background: var(--bil-primary, #00a1d6);
  border-color: var(--bil-primary, #00a1d6);
}

.shield-types {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 2px 0 4px;
}

.shield-type-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #ccc;
  cursor: pointer;
}

.shield-type-item input {
  accent-color: var(--bil-primary, #00a1d6);
}

.shield-words {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 2px 0 4px;
}

.shield-word-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 6px 2px 8px;
  font-size: 12px;
  color: #eee;
  background: #333;
  border-radius: 4px;
}

.shield-word-del {
  border: none;
  background: none;
  color: #999;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  padding: 0 2px;
}

.shield-word-del:hover {
  color: var(--bil-pink);
}

.shield-word-empty {
  font-size: 12px;
  color: #777;
}

.shield-word-add {
  display: flex;
  gap: 6px;
  padding-bottom: 4px;
}

.shield-word-add input {
  flex: 1;
  min-width: 0;
  height: 28px;
  padding: 0 8px;
  font-size: 12px;
  color: #eee;
  background: #2a2a2a;
  border: 1px solid #555;
  border-radius: 4px;
  outline: none;
}

.shield-word-add input:focus {
  border-color: var(--bil-primary, #00a1d6);
}

.shield-word-add button {
  flex-shrink: 0;
  height: 28px;
  padding: 0 10px;
  font-size: 12px;
  color: #fff;
  background: var(--bil-primary, #00a1d6);
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.shield-word-add button:hover {
  opacity: 0.85;
}

.danmu-toggle-btn {
  margin-top: 8px;
  width: 100%;
  text-align: center;
}

/* ============================
   Volume SVG wrapper
   ============================ */
.volume-svg {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 0;
}

.volume-svg svg {
  display: block;
}

/* ============================
   Danmu input row (below player)
   ============================ */
.danmu-input-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 12px;
  margin-top: 8px;
  background: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.danmu-mode-group {
  display: flex;
  gap: 4px;
}

.mode-btn {
  height: 36px;
  padding: 0 12px;
  font-size: 13px;
  color: #666;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn:hover {
  color: var(--bil-primary, #00a1d6);
}

.mode-btn.active {
  color: #fff;
  background: var(--bil-primary, #00a1d6);
  border-color: var(--bil-primary, #00a1d6);
}

.danmu-color-picker {
  display: flex;
  align-items: center;
  gap: 5px;
  height: 36px;
  padding: 0 8px;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.color-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.15);
  box-sizing: border-box;
  transition: transform 0.15s;
}

.color-dot:hover {
  transform: scale(1.2);
}

.color-dot.active {
  outline: 2px solid var(--bil-primary, #00a1d6);
  outline-offset: 1px;
}

.danmu-font-select {
  height: 36px;
  padding: 0 6px;
  font-size: 13px;
  color: #333;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  cursor: pointer;
}

.danmu-input {
  flex: 1;
  min-width: 0;
  height: 36px;
  padding: 0 12px;
  font-size: 13px;
  color: #333;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
  transition: border-color 0.2s, background 0.2s;
}

.danmu-input:focus {
  border-color: var(--bil-primary, #00a1d6);
  background: #fff;
}

.danmu-input::placeholder {
  color: #bbb;
}

.danmu-send-btn {
  flex-shrink: 0;
  height: 36px;
  padding: 0 20px;
  font-size: 13px;
  color: #fff;
  background: var(--bil-primary, #00a1d6);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.danmu-send-btn:hover {
  opacity: 0.85;
}

.danmu-send-btn:active {
  opacity: 0.7;
}

.jump-btn {
  color: var(--bil-primary, #00a1d6);
  border-color: var(--bil-primary, #00a1d6);
  background: #f0faff;
}

.jump-btn:hover {
  color: #fff;
  background: var(--bil-primary, #00a1d6);
}

.danmu-context-menu {
  position: fixed;
  z-index: 9999;
  min-width: 120px;
  padding: 4px;
  background: #ffffff;
  border: 1px solid #e5e6eb;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.danmu-menu-item {
  padding: 8px 16px;
  font-size: 13px;
  color: #333;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s;
}

.danmu-menu-item:hover {
  background: #f5f6f7;
  color: var(--bil-primary, #00a1d6);
}
</style>
