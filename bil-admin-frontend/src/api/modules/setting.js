import request from '@/api/request'

export const getSettingApi = () => request({ url: '/setting/getSetting', method: 'post' })
export const saveSettingApi = (data) => request({ url: '/setting/saveSetting', method: 'post', data })
