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
