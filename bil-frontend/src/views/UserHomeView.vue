<template>
  <section class="user-home">
    <!-- Skeleton loading state for initial profile load -->
    <template v-if="profileLoading">
      <div class="banner-wrap skeleton-banner" />
      <div class="avatar-area">
        <div class="avatar-wrapper skeleton-avatar" />
        <div class="avatar-side-info" style="padding-top: 48px">
          <div class="skeleton-line skeleton-line-name" />
          <div class="skeleton-line skeleton-line-sig" />
        </div>
      </div>
      <div class="stats-row skeleton-stats">
        <div v-for="n in 4" :key="n" class="stat-item">
          <div class="skeleton-line" style="width: 40px; height: 18px; margin: 0 auto 4px" />
          <div class="skeleton-line" style="width: 24px; height: 12px; margin: 0 auto" />
        </div>
      </div>
      <div class="tab-nav">
        <button v-for="tab in tabs" :key="tab.key" class="tab-btn" disabled>{{ tab.label }}</button>
      </div>
      <div class="tab-content">
        <div class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
      </div>
    </template>

    <!-- Banner -->
    <template v-else>
    <div class="banner-wrap">
      <div class="banner" :style="{ background: themeGradient }" />
      <div v-if="isOwnProfile" class="banner-edit-btn" title="更换主题" @click="showThemePicker = !showThemePicker">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M12 22C6.49 22 2 17.51 2 12S6.49 2 12 2s10 4.04 10 9c0 3.31-2.69 6-6 6h-1.77c-.28 0-.5.22-.5.5 0 .12.05.23.13.33.41.47.64 1.06.64 1.67A2.5 2.5 0 0 1 12 22zm0-18c-4.41 0-8 3.59-8 8s3.59 8 8 8c.28 0 .5-.22.5-.5a.54.54 0 0 0-.14-.35c-.41-.46-.63-1.05-.63-1.65a2.5 2.5 0 0 1 2.5-2.5H16c2.21 0 4-1.79 4-4 0-3.86-3.59-7-8-7z"/><circle cx="6.5" cy="11.5" r="1.5"/><circle cx="9.5" cy="7.5" r="1.5"/><circle cx="14.5" cy="7.5" r="1.5"/><circle cx="17.5" cy="11.5" r="1.5"/></svg>
      </div>
      <div v-if="showThemePicker" class="theme-picker">
        <div v-for="t in themeList" :key="t.themeId" class="theme-option" :class="{ active: (profile.theme || 1) === t.themeId }" :style="{ background: t.gradient }" @click="selectTheme(t.themeId)" />
      </div>
    </div>

    <!-- Avatar area -->
    <div class="avatar-area">
      <div class="avatar-wrapper" @click="isOwnProfile && editAvatar()">
        <img v-if="profile.avatar" :src="profile.avatar" class="avatar-img" alt="头像" />
        <div v-else class="avatar-fallback">{{ (profile.useName || 'B')[0] }}</div>
        <div v-if="isOwnProfile" class="avatar-edit-overlay">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z" />
          </svg>
        </div>
      </div>
      <div class="avatar-side-info">
        <div class="name-row">
          <template v-if="editingName">
            <input v-model="nameDraft" class="name-edit-input" maxlength="20" @keyup.enter="saveName" @keyup.escape="editingName = false" />
            <button class="name-save-btn" @click="saveName">保存</button>
            <button class="name-cancel-btn" @click="editingName = false">取消</button>
          </template>
          <template v-else>
            <h1 class="user-name">{{ profile.useName || 'VidVault UP主' }}</h1>
            <button v-if="isOwnProfile" class="edit-icon-btn" title="修改昵称" @click="startEditName">
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" /></svg>
            </button>
          </template>
          <span v-if="profile.level" class="level-badge" :class="'lv-' + profile.level">
            Lv{{ profile.level }}
          </span>
          <span v-if="profile.vip" class="vip-badge" title="大会员">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M20 2H4c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2.76 8.55L15.5 12l1.74 3.45c.18.37-.04.74-.44.74H13.5l-2.24 4.47c-.22.45-.9.45-1.12 0L8 16.19H4.7c-.4 0-.62-.37-.44-.74L6 12 4.26 8.55c-.18-.37.04-.74.44-.74H8l2.24-4.47c.22-.45.9-.45 1.12 0L13.5 7.81h3.3c.4 0 .62.37.44.74z" />
            </svg>
          </span>
          <!-- Follow / PM buttons for other users -->
          <button
            v-if="!isOwnProfile && typeof followStatus !== 'undefined'"
            class="follow-btn"
            :class="{ 'is-following': followStatus === 'following', 'is-mutual': followStatus === 'mutual' }"
            type="button"
            :disabled="followLoading"
            @mouseenter="followHover = true" @mouseleave="followHover = false"
            @click="toggleProfileFollow"
          >
            <template v-if="followLoading">...</template>
            <template v-else-if="followStatus === 'mutual'">已互粉</template>
            <template v-else-if="followStatus === 'following'">{{ followHover ? '取消关注' : '已关注' }}</template>
            <template v-else>关注</template>
          </button>
          <button
            v-if="!isOwnProfile && (followStatus === 'following' || followStatus === 'mutual')"
            class="pm-btn"
            type="button"
            title="私信"
            @click="showPmDialog = true"
          >
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
            私信
          </button>
        </div>
        <!-- Level progress bar (always show) -->
        <div class="level-progress-section">
          <div class="level-progress-header">
            <span class="level-progress-label">Lv.{{ profile.level || 1 }}</span>
            <span class="level-progress-text">{{ profile.exp || 0 }} / {{ levelRange.max }} exp</span>
          </div>
          <div class="level-progress-track">
            <div class="level-progress-fill" :style="{ width: levelPercent + '%' }" />
          </div>
          <div class="level-exp-hints">
            <span>登录 +5</span>
            <span>观看/点赞/投币 +10</span>
            <span>被赞/被投币 +1</span>
          </div>
        </div>
        <p v-if="profile.introduction" class="signature">
          {{ editingSignature ? '' : profile.introduction }}
          <template v-if="isOwnProfile && !editingSignature">
            <button class="edit-icon-btn" type="button" title="编辑签名" @click="startEditSignature">
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
              </svg>
            </button>
          </template>
        </p>
        <div v-if="isOwnProfile && editingSignature" class="signature-edit-row">
          <input
            ref="signatureInputRef"
            v-model="signatureDraft"
            class="signature-input"
            maxlength="100"
            placeholder="写下你的个性签名..."
            @keyup.enter="saveSignature"
            @keyup.escape="cancelEditSignature"
          />
          <button class="sig-save-btn" type="button" @click="saveSignature">保存</button>
          <button class="sig-cancel-btn" type="button" @click="cancelEditSignature">取消</button>
        </div>
        <div v-if="!profile.introduction && !isOwnProfile" class="signature muted">
          这个人还没有写简介
        </div>
        <div v-if="profile.joinTime" class="join-date">
          加入于 {{ formatJoinDate(profile.joinTime) }}
        </div>
      </div>
    </div>

    <!-- Stats row -->
    <div class="stats-row">
      <button class="stat-item" type="button" @click="openFollowList">
        <span class="stat-value">{{ formatCount(profile.followCount || 0) }}</span>
        <span class="stat-label">关注</span>
      </button>
      <button class="stat-item" type="button" @click="openFansList">
        <span class="stat-value">{{ formatCount(profile.fansCount || 0) }}</span>
        <span class="stat-label">粉丝</span>
      </button>
      <button class="stat-item" type="button">
        <span class="stat-value">{{ formatCount(profile.likeCount || 0) }}</span>
        <span class="stat-label">获赞</span>
      </button>
      <button class="stat-item" type="button">
        <span class="stat-value">{{ formatCount(profile.playCount || 0) }}</span>
        <span class="stat-label">播放</span>
      </button>
      <button class="stat-item" type="button">
        <span class="stat-value">{{ profile.currentCoinCount || 0 }}</span>
        <span class="stat-label">硬币</span>
      </button>
    </div>

    <!-- Tab navigation -->
    <div class="tab-nav">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ active: activeTab === tab.key }"
        type="button"
        @click="switchTab(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Tab content -->
    <div class="tab-content">
      <!-- 投稿 -->
      <template v-if="activeTab === 'videos'">
        <div v-if="tabLoading" class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else-if="tabError.videos" class="tab-error">
          <p>{{ tabError.videos }}</p>
          <button class="retry-btn" type="button" @click="retryTab('videos')">重试</button>
        </div>
        <div v-else-if="videos.length" class="video-section">
          <div class="video-grid">
            <VideoCard
              v-for="video in videos"
              :key="video.videoId"
              :video="video"
              @play="openVideo"
            />
          </div>
          <div v-if="hasMoreVideos" class="load-more-wrap">
            <button
              class="load-more-btn"
              type="button"
              :disabled="loadingMore"
              @click="loadMoreVideos"
            >
              <template v-if="loadingMore">
                <span class="spinner-small" />
                加载中...
              </template>
              <template v-else>
                加载更多
              </template>
            </button>
          </div>
        </div>
        <div v-else class="tab-empty">暂无内容</div>
      </template>

      <!-- 收藏 -->
      <template v-if="activeTab === 'collections'">
        <div v-if="tabLoading" class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else-if="tabError.collections" class="tab-error">
          <p>{{ tabError.collections }}</p>
          <button class="retry-btn" type="button" @click="retryTab('collections')">重试</button>
        </div>
        <div v-else-if="collections.length" class="video-section">
          <div class="video-grid">
            <VideoCard
              v-for="item in collections"
              :key="item.collectionId || item.videoId"
              :video="item"
              @play="openVideo"
            />
          </div>
        </div>
        <div v-else class="tab-empty">暂无内容</div>
      </template>

      <!-- 动态 -->
      <template v-if="activeTab === 'dynamics'">
        <!-- Dynamic post editor (own profile only) -->
        <DynamicPostEditor v-if="isOwnProfile" @posted="onDynamicPosted" />
        <div v-if="tabLoading" class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else-if="tabError.dynamics" class="tab-error">
          <p>{{ tabError.dynamics }}</p>
          <button class="retry-btn" type="button" @click="retryTab('dynamics')">重试</button>
        </div>
        <div v-else-if="dynamics.length" class="dynamics-list">
          <div
            v-for="d in dynamics"
            :key="d.dynamicId || d.id"
            class="dynamic-card"
          >
            <div class="dynamic-header">
              <div class="dynamic-user">
                <UserBadge
                  :user-id="d.userId"
                  :user-name="d.userName"
                  :avatar="d.userAvatar"
                  size="md"
                />
                <span class="dynamic-time">{{ formatTimeAgo(d.createTime) }}</span>
              </div>
            </div>
            <p class="dynamic-text">{{ d.content || d.text || '' }}</p>
            <div class="dynamic-actions">
              <button
                class="dynamic-like-btn"
                :class="{ liked: d.liked }"
                type="button"
                @click="toggleDynamicLike(d)"
              >
                <svg viewBox="0 0 24 24" width="14" height="14" :fill="d.liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                  <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3" />
                </svg>
                <span>{{ d.likeCount || 0 }}</span>
              </button>
            </div>
            <div v-if="d.images && d.images.length" class="dynamic-images">
              <img
                v-for="(img, idx) in d.images.slice(0, 9)"
                :key="idx"
                :src="img"
                class="dynamic-img"
                loading="lazy"
                alt="图片"
              />
            </div>
            <!-- Associated video -->
            <div v-if="d.videoId" class="dynamic-video-ref" @click="openVideo({ videoId: d.videoId, videoName: d.videoName, videoCover: d.videoCover })">
              <img v-if="d.videoCover" :src="d.videoCover" class="dv-cover" alt="" />
              <div class="dv-info">
                <span class="dv-title">{{ d.videoName || '关联视频' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="tab-empty">暂无内容</div>
      </template>

      <!-- 系列 -->
      <template v-if="activeTab === 'series'">
        <div v-if="tabLoading" class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else-if="tabError.series" class="tab-error">
          <p>{{ tabError.series }}</p>
          <button class="retry-btn" type="button" @click="retryTab('series')">重试</button>
        </div>
        <div v-else-if="seriesList.length" class="series-grid">
          <div
            v-for="s in seriesList"
            :key="s.seriesId || s.id"
            class="series-card"
            @click="openSeries(s)"
          >
            <div class="series-cover">
              <img v-if="s.cover" :src="s.cover" alt="" />
              <div v-else class="series-cover-placeholder" />
            </div>
            <div class="series-info">
              <h4 class="series-name">{{ s.name || s.seriesName || '未命名' }}</h4>
              <span class="series-meta">{{ s.videoCount || 0 }} 个视频</span>
            </div>
          </div>
        </div>
        <div v-else class="tab-empty">暂无内容</div>
      </template>

      <!-- 关于 -->
      <template v-if="activeTab === 'about'">
        <div v-if="tabLoading" class="tab-loading">
          <div class="spinner" />
          <p>加载中...</p>
        </div>
        <div v-else class="about-panel">
          <dl class="about-list">
            <div class="about-item">
              <dt>用户名</dt>
              <dd>{{ profile.useName || '-' }}</dd>
            </div>
            <div class="about-item">
              <dt>个性签名</dt>
              <dd v-if="isOwnProfile && editingField === 'personProfile'">
                <div class="field-edit-row">
                  <input
                    ref="fieldInputRef"
                    v-model="editFieldDraft"
                    class="signature-input"
                    maxlength="200"
                    placeholder="写下你的个性签名..."
                    @keyup.enter="saveFieldEdit"
                    @keyup.escape="cancelFieldEdit"
                  />
                  <button class="sig-save-btn" type="button" @click="saveFieldEdit">保存</button>
                  <button class="sig-cancel-btn" type="button" @click="cancelFieldEdit">取消</button>
                </div>
              </dd>
              <dd v-else class="about-dd-row">
                <span>{{ profile.personProfile || profile.introduction || '未设置' }}</span>
                <button v-if="isOwnProfile" class="edit-icon-btn" type="button" title="编辑个性签名" @click="startFieldEdit('personProfile', profile.personProfile || profile.introduction || '')">
                  <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                </button>
              </dd>
            </div>
            <div class="about-item">
              <dt>学校</dt>
              <dd v-if="isOwnProfile && editingField === 'school'">
                <div class="field-edit-row">
                  <input
                    ref="fieldInputRef"
                    v-model="editFieldDraft"
                    class="signature-input"
                    maxlength="100"
                    placeholder="请输入学校名称"
                    @keyup.enter="saveFieldEdit"
                    @keyup.escape="cancelFieldEdit"
                  />
                  <button class="sig-save-btn" type="button" @click="saveFieldEdit">保存</button>
                  <button class="sig-cancel-btn" type="button" @click="cancelFieldEdit">取消</button>
                </div>
              </dd>
              <dd v-else class="about-dd-row">
                <span>{{ profile.school || '未设置' }}</span>
                <button v-if="isOwnProfile" class="edit-icon-btn" type="button" title="编辑学校" @click="startFieldEdit('school', profile.school || '')">
                  <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                </button>
              </dd>
            </div>
            <div class="about-item">
              <dt>性别</dt>
              <dd v-if="isOwnProfile && editingField === 'sex'">
                <div class="field-edit-row">
                  <select
                    ref="fieldInputRef"
                    v-model="editFieldDraft"
                    class="field-select"
                    @keyup.escape="cancelFieldEdit"
                    @change="saveFieldEdit"
                  >
                    <option value="0">未知</option>
                    <option value="1">男</option>
                    <option value="2">女</option>
                  </select>
                </div>
              </dd>
              <dd v-else class="about-dd-row">
                <span>{{ sexLabel(profile.sex) }}</span>
                <button v-if="isOwnProfile" class="edit-icon-btn" type="button" title="编辑性别" @click="startFieldEdit('sex', String(profile.sex != null ? profile.sex : 0))">
                  <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                </button>
              </dd>
            </div>
            <div class="about-item">
              <dt>生日</dt>
              <dd v-if="isOwnProfile && editingField === 'birthday'">
                <div class="field-edit-row">
                  <input
                    ref="fieldInputRef"
                    v-model="editFieldDraft"
                    class="signature-input"
                    maxlength="20"
                    placeholder="如: 2000-01-01"
                    @keyup.enter="saveFieldEdit"
                    @keyup.escape="cancelFieldEdit"
                  />
                  <button class="sig-save-btn" type="button" @click="saveFieldEdit">保存</button>
                  <button class="sig-cancel-btn" type="button" @click="cancelFieldEdit">取消</button>
                </div>
              </dd>
              <dd v-else class="about-dd-row">
                <span>{{ profile.birthday || '未设置' }}</span>
                <button v-if="isOwnProfile" class="edit-icon-btn" type="button" title="编辑生日" @click="startFieldEdit('birthday', profile.birthday || '')">
                  <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                    <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                </button>
              </dd>
            </div>
            <div class="about-item">
              <dt>加入时间</dt>
              <dd>{{ formatJoinDate(profile.joinTime || profile.createTime) }}</dd>
            </div>
            <div class="about-item">
              <dt>等级</dt>
              <dd>
                <span v-if="profile.level" class="level-badge" :class="'lv-' + profile.level">
                  Lv{{ profile.level }}
                </span>
                <span v-else>-</span>
              </dd>
            </div>
          </dl>
        </div>
      </template>
    </div>

    <!-- Image Cropper Dialog for avatar -->
    <ImageCropperDialog v-model="showAvatarCropper" shape="circle" :aspectRatio="[1,1]" @success="onAvatarCropped" />

    <!-- Hidden file inputs for avatar / banner -->
    <input
      ref="avatarInputRef"
      type="file"
      accept="image/*"
      hidden
      @change="onAvatarFile"
    />
    <input
      ref="bannerInputRef"
      type="file"
      accept="image/*"
      hidden
      @change="onBannerFile"
    />
    </template>

    <!-- Follow / Fans list dialog -->
    <FollowListDialog
      v-model:visible="followListVisible"
      :type="followListType"
      :user-id="route.params.userId || ''"
    />

    <!-- Private message dialog -->
    <PrivateMessageDialog
      v-if="showPmDialog"
      v-model:visible="showPmDialog"
      :target-user-id="route.params.userId || ''"
      :target-name="profile.useName || ''"
    />
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getUserInfoApi,
  loadUserVideoListApi,
  loadFavoriteFoldersApi,
  loadFavoriteVideosApi,
  loadDynamicsApi,
  loadVideoSeriesWithVideoApi,
  updateUserInfoApi,
  saveThemeApi,
  loadThemesApi,
  focusApi,
  cancelFocusApi,
  loadFocusListApi,
  loadFansListApi,
  likeDynamicApi,
  unlikeDynamicApi,
  loadUserCollectionApi
} from '@/api/modules/user'
import { uploadImageApi } from '@/api/modules/file'
import { getVideoInfoApi } from '@/api/modules/video'
import VideoCard from '@/components/video/VideoCard.vue'
import DynamicPostEditor from '@/components/user/DynamicPostEditor.vue'
import FollowListDialog from '@/components/user/FollowListDialog.vue'
import PrivateMessageDialog from '@/components/user/PrivateMessageDialog.vue'
import UserBadge from '@/components/user/UserBadge.vue'
import ImageCropperDialog from '@/components/common/ImageCropperDialog.vue'
import { usePlayerStore } from '@/stores/player'
import { useUserStore } from '@/stores/user'
import { normalizeVideoList } from '@/utils/videoList'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const userStore = useUserStore()

const profileLoading = ref(true)
const profile = ref({})
const videos = ref([])
const collections = ref([])
const dynamics = ref([])
const seriesList = ref([])

const activeTab = ref('videos')
const tabLoading = ref(false)
const tabError = reactive({})
const loadedTabs = ref({})

// Pagination
const videoPageNo = ref(1)
const hasMoreVideos = ref(false)
const loadingMore = ref(false)

// Inline editing for about fields
const editingField = ref(null) // 'school' | 'sex' | 'birthday' | null
const editFieldDraft = ref('')

const editingName = ref(false)
const nameDraft = ref('')
const editingSignature = ref(false)
const signatureDraft = ref('')
const signatureInputRef = ref(null)
const avatarInputRef = ref(null)
const showAvatarCropper = ref(false)
const bannerInputRef = ref(null)
const fieldInputRef = ref(null)

const themeList = ref([])

const showThemePicker = ref(false)
const themeGradient = computed(() => {
  const themeId = profile.value.theme || 1
  const found = themeList.value.find(t => t.themeId === themeId)
  return found ? found.gradient : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
})

const tabs = [
  { key: 'videos', label: '投稿' },
  { key: 'collections', label: '收藏' },
  { key: 'dynamics', label: '动态' },
  { key: 'series', label: '系列' },
  { key: 'about', label: '关于' }
]

const isOwnProfile = computed(function () {
  var viewerId = userStore.profile && userStore.profile.userId
  var pageId = route.params.userId || viewerId || ''
  return Boolean(viewerId && pageId && String(viewerId) === String(pageId))
})

// Follow status for profile page
const followStatus = ref(undefined)
const followLoading = ref(false)
const followHover = ref(false)
const showPmDialog = ref(false)

// Follow/fans list dialog
const followListVisible = ref(false)
const followListType = ref('focus')

// Level system
const levelConfig = [
  { level: 1, min: 0, max: 100 },
  { level: 2, min: 101, max: 500 },
  { level: 3, min: 501, max: 1500 },
  { level: 4, min: 1501, max: 5000 },
  { level: 5, min: 5001, max: 15000 },
  { level: 6, min: 15001, max: Infinity }
]

const levelRange = computed(function () {
  var lv = profile.value.level || 1
  var cfg = levelConfig.find(function (c) { return c.level === lv })
  return cfg || { min: 0, max: 0 }
})

const levelPercent = computed(function () {
  var cfg = levelRange.value
  if (!cfg.max || cfg.max === Infinity || cfg.max <= cfg.min) return 0
  var exp = profile.value.exp || 0
  return Math.min(100, Math.max(0, ((exp - cfg.min) / (cfg.max - cfg.min)) * 100))
})

// --- Load user info ---

async function loadThemeList() {
      try {
        const data = await loadThemesApi()
        if (data && data.length > 0) {
          themeList.value = data
        }
      } catch {
        // keep defaults
      }
    }

    async function loadProfile() {
  var userId = route.params.userId || ''
  try {
    var data = await getUserInfoApi({ userId: userId })
    profile.value = data || {}
    // 同步更新右上角头像
    if (userStore.isLogin && data && data.userId === userStore.profile.userId) {
      userStore.profile.avatar = data.avatar
    }
  } catch (_e) {
    profile.value = {
      useName: 'VidVault UP主',
      introduction: '分享热爱的内容创作'
    }
  }
  profileLoading.value = false
  checkProfileFollowStatus()
}

// --- Tab loading ---

async function loadTabContent(tabKey) {
  if (loadedTabs.value[tabKey]) return
  tabLoading.value = true
  tabError[tabKey] = ''
  try {
    var userId = route.params.userId || ''
    if (tabKey === 'videos') {
      videoPageNo.value = 1
      var data = await loadUserVideoListApi({ userId: userId, pageNo: 1 })
      var list = normalizeVideoList(data)
      videos.value = list.length ? list : []
      hasMoreVideos.value = list.length >= 10
    } else if (tabKey === 'collections') {
      var collData = await loadUserCollectionApi({ userId: userId })
      var rawList = Array.isArray(collData) ? collData : (Array.isArray(collData && collData.list) ? collData.list : [])
      var enriched = []
      for (var i = 0; i < rawList.length; i++) {
        var item = rawList[i]
        if (!item || !item.videoId) continue
        var entry = { videoId: item.videoId, videoName: item.videoId, videoCover: '', playCount: 0, danmuCount: 0, createTime: item.createTime, collectionId: item.collectionId, userName: '' }
        try {
          var detail = await getVideoInfoApi({ videoId: item.videoId })
          if (detail) {
            entry.videoName = detail.videoName || item.videoId
            entry.videoCover = detail.videoCover || ''
            entry.playCount = detail.playCount || 0
            entry.danmuCount = detail.danmuCount || 0
            entry.userName = detail.userName || detail.useName || ''
          }
        } catch (_) {}
        enriched.push(entry)
      }
      collections.value = enriched
    } else if (tabKey === 'dynamics') {
      var dynData = await loadDynamicsApi({ userId: userId })
      dynamics.value = Array.isArray(dynData) ? dynData : (Array.isArray(dynData && dynData.list) ? dynData.list : [])
    } else if (tabKey === 'series') {
      var serData = await loadVideoSeriesWithVideoApi({ userId: userId })
      seriesList.value = Array.isArray(serData) ? serData : (Array.isArray(serData && serData.list) ? serData.list : [])
    } else if (tabKey === 'about') {
      // about tab just shows profile, no extra load needed
    }
    loadedTabs.value[tabKey] = true
  } catch (_e) {
    tabError[tabKey] = '加载失败，请稍后再试'
  } finally {
    tabLoading.value = false
  }
}

function retryTab(tabKey) {
  loadedTabs.value[tabKey] = false
  tabError[tabKey] = ''
  loadTabContent(tabKey)
}

// --- Load more videos ---

async function loadMoreVideos() {
  if (loadingMore.value) return
  loadingMore.value = true
  var userId = route.params.userId || ''
  var nextPage = videoPageNo.value + 1
  try {
    var data = await loadUserVideoListApi({ userId: userId, pageNo: nextPage })
    var list = normalizeVideoList(data)
    if (list.length) {
      videos.value = videos.value.concat(list)
      videoPageNo.value = nextPage
      hasMoreVideos.value = list.length >= 10
    } else {
      hasMoreVideos.value = false
    }
  } catch (_e) {
    ElMessage.error('加载更多视频失败')
  } finally {
    loadingMore.value = false
  }
}

function switchTab(key) {
  activeTab.value = key
  // Reset inline editing when switching tabs
  editingField.value = null
  editFieldDraft.value = ''
  loadTabContent(key)
}

// --- Edit profile ---

function startEditName() {
  nameDraft.value = profile.value.useName || ''
  editingName.value = true
  nextTick(function () {
    var el = document.querySelector('.name-edit-input')
    if (el) { el.focus(); el.select() }
  })
}

async function saveName() {
  var v = nameDraft.value.trim()
  if (!v) { ElMessage.warning('昵称不能为空'); return }
  try {
    await updateUserInfoApi({ userId: profile.value.userId, useName: v })
    profile.value.useName = v
    editingName.value = false
    ElMessage.success('昵称已更新')
  } catch { ElMessage.error('更新昵称失败') }
}

function startEditSignature() {
  signatureDraft.value = profile.value.introduction || ''
  editingSignature.value = true
  nextTick(function () {
    if (signatureInputRef.value) {
      signatureInputRef.value.focus()
    }
  })
}

function cancelEditSignature() {
  editingSignature.value = false
  signatureDraft.value = ''
}

async function saveSignature() {
  var text = (signatureDraft.value || '').trim()
  try {
    await updateUserInfoApi({
      userId: userStore.profile && userStore.profile.userId,
      introduction: text
    })
    profile.value.introduction = text
    ElMessage.success('签名已更新')
  } catch (_e) {
    ElMessage.error('更新签名失败')
  } finally {
    editingSignature.value = false
  }
}

// --- Inline edit for about fields ---

function sexLabel(val) {
  var v = Number(val)
  if (v === 1) return '男'
  if (v === 2) return '女'
  return '未知'
}

function startFieldEdit(fieldName, currentValue) {
  editingField.value = fieldName
  editFieldDraft.value = currentValue || ''
  nextTick(function () {
    if (fieldInputRef.value) {
      if (typeof fieldInputRef.value.focus === 'function') {
        fieldInputRef.value.focus()
      }
    }
  })
}

function cancelFieldEdit() {
  editingField.value = null
  editFieldDraft.value = ''
}

async function saveFieldEdit() {
  var field = editingField.value
  if (!field) return
  var value = (editFieldDraft.value || '').trim()
  if (value === '') {
    // Allow empty to clear the field, but for sex we need to keep it
    if (field === 'sex') {
      value = '0'
    }
  }
  try {
    var payload = { userId: userStore.profile && userStore.profile.userId }
    if (field === 'personProfile') {
      payload.personProfile = value
    } else if (field === 'school') {
      payload.school = value
    } else if (field === 'sex') {
      payload.sex = value
    } else if (field === 'birthday') {
      payload.birthday = value
    }
    await updateUserInfoApi(payload)
    // Update local profile
    if (field === 'personProfile') {
      profile.value.personProfile = value
      profile.value.introduction = value
    } else if (field === 'school') {
      profile.value.school = value
    } else if (field === 'sex') {
      profile.value.sex = Number(value)
    } else if (field === 'birthday') {
      profile.value.birthday = value
    }
    ElMessage.success('资料已更新')
  } catch (_e) {
    ElMessage.error('更新失败')
  } finally {
    editingField.value = null
    editFieldDraft.value = ''
  }
}

function editAvatar() {
  showAvatarCropper.value = true
}

function editBanner() {
  showThemePicker.value = !showThemePicker.value
}

async function selectTheme(themeId) {
  showThemePicker.value = false
  try {
    await saveThemeApi({ userId: profile.value.userId, theme: String(themeId) })
    profile.value.theme = themeId
    ElMessage.success('主题已更新')
  } catch { ElMessage.error('主题设置失败') }
}

async function uploadAndSet(path, fieldKey, successMsg) {
  return new Promise(function (resolve, reject) {
    var reader = new FileReader()
    reader.onload = async function (e) {
      try {
        var result = await uploadImageApi({ file: e.target.result, createThumbnail: 'false' })
        var filePath = (result && result.filePath) || (result && result.fileId)
        if (!filePath) {
          throw new Error('upload failed')
        }
        var payload = { userId: userStore.profile && userStore.profile.userId }
        payload[fieldKey] = filePath
        await updateUserInfoApi(payload)
        // Re-fetch to get the actual URL from server
        await loadProfile()
        ElMessage.success(successMsg)
        resolve()
      } catch (_err) {
        ElMessage.error('更新失败')
        reject(_err)
      }
    }
    reader.readAsDataURL(path)
  })
}

async function onAvatarCropped(filePath) {
    if (!filePath) {
      ElMessage.error("头像路径为空")
      return
    }
    try {
      // 乐观更新：先更新本地头像
      profile.value.avatar = filePath
      if (userStore.profile) userStore.profile.avatar = filePath
      
      var payload = {}
      payload.avatar = filePath
      await updateUserInfoApi(payload)
      await loadProfile()
      ElMessage.success("头像已更新")
    } catch (_err) {
      ElMessage.error("更新头像失败")
      // 回滚本地头像
      await loadProfile()
    }
  }

  // Legacy
  async function onAvatarFile(e) {
  var file = e.target && e.target.files && e.target.files[0]
  if (!file) return
  await uploadAndSet(file, 'avatar', '头像已更新')
  // Reset input to allow re-upload of same file
  if (avatarInputRef.value) avatarInputRef.value.value = ''
}

async function onBannerFile(e) {
  var file = e.target && e.target.files && e.target.files[0]
  if (!file) return
  await uploadAndSet(file, 'banner_image', '封面已更新')
  if (bannerInputRef.value) bannerInputRef.value.value = ''
}

// --- Helpers ---

function formatJoinDate(d) {
  if (!d) return ''
  return String(d).split(' ')[0]
}

function formatCount(value) {
  var count = Number(value || 0)
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return String(count)
}

function openVideo(video) {
  if (!video || !video.videoId) return
  playerStore.play(video)
  router.push({ name: 'video-detail', params: { videoId: video.videoId } })
}

function openFollowList() {
  followListType.value = 'focus'
  followListVisible.value = true
}

function openFansList() {
  followListType.value = 'fans'
  followListVisible.value = true
}

// --- Follow / Mutual / PM ---

async function checkProfileFollowStatus() {
  var viewerId = userStore.profile && userStore.profile.userId
  var pageId = route.params.userId || ''
  if (!viewerId || !pageId || String(viewerId) === String(pageId)) {
    followStatus.value = undefined
    return
  }
  try {
    var focusList = await loadFocusListApi({ userId: viewerId })
    var fansList = await loadFansListApi({ userId: viewerId })
    var iFollow = Array.isArray(focusList) && focusList.some(function (f) { return String(f.focusUserId) === String(pageId) })
    var theyFollowMe = Array.isArray(fansList) && fansList.some(function (f) { return String(f.userId) === String(pageId) })
    if (iFollow && theyFollowMe) {
      followStatus.value = 'mutual'
    } else if (iFollow) {
      followStatus.value = 'following'
    } else {
      followStatus.value = 'none'
    }
  } catch (_e) {
    followStatus.value = 'none'
  }
}

async function toggleProfileFollow() {
  var viewerId = userStore.profile && userStore.profile.userId
  var pageId = route.params.userId || ''
  if (!viewerId || !pageId) return
  followLoading.value = true
  try {
    if (followStatus.value === 'none') {
      await focusApi({ userId: viewerId, focusUserId: pageId })
      followStatus.value = 'following'
    } else {
      await cancelFocusApi({ userId: viewerId, focusUserId: pageId })
      followStatus.value = 'none'
      showPmDialog.value = false
    }
    // Re-check mutual status
    await checkProfileFollowStatus()
  } catch (_e) {
    // handled by interceptor
  } finally {
    followLoading.value = false
  }
}

// --- Dynamic like ---

async function toggleDynamicLike(d) {
  var dynamicId = d.dynamicId || d.id
  if (!dynamicId) return
  try {
    if (d.liked) {
      await unlikeDynamicApi({ dynamicId: dynamicId })
      d.liked = false
      d.likeCount = Math.max(0, (d.likeCount || 0) - 1)
    } else {
      await likeDynamicApi({ dynamicId: dynamicId })
      d.liked = true
      d.likeCount = (d.likeCount || 0) + 1
    }
  } catch (_e) {
    // handled by interceptor
  }
}

function onDynamicPosted() {
  // Reload dynamics
  loadedTabs.value['dynamics'] = false
  tabError.dynamics = ''
  loadTabContent('dynamics')
}

// --- Helpers ---

function formatTimeAgo(dateStr) {
  if (!dateStr) return ''
  var d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  var now = Date.now()
  var diff = now - d.getTime()
  var minutes = Math.floor(diff / 60000)
  var hours = Math.floor(diff / 3600000)
  var days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return minutes + '分钟前'
  if (hours < 24) return hours + '小时前'
  if (days < 30) return days + '天前'
  return d.toLocaleDateString()
}

function openUser(userId) {
  if (!userId) return
  router.push({ name: 'user-home', params: { userId: userId } })
}

function openSeries(s) {
  var sid = s.seriesId || s.id
  if (sid) {
    router.push({ name: 'series-detail', params: { seriesId: sid } })
  }
}

// --- Lifecycle ---

onMounted(async function () {
  loadThemeList()
  await loadProfile()
  // Load default tab content
  loadTabContent(activeTab.value)
})

// Re-load when route param changes
watch(
  function () { return route.params.userId },
  async function () {
    profileLoading.value = true
    loadedTabs.value = {}
    tabError.videos = ''
    tabError.collections = ''
    tabError.dynamics = ''
    tabError.series = ''
    activeTab.value = 'videos'
    videoPageNo.value = 1
    hasMoreVideos.value = false
    editingField.value = null
    editFieldDraft.value = ''
    await loadProfile()
    loadTabContent('videos')
  }
)
</script>

<style scoped>
.user-home {
  min-width: 0;
}

/* ---- Skeleton ---- */
.skeleton-banner {
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-avatar {
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-stats {
  display: flex;
  gap: 0;
  padding: 16px 20px;
  background: var(--bil-surface);
  border-radius: 12px;
}

.skeleton-line {
  height: 14px;
  border-radius: 6px;
  background: var(--bil-border);
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-bottom: 8px;
}

.skeleton-line-name {
  width: 160px;
  height: 22px;
}

.skeleton-line-sig {
  width: 280px;
}

@keyframes skeleton-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ---- Banner ---- */
.banner-wrap {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--bil-gradient);
}

.banner {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.banner-edit-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 0;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.banner-edit-btn:hover {
  background: rgba(0, 0, 0, 0.65);
}

.theme-picker {
  position: absolute;
  bottom: 12px;
  left: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  z-index: 5;
}
.theme-option {
  width: 32px; height: 32px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
  transition: transform 0.2s;
}
.theme-option:hover { transform: scale(1.15); }
.theme-option.active { border-color: #fff; transform: scale(1.15); }

/* ---- Avatar area ---- */
.avatar-area {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-top: -44px;
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

.avatar-wrapper {
  position: relative;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  border: 3px solid var(--bil-surface);
  overflow: hidden;
  cursor: default;
  flex-shrink: 0;
  background: var(--bil-gradient);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  color: #fff;
}

.avatar-edit-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  opacity: 0;
  transition: opacity 0.2s;
  cursor: pointer;
}

.avatar-wrapper:hover .avatar-edit-overlay {
  opacity: 1;
}

.avatar-side-info {
  flex: 1;
  min-width: 0;
  padding-top: 48px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.user-name {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
  color: var(--bil-text);
}

/* Level badge */
.level-badge {
  display: inline-flex;
  align-items: center;
  height: 20px;
  padding: 0 7px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.lv-0 { background: #9499a0; }
.lv-1 { background: #a0a4a8; }
.lv-2 { background: #56b887; }
.lv-3 { background: #00a1d6; }
.lv-4 { background: #f5a623; }
.lv-5 { background: #e85b52; }
.lv-6 { background: #c7883e; }

.vip-badge {
  display: flex;
  color: var(--bil-pink);
}

.signature {
  margin: 8px 0 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--bil-text);
  display: flex;
  align-items: center;
  gap: 6px;
}

.signature.muted {
  color: var(--bil-muted);
}

.edit-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: 0;
  border-radius: 50%;
  background: transparent;
  color: var(--bil-muted);
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.edit-icon-btn:hover {
  background: var(--bil-hover);
  color: var(--bil-pink);
}

.name-edit-input {
  font-size: 24px;
  font-weight: 700;
  padding: 4px 10px;
  border: 1px solid var(--bil-primary);
  border-radius: 8px;
  background: var(--bil-bg);
  color: var(--bil-text);
  width: 200px;
}
.name-save-btn, .name-cancel-btn {
  padding: 4px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
}
.name-save-btn { background: var(--bil-pink); color: #fff; }
.name-cancel-btn { background: var(--bil-hover); color: var(--bil-muted); }

.signature-edit-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.signature-input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--bil-primary);
  border-radius: 8px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 14px;
  outline: none;
}

.sig-save-btn {
  height: 32px;
  padding: 0 14px;
  border: 0;
  border-radius: 6px;
  background: var(--bil-pink);
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.sig-save-btn:hover {
  background: var(--bil-pink-hover);
}

.sig-cancel-btn {
  height: 32px;
  padding: 0 14px;
  border: 1px solid var(--bil-border);
  border-radius: 6px;
  background: var(--bil-surface);
  color: var(--bil-muted);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.sig-cancel-btn:hover {
  background: var(--bil-hover);
}

.join-date {
  margin-top: 8px;
  font-size: 12px;
  color: var(--bil-muted);
}

/* ---- Stats row ---- */
.stats-row {
  display: flex;
  gap: 0;
  margin: 20px 0 0;
  padding: 16px 20px;
  background: var(--bil-surface);
  border-radius: 12px;
  box-shadow: var(--bil-shadow);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  border: 0;
  background: transparent;
  cursor: pointer;
  padding: 4px;
  color: var(--bil-text);
  transition: background 0.15s;
  border-radius: 8px;
}

.stat-item:hover {
  background: var(--bil-hover);
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: var(--bil-muted);
}

/* ---- Tab navigation ---- */
.tab-nav {
  display: flex;
  gap: 0;
  margin-top: 16px;
  padding: 0;
  border-bottom: 1px solid var(--bil-border);
}

.tab-btn {
  flex: 0 0 auto;
  padding: 12px 24px;
  border: 0;
  border-bottom: 2px solid transparent;
  background: transparent;
  color: var(--bil-muted);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
}

.tab-btn:hover {
  color: var(--bil-text);
}

.tab-btn.active {
  color: var(--bil-pink);
  border-bottom-color: var(--bil-pink);
  font-weight: 600;
}

/* ---- Tab content ---- */
.tab-content {
  min-height: 200px;
  padding-top: 20px;
}

.tab-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  color: var(--bil-muted);
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--bil-border);
  border-top-color: var(--bil-pink);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin-bottom: 10px;
}

.spinner-small {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid var(--bil-border);
  border-top-color: var(--bil-pink);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  vertical-align: middle;
  margin-right: 6px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.tab-empty {
  padding: 48px 20px;
  text-align: center;
  color: var(--bil-muted);
  font-size: 14px;
}

.tab-error {
  padding: 48px 20px;
  text-align: center;
  color: var(--bil-muted);
  font-size: 14px;
}

.tab-error p {
  margin: 0 0 12px;
}

.retry-btn {
  padding: 6px 20px;
  border: 1px solid var(--bil-primary);
  border-radius: 6px;
  background: transparent;
  color: var(--bil-primary);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.retry-btn:hover {
  background: var(--bil-primary);
  color: #fff;
}

/* ---- Video grid ---- */
.video-section {
  display: contents;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px 16px;
}

.load-more-wrap {
  display: flex;
  justify-content: center;
  padding: 24px 0 8px;
}

.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 28px;
  border: 1px solid var(--bil-border);
  border-radius: 8px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}

.load-more-btn:hover:not(:disabled) {
  background: var(--bil-hover);
  border-color: var(--bil-pink);
  color: var(--bil-pink);
}

.load-more-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* ---- Folders section ---- */
.folders-section {
  display: grid;
  gap: 12px;
}

.folder-item {
  border-radius: 10px;
  overflow: hidden;
}

.folder-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.folder-card:hover,
.folder-card.expanded {
  background: var(--bil-surface);
}

.folder-cover {
  position: relative;
  width: 120px;
  aspect-ratio: 16 / 10;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bil-border);
}

.folder-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.folder-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bil-muted);
}

.folder-count {
  position: absolute;
  right: 6px;
  bottom: 6px;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.65);
  color: #fff;
  font-size: 11px;
}

.folder-info {
  flex: 1;
  min-width: 0;
}

.folder-name {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Folder expanded videos panel */
.folder-videos-panel {
  margin: 0 12px 8px;
  border-top: 1px solid var(--bil-border);
}

.folder-video-list {
  display: grid;
  gap: 2px;
  padding: 4px 0;
}

.folder-video-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
}

.folder-video-item:hover {
  background: var(--bil-hover);
}

.fv-cover {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.fv-cover-placeholder {
  width: 80px;
  height: 45px;
  border-radius: 4px;
  flex-shrink: 0;
  background: var(--bil-border);
}

.fv-info {
  flex: 1;
  min-width: 0;
}

.fv-title {
  margin: 0 0 2px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.fv-meta {
  font-size: 11px;
  color: var(--bil-muted);
}

/* ---- Dynamics list ---- */
.dynamics-list {
  display: grid;
  gap: 16px;
}

.dynamic-card {
  padding: 18px;
  background: var(--bil-surface);
  border-radius: 12px;
  box-shadow: var(--bil-shadow);
}

.dynamic-header {
  margin-bottom: 8px;
}

.dynamic-time {
  font-size: 12px;
  color: var(--bil-muted);
}

.dynamic-text {
  margin: 0 0 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.dynamic-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.dynamic-img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 8px;
}

/* Associated video in dynamic card */
.dynamic-video-ref {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  padding: 8px;
  border-radius: 8px;
  background: var(--bil-hover);
  cursor: pointer;
  transition: background 0.15s;
}

.dynamic-video-ref:hover {
  background: var(--bil-border);
}

.dv-cover {
  width: 80px;
  height: 45px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.dv-info {
  flex: 1;
  min-width: 0;
}

.dv-title {
  font-size: 13px;
  color: var(--bil-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ---- Series grid ---- */
.series-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 18px;
}

.series-card {
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.series-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--bil-shadow);
}

.series-cover {
  aspect-ratio: 16 / 9;
  border-radius: 10px;
  overflow: hidden;
  background: var(--bil-border);
}

.series-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.series-cover-placeholder {
  width: 100%;
  height: 100%;
  background: var(--bil-gradient);
  opacity: 0.5;
}

.series-info {
  padding: 8px 2px 0;
}

.series-name {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.series-meta {
  font-size: 12px;
  color: var(--bil-muted);
}

/* ---- Follow / PM buttons ---- */
.follow-btn {
  padding: 4px 14px; border: 1px solid var(--bil-pink);
  border-radius: 6px; background: var(--bil-pink); color: #fff;
  font-size: 13px; cursor: pointer; transition: all 0.2s; white-space: nowrap; flex-shrink: 0;
}
.follow-btn:hover { background: var(--bil-pink-hover); border-color: var(--bil-pink-hover); }
.follow-btn.is-following {
  background: transparent; color: var(--bil-text); border-color: var(--bil-border);
}
.follow-btn.is-mutual {
  background: transparent; color: var(--bil-pink); border-color: var(--bil-pink);
}
.follow-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.follow-btn.is-following:hover { border-color: var(--bil-pink); color: var(--bil-pink); }

.pm-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 4px 12px; border: 1px solid var(--bil-border);
  border-radius: 6px; background: transparent; color: var(--bil-text);
  font-size: 13px; cursor: pointer; transition: all 0.2s; flex-shrink: 0;
}
.pm-btn:hover { background: var(--bil-hover); border-color: var(--bil-primary); }

/* ---- Level progress bar ---- */
.level-progress-section {
  margin-top: 10px; max-width: 360px;
}
.level-progress-header {
  display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 4px;
}
.level-progress-label {
  font-size: 12px; font-weight: 700; color: var(--bil-text);
}
.level-progress-text {
  font-size: 11px; color: var(--bil-muted);
}
.level-progress-track {
  width: 100%; height: 6px; border-radius: 3px;
  background: var(--bil-hover); overflow: hidden;
}
.level-progress-fill {
  height: 100%; border-radius: 3px;
  background: var(--bil-gradient);
  transition: width 0.5s ease;
}
.level-exp-hints {
  display: flex; gap: 10px; flex-wrap: wrap; margin-top: 4px;
}
.level-exp-hints span {
  font-size: 10px; color: var(--bil-muted); opacity: 0.7;
}

/* ---- Dynamic user header ---- */
.dynamic-user {
  display: flex; align-items: center; gap: 8px;
}
.dynamic-time {
  font-size: 11px; color: var(--bil-muted); margin-left: auto;
}

/* ---- Dynamic actions ---- */
.dynamic-actions {
  display: flex; gap: 12px; margin-top: 10px;
}
.dynamic-like-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 4px 10px; border: 0; border-radius: 6px;
  background: transparent; color: var(--bil-muted); font-size: 13px;
  cursor: pointer; transition: color 0.15s, background 0.15s;
}
.dynamic-like-btn:hover { background: var(--bil-hover); }
.dynamic-like-btn.liked { color: var(--bil-primary); }

/* ---- About panel ---- */
.about-panel {
  max-width: 560px;
}

.about-list {
  margin: 0;
  padding: 0;
}

.about-item {
  display: flex;
  padding: 14px 0;
  border-bottom: 1px solid var(--bil-border);
}

.about-item:first-child {
  padding-top: 0;
}

.about-item dt {
  width: 100px;
  flex-shrink: 0;
  font-size: 14px;
  color: var(--bil-muted);
}

.about-item dd {
  margin: 0;
  font-size: 14px;
  color: var(--bil-text);
}

.about-actions {
  margin-top: 24px;
}

.edit-profile-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border: 1px solid var(--bil-primary);
  border-radius: 8px;
  background: transparent;
  color: var(--bil-primary);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.edit-profile-btn:hover {
  background: var(--bil-primary);
  color: #fff;
}

/* ---- About inline editing ---- */
.about-dd-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.field-edit-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.field-select {
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--bil-primary);
  border-radius: 8px;
  background: var(--bil-surface);
  color: var(--bil-text);
  font-size: 14px;
  outline: none;
  cursor: pointer;
  min-width: 120px;
}

/* ---- Collection cards ---- */
.collection-video-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background: var(--bil-surface);
  cursor: pointer;
  transition: background 0.15s;
}

.collection-video-card:hover {
  background: var(--bil-hover);
}

.collection-cover {
  width: 80px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: var(--bil-border);
  color: var(--bil-muted);
  flex-shrink: 0;
}

.collection-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.collection-video-id {
  font-size: 13px;
  font-weight: 500;
  color: var(--bil-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.collection-time {
  font-size: 11px;
  color: var(--bil-muted);
}

/* ---- Responsive ---- */
@media (max-width: 760px) {
  .banner-wrap {
    height: 140px;
    border-radius: 8px;
  }

  .avatar-area {
    flex-direction: column;
    align-items: center;
    margin-top: -36px;
    gap: 10px;
    padding: 0 12px;
  }

  .avatar-wrapper {
    width: 72px;
    height: 72px;
  }

  .avatar-side-info {
    padding-top: 0;
    text-align: center;
  }

  .name-row {
    justify-content: center;
  }

  .user-name {
    font-size: 18px;
  }

  .signature {
    justify-content: center;
  }

  .signature-edit-row {
    flex-wrap: wrap;
    justify-content: center;
  }

  .stats-row {
    padding: 12px 8px;
  }

  .stat-value {
    font-size: 16px;
  }

  .tab-btn {
    padding: 10px 16px;
    font-size: 13px;
  }

  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 16px 12px;
  }

  .about-item {
    flex-direction: column;
    gap: 4px;
  }

  .about-item dt {
    width: auto;
  }
}
</style>

