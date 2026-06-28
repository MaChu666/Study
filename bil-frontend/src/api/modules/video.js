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
