import request from '@/api/request'

export const postDanmuApi = (data) => request({ url: '/danmu/postDanmu', method: 'post', data })
export const loadDanmuApi = (data) => request({ url: '/danmu/loadDanmu', method: 'post', data })
