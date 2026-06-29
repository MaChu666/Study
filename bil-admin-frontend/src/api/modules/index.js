import request from '@/api/request'

export const getActualTimeStatisticsInfoApi = () => request({ url: '/index/getActualTimeStatisticsInfo', method: 'post' })
export const getWeekStatisticsInfoApi = () => request({ url: '/index/getWeekStatisticsInfo', method: 'post' })
