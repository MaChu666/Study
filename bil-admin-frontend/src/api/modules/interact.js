import request from '@/api/request'

export const loadDanmuApi = (pageNo, videoNameFuzzy) => request({ url: '/interact/loadDanmu', method: 'post', data: { pageNo, videoNameFuzzy } })
export const delDanmuApi = (danmuId) => request({ url: '/interact/delDanmu', method: 'post', data: { danmuId } })
export const loadCommentApi = (pageNo, videoNameFuzzy) => request({ url: '/interact/loadComment', method: 'post', data: { pageNo, videoNameFuzzy } })
export const delCommentApi = (commentId) => request({ url: '/interact/delComment', method: 'post', data: { commentId } })
