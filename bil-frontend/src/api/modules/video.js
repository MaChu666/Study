import request from '@/api/request'

export const loadRecommendVideoApi = (signal) => request({ url: '/video/loadRecommendVideo', method: 'post', signal })
export const loadVideoApi = (data, signal) => request({ url: '/video/loadVideo', method: 'post', data, signal })
export const getVideoInfoApi = (data) => request({ url: '/video/getVideoInfo', method: 'post', data })
export const loadVideoPListApi = (data) => request({ url: '/video/loadVideoPList', method: 'post', data })
export const doActionApi = (data) => request({ url: '/userAction/doAction', method: 'post', data })
export const suggestVideoApi = (data) => request({ url: '/video/suggest', method: 'post', data })
export const searchVideoApi = (data) => request({ url: '/video/search', method: 'post', data })
export const getSearchKeywordTopApi = (signal) => request({ url: '/video/getSearchKeywordTop', method: 'post', signal })
export const getVideoRecommendApi = (data) => request({ url: '/video/getVideoRecommend', method: 'post', data })
export const loadHotVideoListApi = (signal) => request({ url: '/video/loadHotVideoList', method: 'post', signal })
export const reportVideoPlayOnlineApi = (data) => request({ url: '/video/reportVideoPlayOnline', method: 'post', data })
