import request from '@/api/request'

export const loadUserApi = (data) => request({ url: '/user/loadUser', method: 'post', data })
export const changeStatusApi = (userId, status) => request({ url: '/user/changeStatus', method: 'post', data: { userId, status } })
