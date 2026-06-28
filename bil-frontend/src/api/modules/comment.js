import request from '@/api/request'

export const postCommentApi = (data) => request({ url: '/comment/postComment', method: 'post', data })
export const loadCommentApi = (data) => request({ url: '/comment/loadComment', method: 'post', data })
export const topCommentApi = (data) => request({ url: '/comment/topComment', method: 'post', data })
export const cancelTopCommentApi = (data) => request({ url: '/comment/cancelTopComment', method: 'post', data })
export const userDelCommentApi = (data) => request({ url: '/comment/userDelComment', method: 'post', data })
