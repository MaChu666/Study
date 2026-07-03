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
export const deleteMyVideoApi = (data) => request({ url: '/ucenter/deleteVideo', method: 'post', data })

// 关注列表
export const loadFocusListApi = (data) => request({ url: '/uhome/loadFocusList', method: 'post', data })
export const loadFansListApi = (data) => request({ url: '/uhome/loadFansList', method: 'post', data })

// 视频系列
export const loadVideoSeriesApi = (data) => request({ url: '/uhome/series/loadVideoSeries', method: 'post', data })
export const loadAllVideoApi = (data) => request({ url: '/uhome/series/loadAllVideo', method: 'post', data })
export const getVideoSeriesDetailApi = (data) => request({ url: '/series/getVideoSeriesDetail', method: 'post', data })
export const loadVideoSeriesWithVideoApi = (data) => request({ url: '/uhome/series/loadVideoSeriesWithVideo', method: 'post', data })

// 签到
export const signInApi = () => request({ url: '/signIn/signIn', method: 'post' })
export const getTodaySignApi = () => request({ url: '/signIn/getTodaySign', method: 'post' })

// 收藏夹
export const loadFavoriteFoldersApi = () => request({ url: '/favorite/loadFolders', method: 'post' })
export const loadFavoriteVideosApi = (data) => request({ url: '/favorite/loadVideos', method: 'post', data })

// 动态
export const loadDynamicsApi = (data) => request({ url: '/dynamic/loadDynamics', method: 'post', data })
export const postDynamicApi = (data) => request({ url: '/dynamic/postDynamic', method: 'post', data })

// Banner
export const loadActiveBannersApi = () => request({ url: '/banner/loadActiveBanners', method: 'post' })

// 动态点赞
export const likeDynamicApi = (data) => request({ url: '/dynamic/likeDynamic', method: 'post', data })
export const unlikeDynamicApi = (data) => request({ url: '/dynamic/unlikeDynamic', method: 'post', data })

// 私信
export const sendPrivateMessageApi = (data) => request({ url: '/message/sendPrivateMessage', method: 'post', data })
export const loadPrivateMessagesApi = (data) => request({ url: '/message/loadPrivateMessages', method: 'post', data })

// 搜索用户
export const searchUsersApi = (data) => request({ url: '/uhome/searchUsers', method: 'post', data })
