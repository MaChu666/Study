import request from '@/api/request'

export const submitReportApi = (data) => request({ url: '/report/submitReport', method: 'post', data })
