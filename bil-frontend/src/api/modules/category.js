import request from '@/api/request'

export const loadAllCategoryApi = () => request({ url: '/category/loadAllCategory', method: 'post' })
