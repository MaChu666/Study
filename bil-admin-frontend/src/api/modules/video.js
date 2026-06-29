import request from '@/api/request'

export const loadVideoListApi = (data) => request({ url: '/videoInfo/loadVideoList', method: 'post', data })
export const auditVideoApi = (data) => request({ url: '/videoInfo/auditVideo', method: 'post', data })
export const deleteVideoApi = (videoId) => request({ url: '/videoInfo/deleteVideo', method: 'post', data: { videoId } })
export const recommendVideoApi = (videoId) => request({ url: '/videoInfo/recommendVideo', method: 'post', data: { videoId } })
export const loadVideoPListApi = (videoId) => request({ url: '/videoInfo/loadVideoPList', method: 'post', data: { videoId } })
