import request from '@/api/request'

export const getCheckCodeApi = () => request({ url: '/account/checkCode', method: 'post' })
export const loginApi = (data) => request({ url: '/account/login', method: 'post', data })
export const logoutApi = () => request({ url: '/account/logout', method: 'post' })
