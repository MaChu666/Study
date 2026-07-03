import request from '@/api/request'

export const loadAllCategoryApi = (signal) => request({ url: '/category/loadAllCategory', method: 'post', signal })
