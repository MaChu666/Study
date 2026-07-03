import request from '@/api/request'

export const getCheckCodeApi = () => request({ url: '/account/checkCode', method: 'post' })
export const registerApi = (data) => request({ url: '/account/register', method: 'post', data })
export const loginApi = (data) => request({ url: '/account/login', method: 'post', data })
export const autoLoginApi = () => request({ url: '/account/autologin', method: 'post' })
export const logoutApi = () => request({ url: '/account/logout', method: 'post' })
export const getUserCountInfoApi = () => request({ url: '/account/getUserCountInfo', method: 'post' })

// 消息中心
export const getNoReadCountApi = () => request({ url: '/message/getNoReadCount', method: 'post' })
export const loadMessageApi = () => request({ url: '/message/loadMessage', method: 'post' })
export const delMessageApi = (messageId) => request({ url: '/message/delMessage', method: 'post', data: { messageId } })
export const getNoReadCountGroupApi = () => request({ url: '/message/getNoReadCountGroup', method: 'post' })
export const readAllMessageApi = () => request({ url: '/message/readAll', method: 'post' })
